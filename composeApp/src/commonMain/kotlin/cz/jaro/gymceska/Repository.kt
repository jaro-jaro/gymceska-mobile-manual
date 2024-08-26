package cz.jaro.gymceska

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import cz.jaro.gymceska.rozvrh.TvorbaRozvrhu.vytvoritRozvrhPodleJinych
import cz.jaro.gymceska.rozvrh.TvorbaRozvrhu.vytvoritRucniRozvrh
import cz.jaro.gymceska.rozvrh.Tyden
import cz.jaro.gymceska.rozvrh.Vjec
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.extension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSettingsApi::class)
class Repository(
    private val settings: FlowSettings,
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val _rozvrhy = settings.getStringOrNullFlow("tridy").map {
        if (it == null) null
        else json.decodeFromString<List<String>>(it).associateWith { trida ->
            val rozvrh = settings.getStringOrNull("rozvrh_$trida")!!
            json.decodeFromString<Tyden>(rozvrh)
        }
    }
    val rozvrhy = _rozvrhy.stateIn(scope, SharingStarted.WhileSubscribed(5.seconds), null)

    val tridy = rozvrhy.filterNotNull().map { it.keys }.map {
        setOf(" ") + it
    }

    fun rozvrh(trida: String) = rozvrhy.value!![trida]

    fun mistnosti() = rozvrhy.value!!.flatMap { tyden ->
        tyden.value.flatMap { den ->
            den.flatMap { hodina ->
                hodina.map { bunka ->
                    bunka.ucebna
                }
            }
        }
    }.distinct()

    fun vyucujici() = rozvrhy.value!!.flatMap { tyden ->
        tyden.value.flatMap { den ->
            den.flatMap { hodina ->
                hodina.map { bunka ->
                    bunka.ucitel
                }
            }
        }
    }.distinct()

    suspend fun vyucujici2() = vyucujici().filter {
        vytvoritRozvrhPodleJinych(Vjec.VyucujiciVjec(it, it), this, tridy.first().drop(1).map { Vjec.TridaVjec(it) }).all { den ->
            den.isEmpty() || den.all { hodina ->
                hodina.isEmpty() || hodina.all { bunka ->
                    bunka.predmet.isBlank() || !bunka.predmet.startsWith("ST")
                }
            }
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun loadFile(file: PlatformFile) = scope.launch {
        val data = file.readBytes().decodeToString()
        if (file.extension == "json") saveData(data)
        else saveData(vytvoritRucniRozvrh(data))
    }

    suspend fun saveData(data: String?) {
        if (data == null) settings.remove("tridy")
        else {
            val rozvrhy = json.decodeFromString<Map<String, Tyden>>(data)
            rozvrhy.forEach { (t, _) ->
                settings.putString("rozvrh_$t", json.encodeToString(rozvrhy[t]))
            }
            settings.putString("tridy", json.encodeToString(rozvrhy.keys.toList()))
        }
    }
}