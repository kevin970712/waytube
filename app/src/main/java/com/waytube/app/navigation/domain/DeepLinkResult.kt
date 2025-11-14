package com.waytube.app.navigation.domain

sealed interface DeepLinkResult {
    data class Video(val id: String) : DeepLinkResult

    data class Channel(val id: String) : DeepLinkResult

    data class Playlist(val id: String) : DeepLinkResult
}
