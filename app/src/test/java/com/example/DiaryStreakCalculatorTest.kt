package com.example

import com.example.data.database.DiaryEntry
import com.example.util.calculateDiaryStreak
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

class DiaryStreakCalculatorTest {

    @Test
    fun testEmptyEntriesReturnsZero() {
        val streak = calculateDiaryStreak(emptyList())
        assertEquals(0, streak)
    }

    @Test
    fun testFutureDateEntriesIgnored() {
        val futureCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 2)
        }
        val entry = DiaryEntry(
            id = 1,
            date = futureCal.timeInMillis,
            note = "Future entry",
            rating = 5,
            photoPath = null,
            aiFeedback = null
        )
        val streak = calculateDiaryStreak(listOf(entry))
        assertEquals(0, streak)
    }

    @Test
    fun testMultipleEntriesSameDayCountsAsOne() {
        val now = Date()
        val entry1 = DiaryEntry(id = 1, date = now.time, note = "Morning", rating = 4, photoPath = null, aiFeedback = null)
        val entry2 = DiaryEntry(id = 2, date = now.time, note = "Evening", rating = 5, photoPath = null, aiFeedback = null)

        val streak = calculateDiaryStreak(listOf(entry1, entry2), now)
        assertEquals(1, streak)
    }

    @Test
    fun testConsecutiveDaysCalculatedCorrectly() {
        val now = Calendar.getInstance()
        val today = now.timeInMillis

        now.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = now.timeInMillis

        now.add(Calendar.DAY_OF_YEAR, -1)
        val twoDaysAgo = now.timeInMillis

        val entry1 = DiaryEntry(id = 1, date = today, note = "Today", rating = 5, photoPath = null, aiFeedback = null)
        val entry2 = DiaryEntry(id = 2, date = yesterday, note = "Yesterday", rating = 4, photoPath = null, aiFeedback = null)
        val entry3 = DiaryEntry(id = 3, date = twoDaysAgo, note = "2 days ago", rating = 4, photoPath = null, aiFeedback = null)

        val streak = calculateDiaryStreak(listOf(entry1, entry2, entry3), Date(today))
        assertEquals(3, streak)
    }

    @Test
    fun testStreakActiveWhenLoggedYesterdayButNotTodayYet() {
        val now = Calendar.getInstance()
        val today = now.timeInMillis

        now.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = now.timeInMillis

        now.add(Calendar.DAY_OF_YEAR, -1)
        val twoDaysAgo = now.timeInMillis

        val entry1 = DiaryEntry(id = 1, date = yesterday, note = "Yesterday", rating = 4, photoPath = null, aiFeedback = null)
        val entry2 = DiaryEntry(id = 2, date = twoDaysAgo, note = "2 days ago", rating = 4, photoPath = null, aiFeedback = null)

        val streak = calculateDiaryStreak(listOf(entry1, entry2), Date(today))
        assertEquals(2, streak)
    }

    @Test
    fun testGapInDaysBreaksStreak() {
        val now = Calendar.getInstance()
        val today = now.timeInMillis

        now.add(Calendar.DAY_OF_YEAR, -3)
        val threeDaysAgo = now.timeInMillis

        val entry1 = DiaryEntry(id = 1, date = today, note = "Today", rating = 5, photoPath = null, aiFeedback = null)
        val entry2 = DiaryEntry(id = 2, date = threeDaysAgo, note = "3 days ago", rating = 3, photoPath = null, aiFeedback = null)

        val streak = calculateDiaryStreak(listOf(entry1, entry2), Date(today))
        assertEquals(1, streak)
    }
}
