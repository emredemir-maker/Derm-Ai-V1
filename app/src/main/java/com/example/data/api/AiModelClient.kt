package com.example.data.api

import android.graphics.Bitmap

interface AiModelClient {
    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        bitmap: Bitmap? = null,
        chatHistory: List<Pair<String, String>> = emptyList(),
        temperature: Float? = null,
        responseMimeType: String? = null
    ): String
}
