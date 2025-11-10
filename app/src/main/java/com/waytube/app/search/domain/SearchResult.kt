package com.waytube.app.search.domain

import com.waytube.app.common.domain.ChannelItem
import com.waytube.app.common.domain.Identifiable
import com.waytube.app.common.domain.PlaylistItem
import com.waytube.app.common.domain.VideoItem

sealed interface SearchResult : Identifiable {
    data class Video(val item: VideoItem) : SearchResult, Identifiable by item

    data class Channel(val item: ChannelItem) : SearchResult, Identifiable by item

    data class Playlist(val item: PlaylistItem) : SearchResult, Identifiable by item
}
