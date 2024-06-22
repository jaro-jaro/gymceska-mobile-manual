package cz.jaro.rozvrhmanual.rozvrh

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    val tabulka by viewModel.tabulka.collectAsStateWithLifecycle()
    val realVjec by viewModel.vjec.collectAsStateWithLifecycle()

    RozvrhScreen(
        tabulka = tabulka,
        vjec = realVjec,
        vybratRozvrh = viewModel::vybratRozvrh,
        najdiMiVolnouTridu = viewModel::najdiMivolnouTridu,
        najdiMiVolnehoUcitele = viewModel::najdiMiVolnehoUcitele,
        tridy = tridy,
        reset = viewModel::reset,
        mistnosti = viewModel.mistnosti,
        vyucujici = viewModel.vyucujici,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RozvrhScreen(
    tabulka: Tyden?,
    vjec: Vjec?,
    vybratRozvrh: (Vjec) -> Unit,
    najdiMiVolnouTridu: (Int, List<Int>, (String) -> Unit, (List<Vjec.MistnostVjec>?) -> Unit) -> Unit,
    najdiMiVolnehoUcitele: (Int, List<Int>, (String) -> Unit, (List<Vjec.VyucujiciVjec>?) -> Unit) -> Unit,
    tridy: List<Vjec.TridaVjec>,
    reset: () -> Unit,
    mistnosti: List<Vjec.MistnostVjec>,
    vyucujici: List<Vjec.VyucujiciVjec>,
) = Scaffold(
    topBar = {
        AppBar(
            najdiMiVolnouTridu = najdiMiVolnouTridu,
            najdiMiVolnehoUcitele = najdiMiVolnehoUcitele,
            tabulka = tabulka,
            vybratRozvrh = vybratRozvrh,
            reset = reset,
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
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Vybiratko(
                value = if (vjec is Vjec.TridaVjec) vjec else null,
                seznam = tridy.drop(1),
                onClick = { i, _ -> vybratRozvrh(tridy[i + 1]) },
                Modifier
                    .weight(1F)
                    .padding(horizontal = 4.dp),
                label = "Třídy",
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Vybiratko(
                value = if (vjec is Vjec.MistnostVjec) vjec else null,
                seznam = mistnosti.drop(1),
                onClick = { i, _ -> vybratRozvrh(mistnosti[i + 1]) },
                Modifier
                    .weight(1F)
                    .padding(horizontal = 4.dp),
                label = "Místnosti",
            )

            Vybiratko(
                value = if (vjec is Vjec.VyucujiciVjec) vjec else null,
                seznam = vyucujici.drop(1),
                onClick = { i, _ -> vybratRozvrh(vyucujici[i + 1]) },
                Modifier
                    .weight(1F)
                    .padding(horizontal = 4.dp),
                label = "Vyučující",
            )
        }

        if (tabulka == null) LinearProgressIndicator(Modifier.fillMaxWidth())
        else Tabulka(
            vjec = vjec,
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

