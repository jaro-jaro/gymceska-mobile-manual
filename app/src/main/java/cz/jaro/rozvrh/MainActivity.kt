package cz.jaro.rozvrh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.jaro.rozvrh.rozvrh.RozvrhScreen
import cz.jaro.rozvrh.rozvrh.Vjec
import cz.jaro.rozvrh.rozvrh.rememberResultLauncher
import cz.jaro.rozvrh.ui.theme.GymceskaTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val repo by inject<Repository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val tridy by repo.tridy.collectAsStateWithLifecycle(setOf())
            val uri by repo.uri.collectAsStateWithLifecycle()

            val launcher = rememberResultLauncher(ActivityResultContracts.OpenDocument())
            LaunchedEffect(uri) {
                if (!uri.isNullOrBlank()) repo.loadData()
                else launcher.launch(
                    input = arrayOf("application/json")
                ) { newUri ->
                    if (newUri == null) return@launch
                    repo.loadFile(newUri)
                    repo.loadData()
                }
            }

            if (tridy.isNotEmpty()) GymceskaTheme(
                useDarkTheme = isSystemInDarkTheme(),
                useDynamicColor = true,
            ) {
                Surface {
                    RozvrhScreen(tridy.map { Vjec.TridaVjec(it) })
                }
            }
        }
    }
}
