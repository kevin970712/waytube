package com.waytube.app.common.domain

import kotlin.time.Duration

sealed interface VideoItem : Identifiable {
    val title: String
    val channelName: String
    val thumbnailUrl: String

    data class Regular(
        override val id: String,
        override val title: String,
        override val channelName: String,
        override val thumbnailUrl: String,
        val duration: Duration
    ) : VideoItem

    data class Live(
        override val id: String,
        override val title: String,
        override val channelName: String,
        override val thumbnailUrl: String
    ) : VideoItem
}
