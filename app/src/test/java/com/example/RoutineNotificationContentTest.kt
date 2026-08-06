package com.example

import com.example.data.database.SkinProfile
import com.example.data.notification.RoutineNotificationContentBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineNotificationContentTest {
    private val profile = SkinProfile(
        userName = "Deniz",
        age = 27,
        gender = "Belirtmek istemiyorum",
        skinType = "Yağlı",
        skinConcerns = "Akne",
        skincareGoal = "Sivilce Kontrolü",
        makeupPreference = "",
        lastAnalysisRoutine = """
            Sabah Rutini
            1. Nazik jel temizleyici kullan
            2. SPF 50 güneş kremi uygula
            Akşam Rutini
            1. Yağ bazlı temizleyici kullan
            2. Niasinamid serum uygula
        """.trimIndent()
    )

    @Test
    fun eveningNotificationUsesSavedAiRoutine() {
        val content = RoutineNotificationContentBuilder.build(profile, "evening")

        assertEquals(RoutineNotificationContentBuilder.DESTINATION_EVENING, content.destination)
        assertTrue(content.message.contains("Yağ bazlı temizleyici"))
        assertTrue(content.message.contains("Niasinamid serum"))
        assertFalse(content.message.contains("Salisilik asit"))
    }

    @Test
    fun morningNotificationTargetsMorningGuide() {
        val content = RoutineNotificationContentBuilder.build(profile, "morning")

        assertEquals(RoutineNotificationContentBuilder.DESTINATION_MORNING, content.destination)
        assertTrue(content.message.startsWith("Deniz,"))
        assertTrue(content.message.contains("SPF 50"))
    }

    @Test
    fun missingRoutineDoesNotInventSteps() {
        val content = RoutineNotificationContentBuilder.build(profile.copy(lastAnalysisRoutine = null), "evening")

        assertTrue(content.message.contains("adımlarını görmek için dokun"))
        assertFalse(content.message.contains("krem"))
    }
}
