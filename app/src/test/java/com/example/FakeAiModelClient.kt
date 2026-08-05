package com.example

import android.graphics.Bitmap
import com.example.data.api.AiModelClient

class FakeAiModelClient(
    var mockResponse: String = "Test AI Response",
    var shouldThrow: Boolean = false,
    var exceptionToThrow: Exception = Exception("Network error"),
    var unconfigured: Boolean = false
) : AiModelClient {
    
    val requestedPrompts = mutableListOf<String>()
    val requestedSystemInstructions = mutableListOf<String?>()
    val requestedBitmaps = mutableListOf<Bitmap?>()
    val requestedChatHistories = mutableListOf<List<Pair<String, String>>>()
    val requestedMimeTypes = mutableListOf<String?>()
    val requestedTemperatures = mutableListOf<Float?>()

    override suspend fun generateContent(
        prompt: String,
        systemInstruction: String?,
        bitmap: Bitmap?,
        chatHistory: List<Pair<String, String>>,
        temperature: Float?,
        responseMimeType: String?
    ): String {
        requestedPrompts.add(prompt)
        requestedSystemInstructions.add(systemInstruction)
        requestedBitmaps.add(bitmap)
        requestedChatHistories.add(chatHistory)
        requestedMimeTypes.add(responseMimeType)
        requestedTemperatures.add(temperature)

        if (unconfigured) {
            throw IllegalStateException("Firebase ve AI Logic yapılandırması bulunamadı. Lütfen google-services.json dosyasını ekleyin.")
        }
        if (shouldThrow) {
            throw exceptionToThrow
        }
        if (mockResponse.isEmpty()) {
            throw Exception("AI yanıtı boş döndü.")
        }
        return mockResponse
    }
}
