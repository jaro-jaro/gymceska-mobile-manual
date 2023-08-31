package cz.jaro.rozvrhmanual

import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.jaro.rozvrhmanual.rozvrh.RozvrhScreen
import cz.jaro.rozvrhmanual.rozvrh.Vjec
import org.koin.compose.getKoin

@Composable
fun MainSceeen(
    rozvrh: Boolean,
) {
    val repo = getKoin().get<Repository>()
    val tridy by repo.tridy.collectAsStateWithLifecycle(setOf())
    println(tridy)

    Surface {

        var data by remember { mutableStateOf("") }
        if (tridy.isEmpty()) TextField(
            value = data,
            onValueChange = {
                data = it
                repo.data(it)
            }
        )

        if (tridy.isNotEmpty()) RozvrhScreen(
            tridy.map { Vjec.TridaVjec(it) }
        )
    }
}