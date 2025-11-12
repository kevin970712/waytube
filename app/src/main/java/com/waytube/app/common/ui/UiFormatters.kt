package com.waytube.app.common.ui

import android.icu.text.CompactDecimalFormat
import android.icu.text.RelativeDateTimeFormatter
import android.text.format.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.time.Duration

private val RELATIVE_TIME_UNITS = listOf(
    ChronoUnit.YEARS to RelativeDateTimeFormatter.RelativeUnit.YEARS,
    ChronoUnit.MONTHS to RelativeDateTimeFormatter.RelativeUnit.MONTHS,
    ChronoUnit.WEEKS to RelativeDateTimeFormatter.RelativeUnit.WEEKS,
    ChronoUnit.DAYS to RelativeDateTimeFormatter.RelativeUnit.DAYS,
    ChronoUnit.HOURS to RelativeDateTimeFormatter.RelativeUnit.HOURS,
    ChronoUnit.MINUTES to RelativeDateTimeFormatter.RelativeUnit.MINUTES,
    ChronoUnit.SECONDS to RelativeDateTimeFormatter.RelativeUnit.SECONDS
)

fun Duration.toFormattedString(): String = DateUtils.formatElapsedTime(inWholeSeconds)

fun Long.toPluralCount(): Int = coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

fun Long.toCompactString(): String = CompactDecimalFormat
    .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
    .format(this)

fun Instant.toRelativeTimeString(now: Instant = Instant.now()): String {
    val start = atZone(ZoneId.systemDefault())
    val end = now.atZone(ZoneId.systemDefault())

    val direction = if (start.isAfter(end)) {
        RelativeDateTimeFormatter.Direction.NEXT
    } else {
        RelativeDateTimeFormatter.Direction.LAST
    }

    val (quantity, unit) = RELATIVE_TIME_UNITS.firstNotNullOfOrNull { (chronoUnit, relativeUnit) ->
        val quantity = abs(chronoUnit.between(start, end))
        if (quantity > 0) quantity to relativeUnit else null
    } ?: (0L to RELATIVE_TIME_UNITS.last().second)

    return RelativeDateTimeFormatter.getInstance().format(quantity.toDouble(), direction, unit)
}
