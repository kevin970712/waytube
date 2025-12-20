package com.waytube.app.common.domain

data class PlaylistItem(
    override val id: String,
    val url: String,
    val title: String,
    val channelId: String?,
    val channelName: String,
    val thumbnailUrl: String,
    val videoCount: Long
) : Identifiable
