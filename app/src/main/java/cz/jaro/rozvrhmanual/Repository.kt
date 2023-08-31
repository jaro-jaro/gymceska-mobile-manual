package cz.jaro.rozvrhmanual

import cz.jaro.rozvrhmanual.rozvrh.Tyden
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class Repository {

    private val rozvrhy = MutableStateFlow(null as Map<String, Tyden>?)

    val tridy = rozvrhy.filterNotNull().map { it.keys }

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

    fun data(it: String) {
        rozvrhy.value = Json.decodeFromString<Map<String, Tyden>>(it)
    }
}
