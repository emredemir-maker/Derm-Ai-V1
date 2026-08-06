package com.example.data.api

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAiModelClient : AiModelClient {
    override suspend fun generateContent(
        prompt: String,
        systemInstruction: String?,
        bitmap: Bitmap?,
        chatHistory: List<Pair<String, String>>,
        temperature: Float?,
        responseMimeType: String?
    ): String = withContext(Dispatchers.IO) {
        try {
            val app = FirebaseApp.getInstance()
            val model = Firebase.ai(app).generativeModel(
                modelName = "gemini-3.5-flash",
                generationConfig = generationConfig {
                    temperature?.let { this.temperature = it }
                    responseMimeType?.let { this.responseMimeType = it }
                },
                systemInstruction = systemInstruction?.let {
                    content { text(it) }
                }
            )

            val response = if (bitmap != null) {
                model.generateContent(
                    content {
                        text(prompt)
                        image(bitmap)
                    }
                )
            } else if (chatHistory.isNotEmpty()) {
                val history = chatHistory.map { (role, text) ->
                    content(role) { text(text) }
                }
                val chat = model.startChat(history = history)
                chat.sendMessage(prompt)
            } else {
                model.generateContent(prompt)
            }

            response.text ?: throw Exception("AI yanıtı boş döndü.")
        } catch (e: Exception) {
            val message = e.message ?: "Bilinmeyen hata"
            if (message.contains("Default FirebaseApp is not initialized") || message.contains("google-services.json")) {
                throw IllegalStateException("Firebase ve AI Logic yapılandırması bulunamadı. Lütfen google-services.json dosyasını ekleyin.")
            } else {
                throw Exception("Firebase AI Hatası: $message", e)
            }
        }
    }
}
