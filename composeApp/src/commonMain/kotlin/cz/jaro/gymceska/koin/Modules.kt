package cz.jaro.gymceska.koin

import com.russhwolf.settings.ExperimentalSettingsApi
import cz.jaro.gymceska.Repository
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
val commonModule = module {
    single {
        Repository(get())
    }
}