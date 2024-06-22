package cz.jaro.rozvrhmanual

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import cz.jaro.rozvrhmanual.ui.theme.GymceskaTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rozvrh = intent.getBooleanExtra("rozvrh", false) || intent.getStringExtra("rozvrh") == "true"

        setContent {
            GymceskaTheme(
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
