package com.waytube.app.common.ui

import android.text.format.DateUtils
import kotlin.time.Duration

fun Duration.toFormattedString(): String = DateUtils.formatElapsedTime(inWholeSeconds)
