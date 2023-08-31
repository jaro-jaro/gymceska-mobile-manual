package cz.jaro.rozvrhmanual.rozvrh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jaro.rozvrhmanual.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class RozvrhViewModel(
    private val repo: Repository,
    @InjectedParam params: Parameters
) : ViewModel() {

    data class Parameters(
        val tridy: List<Vjec.TridaVjec>,
    )

    private val tridy = params.tridy

    private val _vjec = MutableStateFlow<Vjec>(tridy.first())
    val vjec = _vjec.asStateFlow()

    fun vybratRozvrh(vjec: Vjec) {
        _vjec.value = vjec
    }

    val mistnosti = repo.mistnosti().map {
        Vjec.MistnostVjec(it)
    }
    val vyucujici = repo.vyucujici().map {
        Vjec.VyucujiciVjec(it, it)
    }

    val tabulka = vjec.map { vjec ->

        when (vjec) {
            is Vjec.TridaVjec -> withContext(Dispatchers.IO) Nacitani@{
                repo.rozvrh(vjec.jmeno)
            }

            else -> vytvoritRozvrhPodleJinych(
                vjec = vjec,
                repo = repo
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), null)

    fun najdiMivolnouTridu(den: Int, hodina: Int, progress: (String) -> Unit, onComplete: (List<Vjec.MistnostVjec>) -> Unit) {
        viewModelScope.launch {
            val plneTridy = tridy.flatMap { trida ->
                progress("Prohledávám třídu\n${trida.zkratka}")
                repo.rozvrh(trida.jmeno)!!.drop(1)[den].drop(1)[hodina].map { bunka ->
                    bunka.ucebna
                }
            }
            progress("Už to skoro je")

            onComplete(mistnosti.filter { it.jmeno !in plneTridy })
        }
    }

    suspend fun vytvoritRozvrhPodleJinych(
        vjec: Vjec,
        repo: Repository,
    ): Tyden = withContext(Dispatchers.IO) {
        if (vjec is Vjec.TridaVjec) return@withContext emptyList<Den>()

        val seznamNazvu = tridy

        val novaTabulka = MutableList(6) { MutableList(17) { mutableListOf<Bunka>() } }

        seznamNazvu.forEach { trida ->

            val rozvrhTridy = repo.rozvrh(trida.jmeno)!!

            rozvrhTridy.forEachIndexed { i, den ->
                den.forEachIndexed { j, hodina ->
                    hodina.forEach bunka@{ bunka ->
                        if (bunka.ucitel.isEmpty() || bunka.predmet.isEmpty()) {
                            return@bunka
                        }
                        if (i == 0 || j == 0) {
                            novaTabulka[i][j] += bunka
                            return@bunka
                        }
                        val zajimavaVec = when (vjec) {
                            is Vjec.VyucujiciVjec -> bunka.ucitel.split(",").first()
                            is Vjec.MistnostVjec -> bunka.ucebna
                            else -> throw IllegalArgumentException()
                        }
                        if (zajimavaVec == vjec.zkratka) {
                            novaTabulka[i][j] += bunka.copy(tridaSkupina = "${trida.zkratka} ${bunka.tridaSkupina}".trim())
                            return@bunka
                        }
                    }
                }
            }
        }
        novaTabulka.forEachIndexed { i, den ->
            den.forEachIndexed { j, hodina ->
                hodina.ifEmpty {
                    novaTabulka[i][j] += Bunka.prazdna
                }
            }
        }
        novaTabulka
    }

    fun reset() = repo.data("{}")
}