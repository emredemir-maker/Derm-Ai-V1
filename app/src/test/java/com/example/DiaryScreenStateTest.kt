package com.example

import com.example.data.database.DiaryEntry
import com.example.ui.screens.calculateDiaryState
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DiaryScreenStateTest {

    private val zoneId = ZoneOffset.UTC

    private fun createEntry(year: Int, month: Int, day: Int, rating: Int): DiaryEntry {
        val instant = ZonedDateTime.of(year, month, day, 12, 0, 0, 0, zoneId).toInstant()
        return DiaryEntry(
            id = 0,
            date = instant.toEpochMilli(),
            note = "",
            rating = rating,
            photoPath = null,
            aiFeedback = null
        )
    }

    @Test
    fun testEmptyEntries() {
        val targetMonth = YearMonth.of(2026, 8)
        val stats = calculateDiaryState(emptyList(), targetMonth, zoneId)

        assertEquals("Ağustos 2026", stats.selectedMonthLabel)
        assertEquals(0, stats.entryCount)
        assertEquals(0f, stats.averageRating)
        // Check days logic (August 2026 starts on Saturday, which is day 6 of week if Mon=1)
        // 5 padding days + 31 days = 36 days.
        // nearest multiple of 7 is 42. So 6 padding days at the end.
        assertEquals(42, stats.calendarDays.size)
    }

    @Test
    fun testAverageAndFiltering() {
        // Create entries for August and September
        val entries = listOf(
            createEntry(2026, 8, 1, 3), // Aug 1, rating 3
            createEntry(2026, 8, 2, 5), // Aug 2, rating 5
            createEntry(2026, 9, 1, 1)  // Sep 1, rating 1 (should be ignored)
        )

        val targetMonth = YearMonth.of(2026, 8)
        val stats = calculateDiaryState(entries, targetMonth, zoneId)

        assertEquals(2, stats.entryCount)
        assertEquals(4f, stats.averageRating) // (3+5)/2
    }

    @Test
    fun testSameDayLatestOverrides() {
        val entries = listOf(
            // Same day, different times/ratings (simulated by same day since our mock creates at 12:00)
            // Wait, we need them to have different dates to test sorting.
            DiaryEntry(id = 1, date = ZonedDateTime.of(2026, 8, 10, 10, 0, 0, 0, zoneId).toInstant().toEpochMilli(), note = "", rating = 2, photoPath = null, aiFeedback = null),
            DiaryEntry(id = 2, date = ZonedDateTime.of(2026, 8, 10, 15, 0, 0, 0, zoneId).toInstant().toEpochMilli(), note = "", rating = 5, photoPath = null, aiFeedback = null)
        )

        val targetMonth = YearMonth.of(2026, 8)
        val stats = calculateDiaryState(entries, targetMonth, zoneId)

        // entryCount counts all records for the month
        assertEquals(2, stats.entryCount)

        // But the calendar should only show the latest rating (5)
        // Let's find day 10 in the calendar
        // August 2026 starts on Saturday (firstDayOfWeek = 6, padding = 5)
        // So day 10 is at index padding + 10 - 1 = 5 + 9 = 14
        val (day, rating) = stats.calendarDays[14]
        assertEquals(10, day)
        assertEquals(5, rating) // the latest one
    }

    @Test
    fun testRatingColors() {
        val entries = listOf(
            createEntry(2026, 8, 1, 1),
            createEntry(2026, 8, 2, 5)
        )
        val targetMonth = YearMonth.of(2026, 8)
        val stats = calculateDiaryState(entries, targetMonth, zoneId)

        // padding = 5
        assertEquals(1, stats.calendarDays[5].second) // Aug 1 rating 1
        assertEquals(5, stats.calendarDays[6].second) // Aug 2 rating 5
    }
}
