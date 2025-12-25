package com.waytube.app.navigation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.waytube.app.channel.ui.ChannelScreen
import com.waytube.app.navigation.domain.DeepLinkResult
import com.waytube.app.playlist.ui.PlaylistScreen
import com.waytube.app.search.ui.SearchScreen
import com.waytube.app.video.ui.VideoNowPlayingBar
import com.waytube.app.video.ui.VideoScreen
import com.waytube.app.video.ui.VideoViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private data object RootRoute

private data object VideoRoute

@Serializable
private data object SearchRoute : NavKey

@Serializable
private data class ChannelRoute(val id: String) : NavKey

@Serializable
private data class PlaylistRoute(val id: String) : NavKey

@Composable
fun NavigationHost(
    viewModel: NavigationViewModel,
    videoViewModel: VideoViewModel,
    onSetVideoImmersiveMode: (Boolean) -> Unit,
    onKeepScreenAwake: (Boolean) -> Unit
) {
    val isVideoActive by videoViewModel.isActive.collectAsStateWithLifecycle()
    val isVideoPlaying by videoViewModel.isPlaying.collectAsStateWithLifecycle()

    val backStack = rememberNavBackStack(SearchRoute)

    var isVideoFullscreen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.deepLinkResult.collect { result ->
            when (result) {
                is DeepLinkResult.Video -> {
                    videoViewModel.play(result.id)
                    isVideoFullscreen = true
                }

                is DeepLinkResult.Channel -> {
                    backStack += ChannelRoute(result.id)
                    isVideoFullscreen = false
                }

                is DeepLinkResult.Playlist -> {
                    backStack += PlaylistRoute(result.id)
                    isVideoFullscreen = false
                }
            }
        }
    }

    if (isVideoActive && isVideoFullscreen) {
        DisposableEffect(Unit) {
            onSetVideoImmersiveMode(true)

            onDispose { onSetVideoImmersiveMode(false) }
        }
    }

    if (isVideoActive && isVideoFullscreen && isVideoPlaying) {
        DisposableEffect(Unit) {
            onKeepScreenAwake(true)

            onDispose { onKeepScreenAwake(false) }
        }
    }

    Surface {
        NavDisplay(
            backStack = listOf(RootRoute).let { stack ->
                if (isVideoActive && isVideoFullscreen) stack + VideoRoute else stack
            },
            onBack = {
                if (isVideoActive && isVideoFullscreen) {
                    isVideoFullscreen = false
                }
            },
            transitionSpec = {
                slideInVertically { it } togetherWith
                        ExitTransition.KeepUntilTransitionsFinished
            },
            popTransitionSpec = {
                EnterTransition.None togetherWith slideOutVertically { it }
            },
            predictivePopTransitionSpec = {
                EnterTransition.None togetherWith slideOutVertically { it }
            },
            entryProvider = entryProvider {
                entry<RootRoute> {
                    Scaffold(
                        bottomBar = {
                            if (isVideoActive) {
                                VideoNowPlayingBar(
                                    viewModel = videoViewModel,
                                    onClick = { isVideoFullscreen = true }
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets(0)
                    ) { contentPadding ->
                        NavDisplay(
                            backStack = backStack,
                            modifier = Modifier
                                .padding(contentPadding)
                                .consumeWindowInsets(contentPadding),
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            transitionSpec = {
                                slideInHorizontally { it } togetherWith slideOutHorizontally { -it / 2 }
                            },
                            popTransitionSpec = {
                                slideInHorizontally { -it / 2 } togetherWith slideOutHorizontally { it }
                            },
                            predictivePopTransitionSpec = {
                                slideInHorizontally { -it / 2 } togetherWith slideOutHorizontally { it }
                            },
                            entryProvider = entryProvider {
                                entry<SearchRoute> {
                                    SearchScreen(
                                        viewModel = koinViewModel(),
                                        onNavigateToVideo = { id ->
                                            videoViewModel.play(id)
                                            isVideoFullscreen = true
                                        },
                                        onNavigateToChannel = { id ->
                                            backStack += ChannelRoute(id)
                                            isVideoFullscreen = false
                                        },
                                        onNavigateToPlaylist = { id ->
                                            backStack += PlaylistRoute(id)
                                            isVideoFullscreen = false
                                        }
                                    )
                                }

                                entry<ChannelRoute> { (id) ->
                                    ChannelScreen(
                                        viewModel = koinViewModel { parametersOf(id) },
                                        onNavigateToVideo = { id ->
                                            videoViewModel.play(id)
                                            isVideoFullscreen = true
                                        }
                                    )
                                }

                                entry<PlaylistRoute> { (id) ->
                                    PlaylistScreen(
                                        viewModel = koinViewModel { parametersOf(id) },
                                        onNavigateToVideo = { id ->
                                            videoViewModel.play(id)
                                            isVideoFullscreen = true
                                        },
                                        onNavigateToChannel = { id ->
                                            backStack += ChannelRoute(id)
                                            isVideoFullscreen = false
                                        }
                                    )
                                }
                            }
                        )
                    }
                }

                entry<VideoRoute> {
                    VideoScreen(viewModel = videoViewModel)
                }
            }
        )
    }
}
