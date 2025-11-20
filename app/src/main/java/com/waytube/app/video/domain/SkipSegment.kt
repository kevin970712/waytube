package com.waytube.app.video.domain

import kotlin.time.Duration

data class SkipSegment(
    val id: String,
    val start: Duration,
    val end: Duration
)
