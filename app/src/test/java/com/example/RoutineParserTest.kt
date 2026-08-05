package com.example

import com.example.util.RoutineParser
import org.junit.Assert.*
import org.junit.Test

class RoutineParserTest {

    @Test
    fun testMorningAndEveningSectionsParsedCorrectly() {
        val input = """
            Sabah Rutini:
            1. Yüz Temizleyici
            2. C Vitamini Serumu
            
            Akşam Rutini:
            - Çift Aşama Temizlik
            - Gece Nemlendiricisi
        """.trimIndent()

        val result = RoutineParser.parse(input)

        assertTrue(result.isCategorized)
        assertEquals(listOf("Yüz Temizleyici", "C Vitamini Serumu"), result.morningSteps)
        assertEquals(listOf("Çift Aşama Temizlik", "Gece Nemlendiricisi"), result.eveningSteps)
        assertNull(result.uncategorizedText)
    }

    @Test
    fun testNumberedStepsAreCleaned() {
        val input = """
            Sabah:
            1. Temizleme
            2) Serum
            3 - Güneş Kremi
        """.trimIndent()

        val result = RoutineParser.parse(input)

        assertEquals(listOf("Temizleme", "Serum", "Güneş Kremi"), result.morningSteps)
    }

    @Test
    fun testDashedAndBulletStepsAreCleaned() {
        val input = """
            Akşam Rutini:
            - Temizleme Jeli
            * Gece Serumu
            • Yoğun Krem
        """.trimIndent()

        val result = RoutineParser.parse(input)

        assertEquals(listOf("Temizleme Jeli", "Gece Serumu", "Yoğun Krem"), result.eveningSteps)
    }

    @Test
    fun testHeaderCaseVariations() {
        val input = """
            SABAH RUTİNİ
            - Adım 1
            
            AKŞAM
            - Adım 2
        """.trimIndent()

        val result = RoutineParser.parse(input)

        assertEquals(listOf("Adım 1"), result.morningSteps)
        assertEquals(listOf("Adım 2"), result.eveningSteps)
    }

    @Test
    fun testOnlyMorningSectionDoesNotCrash() {
        val input = """
            Sabah Rutini:
            1. Güneş Kremi
            2. Nemlendirici
        """.trimIndent()

        val result = RoutineParser.parse(input)

        assertEquals(listOf("Güneş Kremi", "Nemlendirici"), result.morningSteps)
        assertTrue(result.eveningSteps.isEmpty())
        assertNull(result.uncategorizedText)
    }

    @Test
    fun testUncategorizedTextPreservedWhenNoHeaders() {
        val input = "Günlük bakımınızda her gün nemlendirici ve güneş koruyucu kullanın."

        val result = RoutineParser.parse(input)

        assertFalse(result.isCategorized)
        assertTrue(result.morningSteps.isEmpty())
        assertTrue(result.eveningSteps.isEmpty())
        assertEquals("Günlük bakımınızda her gün nemlendirici ve güneş koruyucu kullanın.", result.uncategorizedText)
    }

    @Test
    fun testEmptyAndNullTextProducesNoFakeSteps() {
        val nullResult = RoutineParser.parse(null)
        val emptyResult = RoutineParser.parse("   ")

        assertFalse(nullResult.isCategorized)
        assertTrue(nullResult.morningSteps.isEmpty())
        assertTrue(nullResult.eveningSteps.isEmpty())
        assertNull(nullResult.uncategorizedText)

        assertFalse(emptyResult.isCategorized)
        assertTrue(emptyResult.morningSteps.isEmpty())
        assertTrue(emptyResult.eveningSteps.isEmpty())
        assertNull(emptyResult.uncategorizedText)
    }

    @Test
    fun testMorningAndEveningListsDoNotMix() {
        val input = """
            Sabah Rutini:
            - Sabah Serumu
            
            Akşam Rutini:
            - Akşam Kremi
        """.trimIndent()

        val result = RoutineParser.parse(input)

        assertEquals(listOf("Sabah Serumu"), result.morningSteps)
        assertEquals(listOf("Akşam Kremi"), result.eveningSteps)
        assertFalse(result.morningSteps.contains("Akşam Kremi"))
        assertFalse(result.eveningSteps.contains("Sabah Serumu"))
    }
}
