package cz.jaro.gymceska

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import cz.jaro.gymceska.koin.initKoin
import org.koin.dsl.module

class App : Application() {
    @OptIn(ExperimentalSettingsImplementation::class, ExperimentalSettingsApi::class)
    override fun onCreate() {
        super.onCreate()

        initKoin(module {
            single<FlowSettings> {
                DataStoreSettings(
                    PreferenceDataStoreFactory.create(
                        migrations = listOf()
                    ) {
                        preferencesDataStoreFile("Gymceskamanual_JARO_datastore")
                    }
                )
            }
        })
    }
}
