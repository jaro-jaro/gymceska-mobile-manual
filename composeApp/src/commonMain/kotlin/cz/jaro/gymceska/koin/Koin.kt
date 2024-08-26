package cz.jaro.gymceska.koin

import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(
    platformModule: Module = Module()
) = startKoin {
    modules(commonModule, platformModule)
}