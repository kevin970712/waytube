package com.waytube.app.navigation.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waytube.app.navigation.domain.DeepLinkResult
import com.waytube.app.navigation.domain.NavigationRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NavigationViewModel(private val repository: NavigationRepository) : ViewModel() {
    private val deepLinkResultChannel = Channel<DeepLinkResult>()

    val deepLinkResult = deepLinkResultChannel.receiveAsFlow()

    fun provideIntent(intent: Intent) {
        intent.data?.let { repository.resolveDeepLink(it.toString()) }?.let { result ->
            viewModelScope.launch { deepLinkResultChannel.send(result) }
        }
    }
}
