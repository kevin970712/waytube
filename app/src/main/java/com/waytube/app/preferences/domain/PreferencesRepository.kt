package com.waytube.app.preferences.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val searchHistory: Flow<List<String>>

    suspend fun saveSearch(query: String)
}
