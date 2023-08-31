package cz.jaro.rozvrhmanual.rozvrh

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import cz.jaro.rozvrhmanual.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    najdiMiVolnouTridu: (Int, Int, (String) -> Unit, (List<Vjec.MistnostVjec>?) -> Unit) -> Unit,
) {
    val nacitani = stringResource(R.string.nacitani)
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
            Text(text = stringResource(R.string.rozvrh))
        },
        actions = {
            var volnaTridaNastaveniDialog by remember { mutableStateOf(false) }
            var volnaTridaDialog by remember { mutableStateOf(false) }
            var volneTridy by remember { mutableStateOf(emptyList<Vjec.MistnostVjec>()) }
            var denIndex by remember { mutableIntStateOf(0) }
            var hodinaIndex by remember { mutableIntStateOf(0) }

            if (volnaTridaDialog) AlertDialog(
                onDismissRequest = {
                    volnaTridaDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            volnaTridaDialog = false
                        }
                    ) {
                        Text(text = stringResource(android.R.string.ok))
                    }
                },
                dismissButton = {},
                title = {
                    Text(text = "Najdi mi volnou třídu")
                },
                text = {
                    LazyColumn {
                        item {
                            Text("Na škole jsou ${Seznamy.dny6Pad[denIndex]} ${Seznamy.hodiny4Pad[hodinaIndex]} volné tyto učebny:")
                        }
                        items(volneTridy.toList()) {
                            Text("${it.jmeno}, to je${it.napoveda}")
                        }
                    }
                }
            )

            if (volnaTridaNastaveniDialog) AlertDialog(
                onDismissRequest = {
                    volnaTridaNastaveniDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            nacitame = true
                            volnaTridaNastaveniDialog = false
                            podrobnostiNacitani = "Hledám volnou třídu"

                            najdiMiVolnouTridu(denIndex, hodinaIndex,
                                {
                                    podrobnostiNacitani = it
                                },
                                {
                                    if (it == null) {
                                        podrobnostiNacitani = "Nejste připojeni k internetu a nemáte staženou offline verzi všech rozvrhů tříd"
                                        return@najdiMiVolnouTridu
                                    }
                                    volneTridy = it
                                    volnaTridaDialog = true
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
                            volnaTridaNastaveniDialog = false
                        }
                    ) {
                        Text(text = "Zrušit")
                    }
                },
                title = {
                    Text(text = "Najdi mi volnou třídu")
                },
                text = {
                    Column {
                        Vybiratko(
                            seznam = Seznamy.dny1Pad,
                            aktualIndex = denIndex,
                        ) {
                            denIndex = it
                        }
                        Vybiratko(
                            seznam = Seznamy.hodiny1Pad,
                            aktualIndex = hodinaIndex,
                        ) {
                            hodinaIndex = it
                        }
                    }
                }
            )
            IconButton(
                onClick = {
                    volnaTridaNastaveniDialog = true
                }
            ) {
                Icon(Icons.Default.Search, "Najdi volnou učebnu")
            }
        }
    )
}