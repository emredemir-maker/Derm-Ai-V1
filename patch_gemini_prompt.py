import re

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

old_prompt_block = """        val systemPrompt = \"\"\"
            Sen profesyonel bir Dermatolog ve Yapay Zeka Cilt Analiz Uzmanısın.
            Gönderilen cilt selfie fotoğrafını analiz ederek kullanıcının cilt özelliklerini tahmin etmelisin.
            
            Yanıtını SADECE aşağıdaki şablona tam olarak uyarak ver. Başka hiçbir açıklama, giriş veya çıkış metni ekleme.
            
            ŞABLON:
            SKIN_TYPE: [Değer]
            CONCERNS: [Değerler]
            GOAL: [Değer]
            EXPLANATION: [Açıklama]
            CONFIDENCE: [Değer]
            
            Geçerli Değerler:
            - SKIN_TYPE için şunlardan sadece biri olmalıdır: Kuru, Yağlı, Karma, Hassas, Normal
            - CONCERNS için şunlardan biri veya birkaçı virgülle ayrılmış olmalıdır (veya bulunamazsa boş bırakılabilir): Akne & Sivilce, Siyah Noktalar, Geniş Gözenekler, Lekeler & Pigmentasyon, Kırışıklık & İnce Çizgiler, Kızarıklık, Kuruluk & Pullanma
            - GOAL için şunlardan sadece biri olmalıdır: Nemlendirme, Aydınlatma & Parlaklık, Yaşlanma Karşıtı (Anti-Aging), Sivilce Kontrolü, Cilt Bariyeri Güçlendirme
            - EXPLANATION için: Cildi fotoğraftan nasıl analiz ettiğini açıklayan samimi, Türkçe, maksimum 2 kısa cümlelik bir açıklama.
            - CONFIDENCE için: 0 ile 100 arasında bir tamsayı. Görüntü kalitesi ve analizine ne kadar güvendiğini belirt.
        \"\"\".trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = "Bu yüzü analiz et ve profili çıkar."),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )"""

new_prompt_block = """        val systemPrompt = \"\"\"
            Sen profesyonel bir Dermatolog ve Yapay Zeka Makyaj/Cilt Analiz Uzmanısın.
            Gönderilen cilt selfie (ön veya profil) fotoğrafını detaylı analiz etmelisin.
            Göz bölgesindeki sorunlar, makyajın durumu (makyaj varsa değerlendirmesi ve cilde etkileri), ciltteki eksiklikleri tespit et.
            Ayrıca tespit ettiğin bu sorunların yüzde nerede olduğunu yaklaşık (x, y) koordinatlarıyla (0.0 ile 1.0 arasında) ver.
            
            Yanıtını SADECE aşağıdaki JSON formatında vermelisin. Başka metin veya markdown ekleme (```json gibi şeyler koyma).
            
            {
                "skinType": "Kuru | Yağlı | Karma | Hassas | Normal",
                "concerns": ["Akne", "Kırışıklık", ...],
                "goal": "Nemlendirme | Sivilce Kontrolü | ...",
                "explanation": "Genel cilt durumunun analizi...",
                "eyeAreaAnalysis": "Göz bölgesinin detaylı analizi (morluk, ince çizgi vb.)",
                "makeupEvaluation": "Makyaj varsa değerlendirmesi, yoksa 'Makyaj tespit edilmedi'. Makyajın cilde olası etkileri.",
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
        )"""

content = content.replace(old_prompt_block, new_prompt_block)

old_parsing_block = """        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val typeMatch = Regex("SKIN_TYPE:\\s*(.+)").find(text)
            val concernsMatch = Regex("CONCERNS:\\s*(.+)").find(text)
            val goalMatch = Regex("GOAL:\\s*(.+)").find(text)
            val explMatch = Regex("EXPLANATION:\\s*(.+)").find(text)
            val confMatch = Regex("CONFIDENCE:\\s*(\\d+)").find(text)
            
            if (typeMatch != null && goalMatch != null) {
                val concerns = concernsMatch?.groupValues?.get(1)?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
                ProfileAnalysisResult(
                    skinType = typeMatch.groupValues[1].trim(),
                    concerns = concerns,
                    goal = goalMatch.groupValues[1].trim(),
                    explanation = explMatch?.groupValues?.get(1)?.trim() ?: "",
                    confidenceScore = confMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }"""

new_parsing_block = """        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            val adapter = moshi.adapter(ProfileAnalysisResult::class.java)
            adapter.fromJson(text)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }"""
content = content.replace(old_parsing_block, new_parsing_block)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
