package cz.jaro.rozvrhmanual

import cz.jaro.rozvrhmanual.rozvrh.TvorbaRozvrhu.vytvoritRozvrhPodleJinych
import cz.jaro.rozvrhmanual.rozvrh.Tyden
import cz.jaro.rozvrhmanual.rozvrh.Vjec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class Repository {

    private val rozvrhy = MutableStateFlow(null as Map<String, Tyden>?)

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
        vytvoritRozvrhPodleJinych(Vjec.VyucujiciVjec(it, it), this, tridy.first().map { Vjec.TridaVjec(it) }).all { den ->
            den.isEmpty() || den.all { hodina ->
                hodina.isEmpty() || hodina.all { bunka ->
                    bunka.predmet.isBlank() || bunka.predmet.startsWith("TS")
                }
            }
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun data(it: String) {
        if (it.isBlank()) return
        rozvrhy.value = json.decodeFromString<Map<String, Tyden>>(it)
    }
}
