import json

append_str = """
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val moshi = com.squareup.moshi.Moshi.Builder()
                .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(GeminiRecommendationResponse::class.java)
            adapter.fromJson(cleanJson(jsonText))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun analyzeProductIngredients(
        photoPath: String?,
        rawIngredientsText: String?,
        skinType: String,
        skinConcerns: String,
        skincareGoal: String,
        allergies: String
    ): IngredientAnalysisResponse? {
        val systemPrompt = \"\"\"
            Sen kozmetik formülasyonları, içerik listelerini ve dermatolojiyi çok iyi bilen uzman bir Dermatolog ve Kimyagersin.
            Kullanıcının gönderdiği ürün içerik etiket resmini (veya yazılı metni) okumalı, içerikleri analiz etmeli ve kullanıcının cilt profiline göre alerjen/uyumluluk kontrolü yapmalısın.
            
            Kullanıcının Cilt Profili:
            - Cilt Tipi: $skinType
            - Cilt Hassasiyetleri/Sorunları: $skinConcerns
            - Cilt Bakım Hedefi: $skincareGoal
            - Alerjiler: $allergies
            
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir açıklama bulunmamalıdır. SADECE geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "productName": "Okunan veya tahmin edilen ürün adı (örn: Hyaluronic Acid Moisturizer veya Bilinmeyen Ürün)",
              "compatibilityScore": 85, // 0-100 arası sayısal uyum skoru
              "compatibilityLabel": "Yüksek Uyum / Kısmi Uyum / Uyumsuz",
              "compatibilityExplanation": "Ürünün kullanıcının cilt tipi ve sorunlarıyla neden uyumlu veya uyumsuz olduğuna dair kısa bir açıklama.",
              "detectedIngredients": ["İçerik 1", "İçerik 2", "İçerik 3"], // Tespit edilen tüm aktif ve temel içerikler
              "allergensAndIrritants": [
                {
                  "ingredientName": "Zararlı veya tahriş edici olabilecek madde adı (örn: Denatured Alcohol)",
                  "severity": "Yüksek / Orta / Düşük", // Kullanıcının cilt durumuna göre risk düzeyi
                  "riskDescription": "Neden riskli olduğu (örn: Kuru ciltlerde nem kaybına ve tahrişe yol açabilir)"
                }
              ],
              "beneficialIngredients": [
                {
                  "ingredientName": "Faydalı bileşen adı (örn: Niacinamide)",
                  "benefitDescription": "Neden faydalı olduğu (örn: Sivilce kontrolünü sağlar ve cilt bariyerini onarır)"
                }
              ],
              "finalVerdict": "Bu ürünü kullanmalı mı, kullanmamalı mı veya dikkatli mi kullanmalı? (Örn: Bu ürün cilt tipinizle mükemmel uyum sağlıyor, güvenle kullanabilirsiniz.)",
              "usageTips": "Bu içerik kombinasyonuna göre özel bir kullanım önerisi (Örn: Sabahları güneş kremi ile birlikte kullanın veya nemli cilde uygulayın.)"
            }
            
            Önemli kurallar:
            1. GERÇEKÇİ OLUN: Her ürüne %90 ve üzeri puan VERMEYİN. Ortalama veya sorunlu ürünler için 40-70 arası, eğer kullanıcının belirttiği alerjilerden ("$allergies") biri varsa puanı %30'un altına düşür ve "Uyumsuz" etiketle.
            2. YUVARLAK ŞİŞELER: Görüntü yuvarlak veya kavisli bir şişeden çekilmiş olabilir, metinler eğik, bozuk veya eksik harfli olabilir. OCR hatalarını düzelt, eksik harfleri tahmin ederek doğru INCI isimlerini mantıksal olarak çıkar.
            3. Eğer resimdeki veya metindeki içerik listesi tamamen okunaksızsa veya kozmetik ürün içeriği değilse bile, kullanıcıya boş bir şablon yerine örnek bir analiz döndürme; productName alanını "Okunamadı" yap ve compatibilityExplanation alanında durumu açıkla.
            4. Tüm açıklamalar ve madde adları TÜRKÇE veya uluslararası kozmetik (INCI) isimleri ile verilmelidir.
            5. Alerjen ve tahriş edici maddeleri tespit ederken kullanıcının Hassas, Kuru veya Yağlı cilt tipini ve özellikle Alerjilerini göz önünde bulundur.
        \"\"\".trimIndent()

        val userPrompt = if (!rawIngredientsText.isNullOrBlank()) {
            "İşte analiz etmeniz için ürünün içerik listesi:\n$rawIngredientsText"
        } else {
            "Lütfen ekteki ürün etiketinden içerikleri tarayıp analiz edin."
        }

        val request = if (!photoPath.isNullOrBlank()) {
            val file = java.io.File(photoPath)
            if (!file.exists()) return null

            val base64Data = try {
                val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath) ?: return null
                val maxDimension = 1024 
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
                resizedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, outputStream)
                android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            GenerateContentRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = userPrompt),
                            Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                        )
                    )
                ),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                generationConfig = GenerationConfig(
                    temperature = 0.4f,
                    responseMimeType = "application/json"
                )
            )
        } else {
            GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
                systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                generationConfig = GenerationConfig(
                    temperature = 0.4f,
                    responseMimeType = "application/json"
                )
            )
        }

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val moshi = com.squareup.moshi.Moshi.Builder()
                .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(IngredientAnalysisResponse::class.java)
            adapter.fromJson(cleanJson(jsonText))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchMarketRecommendations(
        skinType: String,
        concerns: String,
        goal: String,
        makeup: String
    ): MarketProductListResponse? {
        val systemPrompt = \"\"\"
            Sen kozmetik piyasasını, popüler marka/ürünlerin içerik ve fiyat/performans (F/P) oranlarını çok iyi analiz eden uzman bir Kozmetik Formülatör ve Satın Alma Danışmanısın.
            Kullanıcının cilt profiline göre piyasadaki en uygun bütçe dostu, orta segment ve premium seçenekleri araştırıp fiyat-performans ve cilt uyum derecelerini içeren bir liste hazırlamalısın.
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir açıklama bulunmamalıdır. SADECE geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "products": [
                {
                  "name": "Ürün Adı",
                  "brand": "Marka",
                  "category": "Kategori",
                  "priceSegment": "Bütçe Dostu / Orta Segment / Premium",
                  "matchScore": 95,
                  "reason": "Neden önerildiği"
                }
              ]
            }
        \"\"\".trimIndent()

        val userPrompt = \"Cilt: $skinType, Sorunlar: $concerns, Hedef: $goal\"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            moshi.adapter(MarketProductListResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }

    suspend fun getPurchaseAdvice(
        productName: String,
        brand: String,
        skinType: String,
        concerns: String,
        goal: String
    ): PurchaseAdviceResponse? {
        val systemPrompt = \"\"\"
            Sen kullanıcıların satın almak istediği kozmetik ürünleri onların cilt tipine göre analiz edip 'AL', 'ALMA' veya 'DİKKATLİ KULLAN' şeklinde net dermatolojik ve ekonomik tavsiye veren bir Güzellik ve Alışveriş Asistanısın.
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir açıklama bulunmamalıdır. SADECE geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "advice": "AL / ALMA / DİKKATLİ KULLAN",
              "reason": "Açıklama",
              "alternatives": ["Alternatif 1", "Alternatif 2"]
            }
        \"\"\".trimIndent()
        
        val userPrompt = "Ürün: $brand $productName. Cilt: $skinType, Sorunlar: $concerns, Hedef: $goal"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            moshi.adapter(PurchaseAdviceResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }

    suspend fun checkWeeklyInventory(
        inventoryJson: String,
        skinType: String,
        concerns: String,
        goal: String
    ): WeeklyInventoryCheckResponse? {
        val systemPrompt = \"\"\"
            Sen kullanıcının elindeki tüm kozmetik/makyaj ürünlerini içeren envanter listesini tarayan, aralarındaki içerik çelişkilerini (örn: Retinol ile C Vitamini veya AHA/BHA'ların birlikte yanlış kullanımı) tespit eden ve haftalık envanter kullanım rehberi hazırlayan uzman bir Dermatolog ve Kimyagersin.
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir açıklama bulunmamalıdır. SADECE geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "conflicts": [
                {
                  "products": ["Ürün 1", "Ürün 2"],
                  "reason": "Neden birlikte kullanılmamalı"
                }
              ],
              "routine": "Haftalık kullanım rutini tavsiyesi"
            }
        \"\"\".trimIndent()
        
        val userPrompt = "Cilt: $skinType, Envanter: $inventoryJson"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            moshi.adapter(WeeklyInventoryCheckResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }

    suspend fun fetchProductPrices(
        productName: String,
        category: String
    ): ProductPriceComparisonResponse? {
        val systemPrompt = \"\"\"
            Sen kozmetik ve cilt bakım ürünlerinin Türkiye pazarındaki (Trendyol, Hepsiburada, Amazon.tr, Watsons, Gratis gibi büyük e-ticaret siteleri) güncel fiyatlarını, kampanyalarını ve kargo seçeneklerini takip eden akıllı bir Fiyat Karşılaştırma Asistanısın.
            Sana verilen ürün adı için bu platformlardaki en güncel, gerçekçi, bütçe dostu fiyat seçeneklerini listelemelisin.
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir açıklama bulunmamalıdır. SADECE geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "prices": [
                {
                  "platform": "Platform Adı",
                  "price": "Fiyat",
                  "link": "Link (Opsiyonel)"
                }
              ]
            }
        \"\"\".trimIndent()
        
        val userPrompt = "Ürün: $productName"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()).build()
            moshi.adapter(ProductPriceComparisonResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }
}
"""

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "a") as f:
    f.write(append_str)

