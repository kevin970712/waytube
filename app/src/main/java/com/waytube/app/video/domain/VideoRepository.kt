package com.waytube.app.video.domain

interface VideoRepository {
    suspend fun getVideo(id: String): Result<Video>

    suspend fun getSkipSegments(id: String): Result<List<SkipSegment>>
}
