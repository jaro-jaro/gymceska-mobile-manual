package cz.jaro.rozvrhmanual

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import cz.jaro.rozvrhmanual.ui.theme.AppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val repo by inject<Repository>()

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rozvrh = intent.getBooleanExtra("rozvrh", false) || intent.getStringExtra("rozvrh") == "true"
        val ukoly = intent.getBooleanExtra("ukoly", false) || intent.getStringExtra("ukoly") == "true"

        setContent {
            AppTheme(
                useDarkTheme = isSystemInDarkTheme(),
                useDynamicColor = true,
            ) {
                MainSceeen(
                    rozvrh = rozvrh,
                )
            }
        }
    }
}
