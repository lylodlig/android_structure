package com.huania.eew.app

import org.koin.dsl.module

val viewModelModule = module {
}
val repositoryModule = module {
}
val appModule = listOf(viewModelModule, repositoryModule)