import re

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

# Find the start and end of analyzeSkinForProfile
start_index = content.find("suspend fun analyzeSkinForProfile(photoPath: String): ProfileAnalysisResult? {")

if start_index == -1:
    print("Could not find analyzeSkinForProfile")
    exit(1)

# Find the end of the method. We can look for the next method: "suspend fun fetchCustomRecommendations"
end_index = content.find("suspend fun fetchCustomRecommendations", start_index)

if end_index == -1:
    print("Could not find end of method")
    exit(1)

new_method = """suspend fun analyzeSkinForProfile(photoPath: String): ProfileAnalysisResult? {
        val file = java.io.File(photoPath)
        if (!file.exists()) return null
        
        val base64Data = try {
            val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath) ?: return null
            val maxDimension = 768
            val width = bitmap.width
            val height = bitmap.height
            val resizedBitmap = if (width > maxDimension || height > maxDimension) {
                val srcAspect = width.toFloat() / height.toFloat()
                val (newWidth, newHeight) = if (srcAspect > 1.0f) {
                    Pair(maxDimension, (maxDimension / srcAspect).toInt())
                } else {
                    Pair((maxDimension * srcAspect).toInt(), maxDimension)
                }
                android.graphics.Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
            val outputStream = java.io.ByteArrayOutputStream()
            resizedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
            android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val systemPrompt = \"\"\"
            Sen profesyonel bir Dermatolog ve Yapay Zeka Makyaj/Cilt Analiz Uzmanısın.
            Gönderilen cilt selfie (ön veya profil) fotoğrafını detaylı analiz etmelisin.
            Göz bölgesindeki sorunlar, makyajın durumu (makyaj varsa değerlendirmesi ve cilde etkileri), ciltteki eksiklikleri tespit et.
            Ayrıca tespit ettiğin bu sorunların yüzde nerede olduğunu yaklaşık (x, y) koordinatlarıyla (0.0 ile 1.0 arasında) ver.
            
            Yanıtını SADECE aşağıdaki JSON formatında vermelisin. Başka metin veya markdown ekleme (```json gibi şeyler koyma).
            
            {
                "skinType": "Kuru | Yağlı | Karma | Hassas | Normal",
                "concerns": ["Akne", "Kırışıklık"],
                "goal": "Nemlendirme | Sivilce Kontrolü",
                "explanation": "Genel cilt durumunun analizi...",
                "eyeAreaAnalysis": "Göz bölgesinin detaylı analizi",
                "makeupEvaluation": "Makyaj tespit edilmedi / Varsa değerlendirmesi",
                "confidenceScore": 95,
                "faceMapRegions": [
                    {
                        "regionName": "Alın",
                        "issue": "Kuru bölge",
                        "recommendedIngredient": "Hyalüronik Asit",
                        "x": 0.5,
                        "y": 0.2
                    }
                ]
            }
        \"\"\".trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Bu yüzü çok detaylı analiz et, göz bölgesi, profil/ön durumu, makyaj varsa doğruluğu ve yüz haritası çıkar."),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.3f, response_mime_type = "application/json")
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            val adapter = moshi.adapter(ProfileAnalysisResult::class.java)
            adapter.fromJson(text)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    """

content = content[:start_index] + new_method + content[end_index:]

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
