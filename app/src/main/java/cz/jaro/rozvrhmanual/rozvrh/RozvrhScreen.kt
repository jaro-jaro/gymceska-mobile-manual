package cz.jaro.rozvrhmanual.rozvrh

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RozvrhScreen(
    tridy: List<Vjec.TridaVjec>
) {
    val viewModel = koinViewModel<RozvrhViewModel> {
        parametersOf(RozvrhViewModel.Parameters(tridy))
    }

    BackHandler {
        viewModel.reset()
    }

    val tabulka by viewModel.tabulka.collectAsStateWithLifecycle()
    val realVjec by viewModel.vjec.collectAsStateWithLifecycle()

    RozvrhScreen(
        tabulka = tabulka,
        vjec = realVjec,
        vybratRozvrh = viewModel::vybratRozvrh,
        najdiMiVolnouTridu = viewModel::najdiMivolnouTridu,
        tridy = tridy,
        mistnosti = viewModel.mistnosti,
        vyucujici = viewModel.vyucujici,
    )
}

@Composable
fun RozvrhScreen(
    tabulka: Tyden?,
    vjec: Vjec?,
    vybratRozvrh: (Vjec) -> Unit,
    najdiMiVolnouTridu: (Int, Int, (String) -> Unit, (List<Vjec.MistnostVjec>?) -> Unit) -> Unit,
    tridy: List<Vjec.TridaVjec>,
    mistnosti: List<Vjec.MistnostVjec>,
    vyucujici: List<Vjec.VyucujiciVjec>,
) = Scaffold(
    topBar = {
        AppBar(
            najdiMiVolnouTridu = najdiMiVolnouTridu,
        )
    }
) { paddingValues ->
    if (vjec == null || tridy.isEmpty()) LinearProgressIndicator(
        Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    )
    else Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            println(vjec to tridy)
            Vybiratko(
                seznam = tridy.map { it.jmeno },
                aktualIndex = if (vjec is Vjec.TridaVjec) tridy.indexOf(vjec) else 0,
            ) { i ->
                if (i == 0) return@Vybiratko
                vybratRozvrh(tridy[i])
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Vybiratko(
                seznam = mistnosti.map { it.jmeno },
                aktualIndex = if (vjec is Vjec.MistnostVjec) mistnosti.indexOf(vjec) else 0,
                nulaDisabled = true,
            ) { i ->
                if (i == 0) return@Vybiratko
                vybratRozvrh(mistnosti[i])
            }

            Spacer(modifier = Modifier.weight(1F))

            Vybiratko(
                seznam = vyucujici.map { it.jmeno },
                aktualIndex = if (vjec is Vjec.VyucujiciVjec) vyucujici.indexOf(vjec) else 0,
                nulaDisabled = true,
            ) { i ->
                if (i == 0) return@Vybiratko
                vybratRozvrh(vyucujici[i])
            }
        }

        if (tabulka == null) LinearProgressIndicator(Modifier.fillMaxWidth())
        else Tabulka(
            tabulka = tabulka,
            kliklNaNeco = { vjec ->
                vybratRozvrh(vjec)
            },
            tridy = tridy,
            mistnosti = mistnosti,
            vyucujici = vyucujici,
        )
    }
}

context(ColumnScope)
@Composable
private fun Tabulka(
    tabulka: Tyden,
    kliklNaNeco: (vjec: Vjec) -> Unit,
    tridy: List<Vjec.TridaVjec>,
    mistnosti: List<Vjec.MistnostVjec>,
    vyucujici: List<Vjec.VyucujiciVjec>,
) {
    if (tabulka.isEmpty()) return

    val horScrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .border(1.dp, MaterialTheme.colorScheme.secondary)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1F)
                    .border(1.dp, MaterialTheme.colorScheme.secondary)
                    .size(60.dp, 60.dp)
            )
        }

        Row(
            modifier = Modifier
                .horizontalScroll(horScrollState)
                .border(1.dp, MaterialTheme.colorScheme.secondary)
        ) {
            tabulka.first().drop(1).forEach { cisloHodiny ->

                Box(
                    modifier = Modifier
                        .aspectRatio(1F)
                        .border(1.dp, MaterialTheme.colorScheme.secondary)
                        .size(120.dp, 60.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Text(
                            text = cisloHodiny.first().predmet,
                            modifier = Modifier
                                .padding(all = 8.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Text(
                            text = cisloHodiny.first().ucitel,
                            modifier = Modifier
                                .padding(all = 8.dp)
                        )
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        item {
            Row {
                val maxy = tabulka.map { radek -> radek.maxOf { hodina -> hodina.size } }

                Column(
                    Modifier.horizontalScroll(rememberScrollState())
                ) {
                    tabulka.drop(1).map { it.first() }.forEachIndexed { i, den ->
                        Column(
                            modifier = Modifier
                                .border(1.dp, MaterialTheme.colorScheme.secondary)
                        ) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1F)
                                    .border(1.dp, MaterialTheme.colorScheme.secondary)
                                    .size(60.dp, 120.dp * maxy[i + 1])
                            ) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = den.first().predmet,
                                        modifier = Modifier
                                            .padding(all = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    Modifier.horizontalScroll(horScrollState)
                ) {
                    tabulka.drop(1).forEachIndexed { i, radek ->
                        Row {
                            radek.drop(1).forEach { hodina ->
                                Column(
                                    modifier = Modifier
                                        .border(1.dp, MaterialTheme.colorScheme.secondary)
                                ) {
                                    hodina.forEach { bunka ->
                                        bunka.Compose(
                                            bunekVHodine = hodina.size,
                                            maxBunekDne = maxy[i + 1],
                                            kliklNaNeco = kliklNaNeco,
                                            tridy = tridy,
                                            mistnosti = mistnosti,
                                            vyucujici = vyucujici,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Vybiratko(
    seznam: List<String>,
    aktualIndex: Int,
    modifier: Modifier = Modifier,
    nulaDisabled: Boolean = false,
    poklik: (i: Int) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(all = 8.dp)
    ) {
        var vidimMenu by remember { mutableStateOf(false) }

        DropdownMenu(
            expanded = vidimMenu,
            onDismissRequest = { vidimMenu = false }
        ) {
            seznam.forEachIndexed { i, x ->

                DropdownMenuItem(
                    text = { Text(x) },
                    onClick = {
                        vidimMenu = false
                        poklik(i)
                    },
                    enabled = !(nulaDisabled && i == 0)
                )
            }
        }

        OutlinedButton(
            onClick = {
                vidimMenu = true
            }
        ) {
            Text(seznam[aktualIndex])
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "Vyberte",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}