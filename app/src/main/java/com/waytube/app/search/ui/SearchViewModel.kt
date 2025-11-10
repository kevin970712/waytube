package com.waytube.app.search.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.waytube.app.search.domain.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SearchRepository
) : ViewModel() {
    private val suggestionsQuery = MutableStateFlow("")

    private val submittedQuery = savedStateHandle.getMutableStateFlow<String?>(
        key = "submitted_query",
        initialValue = null
    )

    val suggestions = suggestionsQuery
        .debounce(150.milliseconds)
        .mapLatest { query ->
            query.takeIf { it.isNotBlank() }?.let {
                repository.getSuggestions(it).getOrNull()
            } ?: emptyList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val isQuerySubmitted = submittedQuery
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = false
        )

    val results = submittedQuery
        .flatMapLatest { query ->
            if (query != null) repository.getResults(query) else flowOf(PagingData.empty())
        }
        .cachedIn(viewModelScope)

    fun setSuggestionQuery(query: String) {
        suggestionsQuery.value = query
    }

    fun trySubmit(query: String): Boolean = query
        .takeIf { it.isNotBlank() }
        ?.also { submittedQuery.value = it } != null
}
