package com.example.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.squareup.moshi.JsonClass
import java.io.File

@JsonClass(generateAdapter = true)
data class FaceRegionIssue(
    val regionName: String = "",
    val issue: String = "",
    val recommendedIngredient: String = "",
    val x: Float = 0f,
    val y: Float = 0f
)

@JsonClass(generateAdapter = true)
data class ProfileAnalysisResult(
    val skinType: String = "",
    val concerns: List<String> = emptyList(),
    val goal: String = "",
    val explanation: String = "",
    val eyeAreaAnalysis: String = "",
    val makeupEvaluation: String = "",
    val skinHealthScore: Int = 0,
    val confidenceScore: Int = 0,
    val faceMapRegions: List<FaceRegionIssue> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MakeupAnalysisResult(
    val overallEvaluation: String = "",
    val whatToDo: List<String> = emptyList(),
    val whatNotToDo: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeminiRecommendationResponse(
    val skinType: String,
    val creamSuggestions: List<com.example.data.database.ProductSuggestion>,
    val makeupSuggestions: List<com.example.data.database.ProductSuggestion>,
    val generalTips: String
)

@JsonClass(generateAdapter = true)
data class IngredientIssue(
    val ingredientName: String = "",
    val severity: String = "",
    val riskDescription: String = ""
)

@JsonClass(generateAdapter = true)
data class IngredientBenefit(
    val ingredientName: String = "",
    val benefitDescription: String = ""
)

@JsonClass(generateAdapter = true)
data class IngredientAnalysisResponse(
    val productName: String = "",
    val compatibilityScore: Int = 0,
    val compatibilityLabel: String = "",
    val compatibilityExplanation: String = "",
    val detectedIngredients: List<String> = emptyList(),
    val allergensAndIrritants: List<IngredientIssue> = emptyList(),
    val beneficialIngredients: List<IngredientBenefit> = emptyList(),
    val finalVerdict: String = "",
    val usageTips: String = "",
    val confidenceScore: Int = 0
)

@JsonClass(generateAdapter = true)
data class MarketProduct(
    val name: String = "",
    val productName: String = "",
    val brand: String = "",
    val category: String = "",
    val priceSegment: String = "",
    val priceTier: String = "",
    val matchScore: Int = 0,
    val compatibilityScore: Int = 0,
    val reason: String = "",
    val estimatedPrice: String = "",
    val keyActiveIngredients: String = "",
    val prosDescription: String = "",
    val valueScore: Int = 0,
    val finalVerdict: String = ""
)

@JsonClass(generateAdapter = true)
data class MarketProductListResponse(val products: List<MarketProduct> = emptyList(), val recommendations: List<MarketProduct> = emptyList())

@JsonClass(generateAdapter = true)
data class PurchaseAdviceResponse(
    val advice: String = "",
    val reason: String = "",
    val alternatives: List<String> = emptyList(),
    val brand: String = "",
    val productName: String = "",
    val suitabilityScore: Int = 0,
    val verdict: String = "",
    val reasoning: String = "",
    val positiveIngredients: List<String> = emptyList(),
    val riskyIngredients: List<String> = emptyList(),
    val alternativeSuggestions: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class InventoryConflict(
    val products: List<String> = emptyList(),
    val reason: String = "",
    val productA: String = "",
    val productB: String = "",
    val conflictReason: String = "",
    val solution: String = ""
)

@JsonClass(generateAdapter = true)
data class RoutineStep(
    val timeOfDay: String = "",
    val steps: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WeeklyInventoryCheckResponse(
    val conflicts: List<InventoryConflict> = emptyList(),
    val routine: String = "",
    val generalAnalysis: String = "",
    val ingredientConflicts: List<InventoryConflict> = emptyList(),
    val routineSuggestions: List<RoutineStep> = emptyList(),
    val missingItems: List<String> = emptyList(),
    val nextWeekFocus: String = ""
)

object GeminiRepository {
    var aiClient: AiModelClient = FirebaseAiModelClient()

    private fun cleanJson(jsonText: String): String {
        var cleaned = jsonText.trim()
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```json").removePrefix("```").trim()
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.removeSuffix("```").trim()
        }
        return cleaned
    }

    suspend fun getSkinCareAnalysis(
        skinType: String,
        concerns: String,
        goal: String,
        makeup: String,
        age: Int = 0,
        gender: String = ""
    ): Pair<String, String> {
        val systemPrompt = """
            Sen profesyonel bir Dermatolog, Cilt Bakım Uzmanı (Estetisyen) ve Makyaj Artistisin.
            Kullanıcıya cilt tipine, cilt sorunlarına, bakım hedeflerine, yaşına ve paylaşmayı seçtiği cinsiyet bilgisine göre kişiselleştirilmiş, bilimsel ve pratik öneriler sunmalısın.
            Yaş ve cinsiyet bilgisini yalnızca içerik güvenliği ve bakım ihtiyacını bağlama oturtmak için kullan; kalıp yargı üretme, tanı koyma veya yalnızca cinsiyete dayanarak ürün önerme.
            
            Yanıtını her zaman TÜRKÇE vermeli ve iki ayrı ana bölüme ayırmalısın:
            1. BÖLÜM: CİLT BAKIM RUTİNİ VE TAVSİYELERİ (Sabah ve Akşam rutinleri, önerilen aktif bileşenler örn. Salisilik Asit, C Vitamini, Retinol, vb.)
            2. BÖLÜM: MAKYAJ TAVSİYELERİ (Cilt tipine uygun fondöten yapısı, kapatıcı, gözenek gizleyici ve makyaj temizleme önerileri)
            
            Yanıtlarında net başlıklar, kısa maddeler ve samimi ama profesyonel bir dil kullan.
        """.trimIndent()

        val userPrompt = """
            Cilt Bilgilerim:
            - Cilt Tipi: $skinType
            - Cilt Sorunları/Şikayetleri: $concerns
            - Cilt Bakım Hedefi: $goal
            - Makyaj Tercihi: $makeup
            - Yaş: ${if (age > 0) age else "Belirtilmedi"}
            - Cinsiyet: ${gender.ifBlank { "Belirtilmedi" }}
            
            Lütfen bana uygun ürün tiplerini (krem, temizleyici, güneş kremi vs.) içeren detaylı günlük rutinleri ve cilt tipime zarar vermeyecek makyaj tüyolarını hazırlar mısın?
        """.trimIndent()

        return try {
            val fullText = aiClient.generateContent(
                prompt = userPrompt,
                systemInstruction = systemPrompt,
                temperature = 0.7f
            )
            val sections = fullText.split("2. BÖLÜM:", "Makyaj Tavsiyeleri", "MAKYAJ TAVSİYELERİ", ignoreCase = true)
            val routineText = sections.getOrNull(0) ?: fullText
            val makeupText = if (sections.size > 1) {
                sections.subList(1, sections.size).joinToString("\n")
            } else {
                "Cilt tipinize uygun doğal tonlar, su bazlı kapatıcılar ve hafif nemlendiricili ten ürünleri kullanabilirsiniz."
            }
            Pair(routineText.trim(), makeupText.trim())
        } catch (e: Exception) {
            e.printStackTrace()
            Pair("Hata oluştu: ${e.message}", "Makyaj tavsiyeleri yüklenemedi.")
        }
    }

    suspend fun getChatResponse(
        userMessage: String,
        skinProfile: String,
        chatHistory: List<Pair<String, String>> = emptyList()
    ): String {
        val systemPrompt = """
            Sen DermaAI adında yardımsever ve profesyonel bir yapay zeka cilt bakımı ve güzellik danışmanısın.
            Kullanıcının cilt profili şudur: $skinProfile.
            Kullanıcının sorularına (krem önerileri, sivilceler, gözenekler, makyaj tüyoları, ürün içerikleri vb.) bu profile sadık kalarak dermatolojik açıdan doğru, güvenli ve pratik yanıtlar ver.
            Önerdiğin ürünlerin ilaç olmadığını, ciddi cilt hastalıkları için bir dermatoloğa görünmeleri gerektiğini nazikçe hatırlat.
            Yanıtları kısa, anlaşılır ve Türkçe olarak ver.
        """.trimIndent()

        return try {
            aiClient.generateContent(
                prompt = userMessage,
                systemInstruction = systemPrompt,
                chatHistory = chatHistory,
                temperature = 0.7f
            )
        } catch (e: Exception) {
            e.printStackTrace()
            "Hata oluştu: ${e.message}"
        }
    }

    suspend fun analyzeSkinDiaryNote(note: String, rating: Int): String {
        val systemPrompt = """
            Kullanıcının günlük cilt günlüğü notuna ve cildinin o günkü iyi/kötü durum puanına (1-5 arası, 5 en iyi) göre kısa (maksimum 2-3 cümlelik) bir AI yorumu yaz.
            Dost canlısı, motive edici ve rehberlik edici bir ton kullan. Türkçe yaz.
        """.trimIndent()
        val prompt = "Cilt Durum Puanı: $rating/5\nGünlük Notu: $note"

        return try {
            aiClient.generateContent(prompt = prompt, systemInstruction = systemPrompt, temperature = 0.8f)
        } catch (e: Exception) {
            "Bugün de kendine vakit ayırdığın için harikasın! Bakım adımlarını ihmal etme."
        }
    }

    private fun processBitmap(photoPath: String): Bitmap? {
        val file = File(photoPath)
        if (!file.exists()) return null
        return try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return null
            val maxDimension = 768
            val width = bitmap.width
            val height = bitmap.height
            if (width > maxDimension || height > maxDimension) {
                val srcAspect = width.toFloat() / height.toFloat()
                val (newWidth, newHeight) = if (srcAspect > 1.0f) {
                    Pair(maxDimension, (maxDimension / srcAspect).toInt())
                } else {
                    Pair((maxDimension * srcAspect).toInt(), maxDimension)
                }
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun analyzeSkinPhoto(photoPath: String, userNote: String): String {
        val bitmap = processBitmap(photoPath) ?: return "Fotoğraf okunamadı."
        val systemPrompt = """
            Sen profesyonel bir Dermatolog ve Yapay Zeka Cilt Analiz Uzmanısın.
            Kullanıcının gönderdiği cilt fotoğrafını ve varsa günlüğe eklediği notu analiz etmelisin.
            Yanıtı kısa, net, samimi, Türkçe ve maksimum 4-5 cümle olarak yaz.
        """.trimIndent()
        val promptText = if (userNote.isNotBlank()) {
            "Kullanıcı Notu: $userNote\nLütfen bu fotoğrafı ve notu birlikte analiz edip geri bildirim ver."
        } else {
            "Lütfen bu cilt fotoğrafını analiz edip durum ve tavsiye raporu sun."
        }

        return try {
            aiClient.generateContent(prompt = promptText, systemInstruction = systemPrompt, bitmap = bitmap, temperature = 0.5f)
        } catch (e: Exception) {
            "Fotoğraf analizi hatası: ${e.message}"
        }
    }

    suspend fun analyzeSkinForProfile(photoPath: String): ProfileAnalysisResult? {
        val bitmap = processBitmap(photoPath) ?: return null
        val systemPrompt = """
            Sen profesyonel bir Dermatolog ve Yapay Zeka Makyaj/Cilt Analiz Uzmanısın.
            Gönderilen cilt selfie fotoğrafını detaylı analiz et.
            Yanıtını SADECE aşağıdaki JSON formatında vermelisin. Başka metin ekleme.
            {
                "skinType": "Kuru | Yağlı | Karma | Hassas | Normal",
                "concerns": ["Akne & Sivilce", "Lekeler & Pigmentasyon"],
                "goal": "Nemlendirme | Sivilce Kontrolü",
                "explanation": "Cilt durumunun analizi...",
                "eyeAreaAnalysis": "Göz bölgesi analizi",
                "makeupEvaluation": "Makyaj değerlendirmesi",
                "skinHealthScore": 62,
                "confidenceScore": 95,
                "faceMapRegions": []
            }
        """.trimIndent()

        return try {
            val text = aiClient.generateContent(
                prompt = "Bu yüzü çok detaylı analiz et.",
                systemInstruction = systemPrompt,
                bitmap = bitmap,
                temperature = 0.3f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(ProfileAnalysisResult::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun analyzeMakeup(photoPath: String): MakeupAnalysisResult? {
        val bitmap = processBitmap(photoPath) ?: return null
        val systemPrompt = """
            Sen profesyonel bir Güzellik Uzmanı ve Makyaj Artisti ve Yapay Zeka Analistisin.
            Yanıtını SADECE aşağıdaki JSON formatında vermelisin:
            {
                "overallEvaluation": "Genel değerlendirme...",
                "whatToDo": ["..."],
                "whatNotToDo": ["..."]
            }
        """.trimIndent()

        return try {
            val text = aiClient.generateContent(
                prompt = "Bu fotoğraftaki makyajı analiz et.",
                systemInstruction = systemPrompt,
                bitmap = bitmap,
                temperature = 0.3f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(MakeupAnalysisResult::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchCustomRecommendations(
        skinType: String,
        concerns: String,
        goal: String,
        makeup: String,
        allergies: String,
        age: Int = 0,
        gender: String = ""
    ): GeminiRecommendationResponse? {
        val systemPrompt = """
            Sen profesyonel bir Dermatolog ve Kozmetologsun. SADECE geçerli bir JSON döndürmelisin:
            {
              "skinType": "...",
              "creamSuggestions": [],
              "makeupSuggestions": [],
              "generalTips": "..."
            }
        """.trimIndent()
        val userPrompt = "Cilt: $skinType, Sorunlar: $concerns, Hedef: $goal, Alerjiler: $allergies, Yaş: ${if (age > 0) age else "Belirtilmedi"}, Cinsiyet: ${gender.ifBlank { "Belirtilmedi" }}. Yaş ve cinsiyeti yalnızca güvenli ve ilgili bakım bağlamında kullan; kalıp yargı veya tıbbi tanı üretme."

        return try {
            val text = aiClient.generateContent(
                prompt = userPrompt,
                systemInstruction = systemPrompt,
                temperature = 0.5f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(GeminiRecommendationResponse::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
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
        val bitmap = if (!photoPath.isNullOrBlank()) processBitmap(photoPath) else null
        val systemPrompt = """
            Sen uzman bir Dermatolog ve Kimyagersin. SADECE geçerli bir JSON döndürmelisin:
            {
              "productName": "...",
              "compatibilityScore": 85,
              "compatibilityLabel": "...",
              "compatibilityExplanation": "...",
              "detectedIngredients": [],
              "allergensAndIrritants": [],
              "beneficialIngredients": [],
              "finalVerdict": "...",
              "usageTips": "...",
              "confidenceScore": 85
            }
        """.trimIndent()
        val userPrompt = if (!rawIngredientsText.isNullOrBlank()) rawIngredientsText else "İçerik etiketini analiz et."

        return try {
            val text = aiClient.generateContent(
                prompt = userPrompt,
                systemInstruction = systemPrompt,
                bitmap = bitmap,
                temperature = 0.4f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(IngredientAnalysisResponse::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchMarketRecommendations(
        skinType: String,
        concerns: String,
        goal: String,
        makeup: String,
        age: Int = 0,
        gender: String = ""
    ): MarketProductListResponse? {
        val systemPrompt = """
            Sen uzman bir Kozmetik Formülatörsün. SADECE geçerli bir JSON döndürmelisin:
            { "products": [] }
        """.trimIndent()
        val userPrompt = "Cilt: $skinType, Sorunlar: $concerns, Hedef: $goal, Yaş: ${if (age > 0) age else "Belirtilmedi"}, Cinsiyet: ${gender.ifBlank { "Belirtilmedi" }}"

        return try {
            val text = aiClient.generateContent(
                prompt = userPrompt,
                systemInstruction = systemPrompt,
                temperature = 0.5f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(MarketProductListResponse::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPurchaseAdvice(
        productName: String,
        brand: String,
        skinType: String,
        concerns: String,
        goal: String
    ): PurchaseAdviceResponse? {
        val systemPrompt = """
            Sen bir Güzellik ve Alışveriş Asistanısın. SADECE geçerli bir JSON döndürmelisin:
            { "advice": "AL", "reason": "...", "alternatives": [] }
        """.trimIndent()
        val userPrompt = "Ürün: $brand $productName. Cilt: $skinType"

        return try {
            val text = aiClient.generateContent(
                prompt = userPrompt,
                systemInstruction = systemPrompt,
                temperature = 0.5f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(PurchaseAdviceResponse::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
            null
        }
    }

    suspend fun checkWeeklyInventory(
        inventoryJson: String,
        skinType: String,
        concerns: String,
        goal: String
    ): WeeklyInventoryCheckResponse? {
        val systemPrompt = """
            Sen bir Dermatolog ve Kimyagersin. SADECE geçerli bir JSON döndürmelisin:
            { "conflicts": [], "routine": "..." }
        """.trimIndent()
        val userPrompt = "Cilt: $skinType, Envanter: $inventoryJson"

        return try {
            val text = aiClient.generateContent(
                prompt = userPrompt,
                systemInstruction = systemPrompt,
                temperature = 0.5f,
                responseMimeType = "application/json"
            )
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(WeeklyInventoryCheckResponse::class.java).fromJson(cleanJson(text))
        } catch (e: Exception) {
            null
        }
    }
}
