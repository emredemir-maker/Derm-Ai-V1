import re

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

# Add MakeupAnalysisResult class
makeup_result_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
data class MakeupAnalysisResult(
    val overallEvaluation: String = "",
    val whatToDo: List<String> = emptyList(),
    val whatNotToDo: List<String> = emptyList()
)
"""
# Insert before @com.squareup.moshi.JsonClass(generateAdapter = true)
# or after ProfileAnalysisResult

target = "@com.squareup.moshi.JsonClass(generateAdapter = true)\ndata class GeminiRecommendationResponse"
content = content.replace(target, makeup_result_class + "\n" + target)

# Add analyzeMakeup function
makeup_func = """
    suspend fun analyzeMakeup(photoPath: String): MakeupAnalysisResult? {
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
            Sen profesyonel bir Güzellik Uzmanı, Makyaj Artisti ve Yapay Zeka Analistisin.
            Gönderilen fotoğraftaki kişinin makyajını analiz edip şu başlıklarda detaylı öneriler sunmalısın:
            1. Genel değerlendirme (Kullanıcının yüz hatlarına makyajın uyumu, renk seçimleri).
            2. Neler Yapmalısınız? (Daha iyi bir görünüm için uygulanması gereken doğru makyaj teknikleri).
            3. Neler Yapmamalısınız? (Yapılan hatalar veya yüz hatlarına uygun olmayan teknikler).

            Yanıtını SADECE aşağıdaki JSON formatında vermelisin:
            {
                "overallEvaluation": "Genel değerlendirme metni...",
                "whatToDo": ["Daha aydınlık bir göz altı için...", "Allığı elmacık kemiklerinin üzerine..."],
                "whatNotToDo": ["Koyu renk rujlardan kaçının çünkü...", "Göz pınarlarında mat far kullanmayın..."]
            }
        \"\"\".trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Bu fotoğraftaki makyajı analiz et ve bana neler yapıp yapmamam gerektiğini söyle."),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.3f, responseMimeType = "application/json")
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            val adapter = moshi.adapter(MakeupAnalysisResult::class.java)
            adapter.fromJson(text)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
"""

content = content.replace("suspend fun fetchCustomRecommendations(", makeup_func + "\n    suspend fun fetchCustomRecommendations(")

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)

