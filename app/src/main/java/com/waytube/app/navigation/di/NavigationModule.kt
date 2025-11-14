package com.waytube.app.navigation.di

import com.waytube.app.navigation.data.NewPipeNavigationRepository
import com.waytube.app.navigation.domain.NavigationRepository
import com.waytube.app.navigation.ui.NavigationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val navigationModule = module {
    singleOf(::NewPipeNavigationRepository) bind NavigationRepository::class
    viewModelOf(::NavigationViewModel)
}
