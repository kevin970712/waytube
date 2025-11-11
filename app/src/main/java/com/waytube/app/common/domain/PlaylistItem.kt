package com.waytube.app.common.domain

data class PlaylistItem(
    override val id: String,
    val title: String,
    val channelName: String,
    val thumbnailUrl: String,
    val videoCount: Long
) : Identifiable
