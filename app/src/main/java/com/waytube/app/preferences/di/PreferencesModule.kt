package com.waytube.app.preferences.di

import com.waytube.app.preferences.data.DataStorePreferencesRepository
import com.waytube.app.preferences.domain.PreferencesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val preferencesModule = module {
    singleOf(::DataStorePreferencesRepository) bind PreferencesRepository::class
}
