package com.example

import com.example.ui.viewmodel.mapToStandardConcerns
import com.example.ui.viewmodel.calculateDynamicSkinScore
import com.example.ui.viewmodel.hasCompletedPhotoAnalysis
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
            userName = "TestUser",
            skinType = "Karma",
            skinConcerns = "Akne & Sivilce, Lekeler & Pigmentasyon, Geniş Gözenekler",
            skincareGoal = "Sivilce Kontrolü",
            makeupPreference = "Doğal"
        )
        // Without photo scan analysis, score returns 0
        val scoreNullAnalysis = calculateDynamicSkinScore(profile, null)
        assertEquals(0, scoreNullAnalysis)

        // With photo scan analysis, returns skinHealthScore from analysis
        val mockAnalysis = ProfileAnalysisResult(
            skinType = "Karma",
            concerns = listOf("Akne"),
            goal = "Sivilce Kontrolü",
            explanation = "Test",
            eyeAreaAnalysis = "",
            makeupEvaluation = "",
            confidenceScore = 90,
            skinHealthScore = 78,
            faceMapRegions = emptyList()
        )
        val scoreWithAnalysis = calculateDynamicSkinScore(profile, mockAnalysis)
        assertEquals(78, scoreWithAnalysis)
    }

    @Test
    fun testCompletedPhotoAnalysisDoesNotDependOnScore() {
        assertTrue(hasCompletedPhotoAnalysis(ProfileAnalysisResult(skinHealthScore = 0)))
        assertEquals(false, hasCompletedPhotoAnalysis(null))
    }
}
