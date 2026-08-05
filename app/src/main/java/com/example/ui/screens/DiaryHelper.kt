@file:android.annotation.SuppressLint("NewApi")

package com.example.ui.screens

import android.annotation.SuppressLint

import com.example.data.database.DiaryEntry
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class MonthStats(
    val selectedMonthLabel: String,
    val entryCount: Int,
    val averageRating: Float,
    val calendarDays: List<Pair<Int, Int>>
)

fun calculateDiaryState(
    entries: List<DiaryEntry>,
    targetMonth: YearMonth,
    zoneId: ZoneId
): MonthStats {
    val monthEntries = entries.filter { entry ->
        val date = Instant.ofEpochMilli(entry.date).atZone(zoneId).toLocalDate()
        date.year == targetMonth.year && date.monthValue == targetMonth.monthValue
    }

    val entryCount = monthEntries.size
    val averageRating = if (entryCount > 0) {
        monthEntries.map { it.rating }.average().toFloat()
    } else {
        0f
    }

    val dailyRatings = mutableMapOf<Int, Int>()
    monthEntries.sortedBy { it.date }.forEach { entry ->
        val date = Instant.ofEpochMilli(entry.date).atZone(zoneId).toLocalDate()
        dailyRatings[date.dayOfMonth] = entry.rating
    }

    val firstDayOfWeek = targetMonth.atDay(1).dayOfWeek.value
    val daysInMonth = targetMonth.lengthOfMonth()

    val calendarDays = mutableListOf<Pair<Int, Int>>()

    val padding = firstDayOfWeek - 1
    for (i in 0 until padding) {
        calendarDays.add(Pair(0, 0))
    }

    for (day in 1..daysInMonth) {
        val rating = dailyRatings[day] ?: 0
        calendarDays.add(Pair(day, rating))
    }

    val remaining = calendarDays.size % 7
    if (remaining != 0) {
        for (i in 0 until (7 - remaining)) {
            calendarDays.add(Pair(0, 0))
        }
    }

    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("tr-TR"))
    val selectedMonthLabel = targetMonth.format(formatter)

    return MonthStats(
        selectedMonthLabel = selectedMonthLabel,
        entryCount = entryCount,
        averageRating = averageRating,
        calendarDays = calendarDays
    )
}
