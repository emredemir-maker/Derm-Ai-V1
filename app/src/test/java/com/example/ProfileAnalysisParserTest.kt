package com.example

import com.example.data.api.GeminiRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileAnalysisParserTest {
    private val validRegions = listOf(
        "Alın",
        "T-Bölgesi ve Burun",
        "Sol Yanak",
        "Sağ Yanak",
        "Göz Çevresi",
        "Çene"
    ).joinToString(",") { name ->
        """{"regionName":"$name","issue":"Görünür bakım ihtiyacı","recommendedIngredient":"Niasinamid","x":0.5,"y":0.5}"""
    }

    @Test
    fun acceptsCompleteSixRegionResponse() {
        val result = GeminiRepository.parseProfileAnalysisResponse(
            """{"skinType":"Karma","concerns":["Kuruluk"],"goal":"Nemlendirme","explanation":"Görsel bakım özeti","faceMapRegions":[$validRegions]}"""
        )
        assertNotNull(result)
        assertEquals(6, result?.faceMapRegions?.size)
    }

    @Test
    fun rejectsMissingRegions() {
        val result = GeminiRepository.parseProfileAnalysisResponse(
            """{"skinType":"Karma","explanation":"Özet","faceMapRegions":[]}"""
        )
        assertNull(result)
    }

    @Test
    fun rejectsGenericRegionNames() {
        val genericRegions = validRegions.replace("Alın", "Bölge 1")
        val result = GeminiRepository.parseProfileAnalysisResponse(
            """{"skinType":"Karma","explanation":"Özet","faceMapRegions":[$genericRegions]}"""
        )
        assertNull(result)
    }
}
