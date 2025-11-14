package com.waytube.app.video.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.waytube.app.R
import com.waytube.app.common.ui.AppColorScheme
import com.waytube.app.common.ui.StateMessage
import com.waytube.app.common.ui.UiState
import com.waytube.app.video.domain.Video

@Composable
fun VideoScreen(viewModel: VideoViewModel) {
    val videoState by viewModel.videoState.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle(initialValue = null)

    VideoScreenContent(
        videoState = { videoState },
        player = { player },
        onRetry = viewModel::retry
    )
}

@Composable
private fun VideoScreenContent(
    videoState: () -> UiState<Video>?,
    player: () -> Player?,
    onRetry: () -> Unit
) {
    MaterialTheme(colorScheme = AppColorScheme.Dark) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.scrim,
            contentColor = MaterialTheme.colorScheme.onSurface,
            contentWindowInsets = WindowInsets.displayCutout
        ) { contentPadding ->
            when (videoState()) {
                null -> {}

                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        StateMessage(
                            text = stringResource(R.string.message_video_load_error),
                            onRetry = onRetry
                        )
                    }
                }

                is UiState.Data -> {
                    player()?.let { player ->
                        AndroidView(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(contentPadding),
                            factory = { context ->
                                PlayerView(context).apply {
                                    this.player = player
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
