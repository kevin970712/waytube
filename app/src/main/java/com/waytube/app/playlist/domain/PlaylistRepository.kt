package com.waytube.app.playlist.domain

import androidx.paging.PagingData
import com.waytube.app.common.domain.VideoItem
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun getPlaylist(id: String): Result<Playlist>

    fun getVideoItems(id: String): Flow<PagingData<VideoItem>>
}
