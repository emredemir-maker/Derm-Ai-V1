package com.example.util

import com.example.data.database.DiaryEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun calculateDiaryStreak(entries: List<DiaryEntry>, now: Date = Date()): Int {
    if (entries.isEmpty()) return 0

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // Filter out future entries
    val validEntries = entries.filter { it.date <= now.time }
    if (validEntries.isEmpty()) return 0

    val loggedDates = validEntries.map { sdf.format(Date(it.date)) }.toSet()

    val cal = Calendar.getInstance().apply {
        time = now
    }
    
    var count = 0
    val todayFormatted = sdf.format(cal.time)

    // Check if logged today
    if (loggedDates.contains(todayFormatted)) {
        while (loggedDates.contains(sdf.format(cal.time))) {
            count++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
    } else {
        // Check if logged yesterday
        cal.add(Calendar.DAY_OF_YEAR, -1)
        if (loggedDates.contains(sdf.format(cal.time))) {
            while (loggedDates.contains(sdf.format(cal.time))) {
                count++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }
        }
    }

    return count
}
