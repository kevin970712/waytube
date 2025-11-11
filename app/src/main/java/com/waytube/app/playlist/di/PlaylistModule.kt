package com.waytube.app.playlist.di

import com.waytube.app.playlist.data.NewPipePlaylistRepository
import com.waytube.app.playlist.domain.PlaylistRepository
import com.waytube.app.playlist.ui.PlaylistViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val playlistModule = module {
    singleOf(::NewPipePlaylistRepository) bind PlaylistRepository::class
    viewModelOf(::PlaylistViewModel)
}
