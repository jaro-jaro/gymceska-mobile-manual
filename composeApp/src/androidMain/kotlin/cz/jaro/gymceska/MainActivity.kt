package cz.jaro.gymceska

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.vinceglb.filekit.core.FileKit
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val repo by inject<Repository>()

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileKit.init(this)

        setContent {
            MainContent(repo)
        }
    }
}
