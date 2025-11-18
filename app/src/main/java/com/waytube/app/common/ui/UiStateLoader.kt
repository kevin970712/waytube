package com.waytube.app.common.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest

@OptIn(ExperimentalCoroutinesApi::class)
class UiStateLoader {
    private val fetchTrigger = MutableSharedFlow<Unit>()

    fun <T> bind(fetch: suspend () -> Result<T>): Flow<UiState<T>> =
        fetchTrigger
            .onStart { emit(Unit) }
            .transformLatest {
                emit(UiState.Loading)
                emit(
                    fetch().fold(
                        onSuccess = { UiState.Data(it) },
                        onFailure = { UiState.Error(it) }
                    )
                )
            }

    suspend fun retry() {
        fetchTrigger.emit(Unit)
    }
}
