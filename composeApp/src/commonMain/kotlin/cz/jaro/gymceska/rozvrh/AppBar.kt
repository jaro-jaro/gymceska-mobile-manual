package cz.jaro.gymceska.rozvrh

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    najdiMiVolnouTridu: (Int, List<Int>, (String) -> Unit, (List<Vjec.MistnostVjec>?) -> Unit) -> Unit,
    najdiMiVolnehoUcitele: (Int, List<Int>, (String) -> Unit, (List<Vjec.VyucujiciVjec>?) -> Unit) -> Unit,
    tabulka: Tyden?,
    vybratRozvrh: (Vjec) -> Unit,
    reset: () -> Unit,
) {
    val nacitani = "Načítání"
    var nacitame by remember { mutableStateOf(false) }
    var podrobnostiNacitani by remember { mutableStateOf(nacitani) }

    if (nacitame) AlertDialog(
        onDismissRequest = {
            nacitame = false
        },
        confirmButton = {},
        title = {
            Text(text = podrobnostiNacitani)
        },
        text = {
            CircularProgressIndicator()
        },
    )

    TopAppBar(
        title = {
            Text(text = "Rozvrh")
        },
        actions = {
            var najdiMiNastaveniDialog by remember { mutableStateOf(false) }
            var najdiMiDialog by remember { mutableStateOf(false) }
            var volneTridy by remember { mutableStateOf(emptyList<Vjec.MistnostVjec>()) }
            var volniUcitele by remember { mutableStateOf(emptyList<Vjec.VyucujiciVjec>()) }
            var ucebna by remember { mutableStateOf(true) }
            var denIndex by remember {
                mutableIntStateOf(
                    LocalDate.now().dayOfWeek.value
                        .let { if (LocalTime.now() > LocalTime.of(15, 45)) it + 1 else it }
                        .let { if (it > 5) 1 else it } - 1
                )
            }
            var hodinaIndexy by remember(tabulka) {
                mutableStateOf(
                    listOf(
                        tabulka
                            ?.get(0)
                            ?.drop(1)
                            ?.indexOfFirst {
                                try {
                                    val cas = it.first().ucitel.split(" - ").first()
                                    val hm = cas.split(":")
                                    LocalTime.now() < LocalTime.of(hm[0].toInt(), hm[1].toInt()) + 10.minutes.toJavaDuration()
                                } catch (e: Exception) {
                                    false
                                }
                            }
                            ?.coerceAtLeast(0)
                            ?: 0
                    )
                )
            }

            if (najdiMiDialog) AlertDialog(
                onDismissRequest = {
                    najdiMiDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            najdiMiDialog = false
                        }
                    ) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {},
                title = {
                    Text(text = "Najdi mi ${if (ucebna) "volnou učebnu" else "volného učitele"}")
                },
                text = {
                    LazyColumn {
                        if (ucebna) item {
                            Text("Na škole jsou ${Seznamy.dny4Pad[denIndex]} ${hodinaIndexy.joinToString(" a ") { "$it." }} hodinu volné tyto učebny:")
                        }
                        if (ucebna) items(volneTridy.toList()) {
                            Text(it.jmeno, Modifier.clickable {
                                najdiMiDialog = false
                                vybratRozvrh(it)
                            })
                        }
                        if (!ucebna) item {
                            Text("Na škole jsou ${Seznamy.dny4Pad[denIndex]} ${hodinaIndexy.joinToString(" a ") { "$it." }} hodinu volní tito učitelé:")
                        }
                        if (!ucebna) items(volniUcitele.toList()) {
                            Text(it.jmeno, Modifier.clickable {
                                najdiMiDialog = false
                                vybratRozvrh(it)
                            })
                        }
                    }
                }
            )

            if (najdiMiNastaveniDialog) AlertDialog(
                onDismissRequest = {
                    najdiMiNastaveniDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            nacitame = true
                            najdiMiNastaveniDialog = false
                            podrobnostiNacitani = "Hledám..."

                            if (ucebna) najdiMiVolnouTridu(denIndex, hodinaIndexy,
                                {
                                    podrobnostiNacitani = it
                                },
                                {
                                    if (it == null) {
                                        podrobnostiNacitani = "Nejste připojeni k internetu a nemáte staženou offline verzi všech rozvrhů tříd"
                                        return@najdiMiVolnouTridu
                                    }
                                    volneTridy = it
                                    najdiMiDialog = true
                                    nacitame = false
                                }
                            )
                            else najdiMiVolnehoUcitele(
                                denIndex, hodinaIndexy,
                                {
                                    podrobnostiNacitani = it
                                },
                                {
                                    if (it == null) {
                                        podrobnostiNacitani = "Nejste připojeni k internetu a nemáte staženou offline verzi všech rozvrhů tříd"
                                        return@najdiMiVolnehoUcitele
                                    }
                                    volniUcitele = it
                                    najdiMiDialog = true
                                    nacitame = false
                                }
                            )
                        }
                    ) {
                        Text(text = "Vyhledat")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            najdiMiNastaveniDialog = false
                        }
                    ) {
                        Text(text = "Zrušit")
                    }
                },
                title = {
                    Text(text = "Najdi mi")
                },
                text = {
                    Column {
                        Vybiratko(
                            seznam = listOf("volnou učebnu", "volného učitele"),
                            index = if (ucebna) 0 else 1,
                            onClick = { i, _ ->
                                ucebna = i == 0
                            },
                            label = "Najdi mi",
                            zaskrtavatko = { false },
                        )
                        Vybiratko(
                            seznam = Seznamy.dny4Pad,
                            index = denIndex,
                            onClick = { i, _ ->
                                denIndex = i
                            },
                            zaskrtavatko = { false },
                        )
                        Vybiratko(
                            value = "${hodinaIndexy.joinToString(" a ") { "$it." }} hodinu",
                            seznam = Seznamy.hodiny4Pad,
                            onClick = { i, _ ->
                                if (i in hodinaIndexy) hodinaIndexy -= i
                                else if (i !in hodinaIndexy) hodinaIndexy += i
                            },
                            zaskrtavatko = {
                                Seznamy.hodiny4Pad.indexOf(it) in hodinaIndexy
                            },
                            zavirat = false
                        )
                    }
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                )
            )
            IconButton(
                onClick = {
                    reset()
                }
            ) {
                Icon(Icons.Default.RestartAlt, "Resetovat")
            }
            IconButton(
                onClick = {
                    najdiMiNastaveniDialog = true
                }
            ) {
                Icon(Icons.Default.Search, "Najdi mi")
            }
        }
    )
}

