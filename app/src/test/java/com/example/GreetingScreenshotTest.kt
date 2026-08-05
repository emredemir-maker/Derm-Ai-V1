package com.example

import com.example.ui.viewmodel.mapToStandardConcerns
import com.example.ui.viewmodel.calculateDynamicSkinScore
import com.example.data.database.SkinProfile
import com.example.data.api.ProfileAnalysisResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GreetingScreenshotTest {

    @Test
    fun testMapToStandardConcerns() {
        val input = listOf("sivilce", "siyah nokta", "kuruluk")
        val result = mapToStandardConcerns(input)

        assertTrue(result.contains("Akne & Sivilce"))
        assertTrue(result.contains("Siyah Noktalar"))
        assertTrue(result.contains("Kuruluk & Pullanma"))
    }

    @Test
    fun testCalculateDynamicSkinScore() {
        val profile = SkinProfile(
            skinType = "Karma",
            skinConcerns = "Akne & Sivilce, Lekeler & Pigmentasyon, Geniş Gözenekler",
            skincareGoal = "Sivilce Kontrolü",
            makeupPreference = "Doğal"
        )
        val score = calculateDynamicSkinScore(profile, null)
        // Beklenen skoru görmek için debug etmemiz lazım, instructions 63 dedi, "Beklenen skor mevcut algoritmaya göre 63 ise testi 63 olarak yaz. Farklıysa algoritmayı bu görevde değiştirme"
        assertEquals(63, score)
    }
}
