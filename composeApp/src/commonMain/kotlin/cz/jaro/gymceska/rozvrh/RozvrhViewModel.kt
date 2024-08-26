package cz.jaro.gymceska.rozvrh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.jaro.gymceska.Repository
import cz.jaro.gymceska.rozvrh.TvorbaRozvrhu.vytvoritRozvrhPodleJinych
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class RozvrhViewModel(
    private val repo: Repository,
    params: Parameters
) : ViewModel() {

    data class Parameters(
        val tridy: List<Vjec.TridaVjec>,
    )

    private val tridy = params.tridy.drop(1)

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
    private val vyucujici2 = viewModelScope.async(Dispatchers.IO) { repo.vyucujici2() }

    val tabulka = vjec.map { vjec ->
        when (vjec) {
            is Vjec.TridaVjec -> repo.rozvrh(
                trida = vjec.jmeno,
            )

            is Vjec.VyucujiciVjec,
            is Vjec.MistnostVjec
                -> vytvoritRozvrhPodleJinych(
                vjec = vjec,
                repo = repo,
                tridy = tridy,
            )

            is Vjec.DenVjec,
            is Vjec.HodinaVjec
                -> TvorbaRozvrhu.vytvoritSpecialniRozvrh(
                vjec = vjec,
                repo = repo,
                tridy = tridy,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), null)

    fun najdiMivolnouTridu(
        den: Int,
        hodiny: List<Int>,
        progress: (String) -> Unit,
        onComplete: (List<Vjec.MistnostVjec>?) -> Unit
    ) {
        viewModelScope.launch {
            val plneTridy = tridy.flatMap { trida ->
                progress("Prohledávám třídu\n${trida.zkratka}")
                repo.rozvrh(trida.jmeno)!!.drop(1)[den].drop(1).slice(hodiny).flatMap { hodina ->
                    hodina.map { bunka ->
                        bunka.ucebna
                    }
                }
            }
            progress("Už to skoro je")

            val vysledek = mistnosti.filter { it.zkratka !in plneTridy }

            onComplete(vysledek)
        }
    }

    fun najdiMiVolnehoUcitele(
        den: Int,
        hodiny: List<Int>,
        progress: (String) -> Unit,
        onComplete: (List<Vjec.VyucujiciVjec>?) -> Unit
    ) {
        viewModelScope.launch {
            val zaneprazdneniUcitele = tridy.flatMap { trida ->
                progress("Prohledávám třídu\n${trida.zkratka}")
                repo.rozvrh(trida.jmeno)!!.drop(1)[den].drop(1).slice(hodiny).flatMap { hodina ->
                    hodina.map { bunka ->
                        bunka.ucitel
                    }
                }
            }
            progress("Už to skoro je")

            val vysledek = vyucujici.filter { it.zkratka !in zaneprazdneniUcitele && it.zkratka in vyucujici2.await() }.toMutableList()

            onComplete(vysledek)
        }
    }

    fun reset() = viewModelScope.launch { repo.saveData(null) }
}
