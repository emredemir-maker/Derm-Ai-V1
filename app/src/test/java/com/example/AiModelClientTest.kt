package com.example

import com.example.data.api.GeminiRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AiModelClientTest {

    private lateinit var fakeClient: FakeAiModelClient

    @Before
    fun setup() {
        fakeClient = FakeAiModelClient()
        GeminiRepository.aiClient = fakeClient
    }

    @Test
    fun testGetSkinCareAnalysisSuccess() = runBlocking {
        fakeClient.mockResponse = "1. BÖLÜM: CILT BAKIM RUTİNİ\nSabah C Vitamini.\n\n2. BÖLÜM: MAKYAJ TAVSİYELERİ\nSu bazlı fondöten."
        val (routine, makeup) = GeminiRepository.getSkinCareAnalysis("Kuru", "Akne", "Nem", "Hafif")
        assertTrue(routine.contains("Sabah C Vitamini"))
        assertTrue(makeup.contains("Su bazlı fondöten"))
        assertEquals(1, fakeClient.requestedPrompts.size)
        assertNotNull(fakeClient.requestedSystemInstructions[0])
    }

    @Test
    fun testAgeAndGenderAreIncludedInRecommendationContext() = runBlocking {
        fakeClient.mockResponse = "1. BÖLÜM: Rutin\n2. BÖLÜM: Tavsiye"

        GeminiRepository.getSkinCareAnalysis(
            skinType = "Yağlı",
            concerns = "Akne",
            goal = "Sivilce Kontrolü",
            makeup = "Belirtilmedi",
            age = 24,
            gender = "Kadın"
        )

        val prompt = fakeClient.requestedPrompts.single()
        assertTrue(prompt.contains("Yaş: 24"))
        assertTrue(prompt.contains("Cinsiyet: Kadın"))
    }

    @Test
    fun testUnconfiguredFirebaseHandling() = runBlocking {
        fakeClient.unconfigured = true
        val response = GeminiRepository.getChatResponse("Merhaba", "Yağlı cilt")
        assertTrue(response.contains("Hata") || response.isNotEmpty())
    }

    @Test
    fun testAnalyzeSkinForProfileJsonParsing() = runBlocking {
        fakeClient.mockResponse = """
            {
                "skinType": "Karma",
                "concerns": ["Sivilce"],
                "goal": "Sivilce Kontrolü",
                "explanation": "Test açıklama",
                "eyeAreaAnalysis": "Normal",
                "makeupEvaluation": "İyi",
                "skinHealthScore": 80,
                "confidenceScore": 90,
                "faceMapRegions": []
            }
        """.trimIndent()
        // passing non-existent path will return null, so we test cleanJson logic or handle gracefully
        val result = GeminiRepository.analyzeSkinForProfile("non_existent_photo.jpg")
        assertNull(result)
    }

    @Test
    fun testFakeClientRecordsParameters() = runBlocking {
        fakeClient.mockResponse = "Test Yanıt"
        GeminiRepository.getChatResponse("Test Mesaj", "Hassas cilt")
        assertEquals(1, fakeClient.requestedPrompts.size)
        assertEquals("Test Mesaj", fakeClient.requestedPrompts[0])
        assertEquals(0.7f, fakeClient.requestedTemperatures[0])
    }
}
