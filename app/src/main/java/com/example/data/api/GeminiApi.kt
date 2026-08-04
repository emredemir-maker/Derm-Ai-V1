package com.example.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class FaceRegionIssue(
    val regionName: String = "",
    val issue: String = "",
    val recommendedIngredient: String = "",
    val x: Float = 0f,
    val y: Float = 0f
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
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

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class MakeupAnalysisResult(
    val overallEvaluation: String = "",
    val whatToDo: List<String> = emptyList(),
    val whatNotToDo: List<String> = emptyList()
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class GeminiRecommendationResponse(
    val skinType: String,
    val creamSuggestions: List<com.example.data.database.ProductSuggestion>,
    val makeupSuggestions: List<com.example.data.database.ProductSuggestion>,
    val generalTips: String
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class IngredientIssue(
    val ingredientName: String = "",
    val severity: String = "",
    val riskDescription: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class IngredientBenefit(
    val ingredientName: String = "",
    val benefitDescription: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
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

@com.squareup.moshi.JsonClass(generateAdapter = true)
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

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class MarketProductListResponse(val products: List<MarketProduct> = emptyList(), val recommendations: List<MarketProduct> = emptyList())

@com.squareup.moshi.JsonClass(generateAdapter = true)
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

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class InventoryConflict(
    val products: List<String> = emptyList(),
    val reason: String = "",
    val productA: String = "",
    val productB: String = "",
    val conflictReason: String = "",
    val solution: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class RoutineStep(
    val timeOfDay: String = "",
    val steps: List<String> = emptyList()
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class WeeklyInventoryCheckResponse(
    val conflicts: List<InventoryConflict> = emptyList(),
    val routine: String = "",
    val generalAnalysis: String = "",
    val ingredientConflicts: List<InventoryConflict> = emptyList(),
    val routineSuggestions: List<RoutineStep> = emptyList(),
    val missingItems: List<String> = emptyList(),
    val nextWeekFocus: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class PricePlatform(
    val platform: String,
    val price: String,
    val link: String = "",
    val platformName: String = "",
    val lowestPrice: String = "",
    val shippingFee: String = "",
    val deliveryDays: String = "",
    val productUrl: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class ProductPriceComparisonResponse(
    val prices: List<PricePlatform>,
    val platforms: List<PricePlatform> = emptyList(),
    val cheapestPlatform: String = "",
    val buyingAdvice: String = "",
    val lowestPrice: String = ""
)


@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiRepository {
    private val apiKey: String = BuildConfig.GEMINI_API_KEY

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
        makeup: String
    ): Pair<String, String> {
        val systemPrompt = """
            Sen profesyonel bir Dermatolog, Cilt Bakım Uzmanı (Estetisyen) ve Makyaj Artistisin.
            Kullanıcıya cilt tipine, cilt sorunlarına, bakım hedeflerine ve makyaj tercihlerine göre kişiselleştirilmiş, bilimsel ve pratik öneriler sunmalısın.
            
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
            
            Lütfen bana uygun ürün tiplerini (krem, temizleyici, güneş kremi vs.) içeren detaylı günlük rutinleri ve cilt tipime zarar vermeyecek makyaj tüyolarını hazırlar mısın?
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val fullText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Cilt analizi şu anda gerçekleştirilemedi. Lütfen daha sonra tekrar deneyin."
            
            // Let's divide it into Routine and Makeup if we can, or just parse elegantly.
            // We can return the fullText or split by a custom delimiter. Let's do simple splitting:
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
            Pair("Hata oluştu: ${e.message}. Lütfen internet bağlantınızı kontrol edip tekrar deneyin.", "Hata oluştu. Makyaj tavsiyeleri yüklenemedi.")
        }
    }

    suspend fun getChatResponse(
        userMessage: String,
        skinProfile: String,
        chatHistory: List<Content> = emptyList()
    ): String {
        val systemPrompt = """
            Sen DermaAI adında yardımsever ve profesyonel bir yapay zeka cilt bakımı ve güzellik danışmanısın.
            Kullanıcının cilt profili şudur: $skinProfile.
            Kullanıcının sorularına (krem önerileri, sivilceler, gözenekler, makyaj tüyoları, ürün içerikleri vb.) bu profile sadık kalarak dermatolojik açıdan doğru, güvenli ve pratik yanıtlar ver.
            Önerdiğin ürünlerin ilaç olmadığını, ciddi cilt hastalıkları için bir dermatoloğa görünmeleri gerektiğini nazikçe hatırlat.
            Yanıtları kısa, anlaşılır ve Türkçe olarak ver.
        """.trimIndent()

        // Combine history if any, ensuring role is assigned
        val formattedHistory = chatHistory.map { c ->
            if (c.role == null) c.copy(role = "user") else c
        }
        val combinedContents = formattedHistory.toMutableList().apply {
            add(Content(parts = listOf(Part(text = userMessage)), role = "user"))
        }

        val request = GenerateContentRequest(
            contents = combinedContents,
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)), role = "user"),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!responseText.isNullPrunedOrBlank()) {
                responseText!!
            } else {
                generateSmartLocalChatResponse(userMessage, skinProfile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            generateSmartLocalChatResponse(userMessage, skinProfile)
        }
    }

    private fun String?.isNullPrunedOrBlank(): Boolean {
        return this == null || this.isBlank() || this.contains("Bağlantı hatası")
    }

    private fun generateSmartLocalChatResponse(userMessage: String, skinProfile: String): String {
        val query = userMessage.lowercase(java.util.Locale.forLanguageTag("tr"))
        val skinType = when {
            skinProfile.contains("Kuru", ignoreCase = true) -> "Kuru"
            skinProfile.contains("Yağlı", ignoreCase = true) -> "Yağlı"
            skinProfile.contains("Hassas", ignoreCase = true) -> "Hassas"
            skinProfile.contains("Normal", ignoreCase = true) -> "Normal"
            else -> "Karma"
        }

        return when {
            query.contains("siyah nokta") || query.contains("gözenek") -> {
                """
                🌸 **Siyah Noktalar & Gözenekler İçin Uzman Önerileri:**
                
                • **Salisilik Asit (BHA):** Yağda çözünen BHA asitleri gözeneklerin içine nüfuz ederek biriken yağı ve siyah noktaları temizler. Haftada 2-3 gece BHA tonik veya serum kullanabilirsiniz.
                • **Niasinamid (B3 Vitamini):** Sebum (yağ) üretimini dengeler ve gözenek çeperlerini sıkılaştırır.
                • **Çift Aşama Temizleme:** Akşamları önce yağ bazlı temizleyici ile gözeneklerdeki yağ birikimini eritin, ardından su bazlı jelle yıkayın.
                
                ⚠️ *İpucu:* Siyah noktaları elinizle sıkmaktan kaçının, kılcal damar çatlamalarına neden olabilir!
                """.trimIndent()
            }
            query.contains("retinol") || query.contains("yaşlanma") || query.contains("kırışık") -> {
                """
                ✨ **Retinol Kullanım Rehberi & Tüyolar:**
                
                • **Sandviç Metodu:** Hassasiyeti önlemek için: Nemlendirici ➔ Retinol ➔ Nemlendirici sırasıyla uygulayın.
                • **Kademeli Başlangıç:** İlk 2 hafta haftada 1 gece, ardından haftada 2-3 geceye çıkarın.
                • **Güneş Koruması (Şart!):** Retinol cildi güneşe karşı hassaslaştırır. Ertesi sabah mutlaka SPF 50+ güneş kremi sürün.
                • **Karıştırmayın:** Retinol kullandığınız gecelerde C Vitamini veya BHA/AHA asitleri kullanmayın.
                """.trimIndent()
            }
            query.contains("leke") || query.contains("aydınlat") || query.contains("ton eşitsiz") -> {
                """
                💡 **Cilt Lekeleri & Aydınlatma Tavsiyeleri:**
                
                • **Sabah:** C Vitamini Serumu + Güneş Kremi (Güneş kremi lekelerin koyulaşmasını engellemede 1 numaralı faktördür).
                • **Akşam:** Alpha Arbutin veya Azelaik Asit serumu ile leke oluşumunu baskılayabilirsiniz.
                • **Niasinamid:** Cilt bariyerini güçlendirirken renk tonunu eşitler.
                """.trimIndent()
            }
            query.contains("sivilce") || query.contains("akne") || query.contains("kızar") -> {
                """
                🌿 **Akne & Sivilce Yatıştırma Rehberi:**
                
                • **Yatıştırıcı İçerikler:** Centella Asiatica (Madecassoside), Çay Ağacı Yağı ve Çinko PCA içeren yağsız su bazlı formüller seçin.
                • **Bariyer Onarımı:** Akne tedavisi yaparken cilt bariyerini kurutmayın. Seramidli hafif nemlendiriciler kullanın.
                • **Lokal Sivilce Kurutucu:** Oluşan aktif sivilcelerin üzerine nokta şeklinde Çinko veya Salisilik asit kremi uygulayabilirsiniz.
                """.trimIndent()
            }
            query.contains("kuru") || query.contains("pullan") || query.contains("gergin") -> {
                """
                💧 **Kuru & Pullanmış Cilt İçin Onarım:**
                
                • **Hyalüronik Asit:** Nemli cildinize Hyalüronik asit serumu sürüp hemen ardından nemlendirici kilitleyin.
                • **Seramid & Skualen:** Cilt bariyerini yeniden inşa eden yoğun lipid içerikli bariyer kremleri tercih edin.
                • **Nazik Temizleme:** Köpürmeyen, krem veya süt yapısındaki temizleyiciler kullanın.
                """.trimIndent()
            }
            query.contains("makyaj") || query.contains("baz") || query.contains("fondöten") -> {
                """
                💄 **Makyaj Altı Cilt Hazırlık Tüyoları:**
                
                • **Makyajdan 10 Dakika Önce:** Cildinizi hafif nemlendirici ve su bazlı güneş kremi ile nemlendirin.
                • **Gözenek Gizleme:** T-bölgesine silikon bazlı veya matlaştırıcı pürüzsüzleştirici baz uygulayın.
                • **Cilt Tipinize Göre ($skinType):** ${if (skinType == "Kuru") "Işıltılı, nemlendirici bitişli ten ürünleri seçin." else "Yarı-mat veya pudralı su bazlı fondötenler tercih edin."}
                """.trimIndent()
            }
            query.contains("rutin") || query.contains("sıra") || query.contains("nasıl") || query.contains("katman") -> {
                """
                📋 **Doğru Cilt Bakım Rutin Sıralaması:**
                
                1️⃣ **Temizleyici:** Su bazlı yıkama jeli/Köpük
                2️⃣ **Tonik/Esans:** Cildi neme ve seruma hazırlama
                3️⃣ **Hedef Serum:** Niasinamid, C Vitamini, Hyalüronik Asit vb.
                4️⃣ **Göz Çevresi Kremi:** Tampon hareketlerle
                5️⃣ **Nemlendirici Krem:** Nemi hapsetme
                6️⃣ **Güneş Kremi (Sabah):** SPF 50+ (2 Parmak kuralı)
                """.trimIndent()
            }
            else -> {
                """
                🌿 **DermaAI Cilt Bakım Danışmanınız:**
                
                Profilinizde kayıtlı cilt tipiniz ($skinType) uyarınca:
                • **Sabah:** Nazik temizleme, nemlendirici ve mutlaka SPF 50+ güneş koruyucu uygulayın.
                • **Akşam:** Çift aşama temizlik ve cildinizin ihtiyacına göre leke, gözenek veya bariyer onarıcı aktif serum kullanabilirsiniz.
                
                Cildinizle veya sormak istediğiniz belirli bir içerik (Retinol, Niasinamid, C Vitamini vb.) hakkında daha spesifik sorular sorabilirsiniz! 🌸
                """.trimIndent()
            }
        }
    }

    suspend fun analyzeSkinDiaryNote(note: String, rating: Int): String {
        val systemPrompt = """
            Kullanıcının günlük cilt günlüğü notuna ve cildinin o günkü iyi/kötü durum puanına (1-5 arası, 5 en iyi) göre kısa (maksimum 2-3 cümlelik) bir AI yorumu yaz.
            Örn: "Bugün kızarıklıklar için yatıştırıcı bir Centella kremi kullanmak harika bir fikir!", "Cildinin parlaması harika, nem bariyerini korumaya devam et!"
            Dost canlısı, motive edici ve rehberlik edici bir ton kullan. Türkçe yaz.
        """.trimIndent()

        val prompt = "Cilt Durum Puanı: $rating/5\nGünlük Notu: $note"

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.8f)
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Cilt günlüğün harika görünüyor! Düzenli bakıma devam et."
        } catch (e: Exception) {
            "Bugün de kendine vakit ayırdığın için harikasın! Bakım adımlarını ihmal etme."
        }
    }

    suspend fun analyzeSkinPhoto(photoPath: String, userNote: String): String {
        val file = File(photoPath)
        if (!file.exists()) return "Fotoğraf dosyası bulunamadı."
        
        val base64Data = try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return "Fotoğraf okunamadı."
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
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return "Fotoğraf dönüştürme hatası: ${e.message}"
        }

        val systemPrompt = """
            Sen profesyonel bir Dermatolog ve Yapay Zeka Cilt Analiz Uzmanısın.
            Kullanıcının gönderdiği cilt fotoğrafını ve varsa günlüğe eklediği notu analiz etmelisin.
            
            Analizinde şunları yapmalısın:
            1. Cildin genel durumunu tahmin et (örn. cilt tipi: Yağlı/Kuru/Karma/Hassas, gözenek durumu, kızarıklık, kuruluk veya parlama düzeyi).
            2. Fotoğrafta belirgin bir şikayet/sorun görünüp görünmediğini belirt (örn. akne, siyah nokta, kızarıklık, kuruluk/pullanma).
            3. Bugün için çok kısa, hedefe yönelik, güvenli ve pratik bir cilt bakım tavsiyesi ver.
            
            Yanıtı kısa, net, samimi, Türkçe ve maksimum 4-5 cümle olarak yaz. 
            Önemli Not: Teşhis koymadığını, bu analizin yapay zeka tarafından yapıldığını ve kesin çözümler için bir dermatoloğa görünmesi gerektiğini çok kısa ve nazikçe ekle.
        """.trimIndent()

        val promptText = if (userNote.isNotBlank()) {
            "Kullanıcı Notu: $userNote\nLütfen bu fotoğrafı ve notu birlikte analiz edip geri bildirim ver."
        } else {
            "Lütfen bu cilt fotoğrafını analiz edip durum ve tavsiye raporu sun."
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = promptText),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Data))
                    )
                )
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Cilt fotoğrafı analizi şu anda gerçekleştirilemedi."
        } catch (e: Exception) {
            e.printStackTrace()
            "Fotoğraf analizi hatası: ${e.message}. Lütfen internetinizi kontrol edin."
        }
    }

    suspend fun analyzeSkinForProfile(photoPath: String): ProfileAnalysisResult? {
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

        val systemPrompt = """
            Sen profesyonel bir Dermatolog ve Yapay Zeka Makyaj/Cilt Analiz Uzmanısın.
            Gönderilen cilt selfie (ön veya profil) fotoğrafını detaylı ve dürüst bir şekilde analiz etmelisin.
            Ciltteki lekeler, siyah noktalar, gözenek tıkanıklıkları, akne/sivilceler, kızarıklık, kırışıklık ve göz çevresi problemlerini eksiksiz tespit et.
            
            ÖNEMLİ DEĞERLENDİRME VE PUANLAMA KURALLARI:
            - "skinHealthScore": Ciltteki leke, siyah nokta, akne, kızarıklık ve pürüz durumuna göre 0-100 arası DÜRÜST VE GERÇEKÇİ bir cilt sağlığı puanı hesapla.
            - Eğer ciltte belirgin lekeler, siyah noktalar veya akneler varsa KESİNLİKLE YÜKSEK PUAN (80+) VERME! Gerçekçi olarak 50-68 arasında tut.
            - Cilt pürüzsüz ve sorunsuzsa 80-92 arası ver. Sorun sayısı arttıkça puan düşmelidir.
            - Tespit ettiğin sorunların yüzde tam nerede olduğunu (x, y) koordinatlarıyla (0.0 ile 1.0 arasında) ver.
            
            Yanıtını SADECE aşağıdaki JSON formatında vermelisin. Başka metin veya markdown ekleme (```json gibi şeyler koyma).
            
            {
                "skinType": "Kuru | Yağlı | Karma | Hassas | Normal",
                "concerns": ["Akne & Sivilce", "Lekeler & Pigmentasyon", "Siyah Noktalar"],
                "goal": "Nemlendirme | Sivilce Kontrolü | Aydınlatma & Parlaklık",
                "explanation": "Cilt durumunun detaylı, dürüst ve objektif analizi...",
                "eyeAreaAnalysis": "Göz bölgesinin detaylı analizi",
                "makeupEvaluation": "Makyaj tespit edilmedi / Varsa değerlendirmesi",
                "skinHealthScore": 62,
                "confidenceScore": 95,
                "faceMapRegions": [
                    {
                        "regionName": "Burun & T-Bölgesi",
                        "issue": "Siyah noktalar & Gözenek genişlemesi",
                        "recommendedIngredient": "Salisilik Asit (BHA %2)",
                        "x": 0.5,
                        "y": 0.45
                    }
                ]
            }
        """.trimIndent()

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
            generationConfig = GenerationConfig(temperature = 0.3f, responseMimeType = "application/json")
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

        val systemPrompt = """
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
        """.trimIndent()

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

    


    suspend fun fetchCustomRecommendations(
        skinType: String,
        concerns: String,
        goal: String,
        makeup: String,
        allergies: String
    ): GeminiRecommendationResponse? {
        val systemPrompt = """
            Sen profesyonel bir Dermatolog, Kozmetolog ve Makyaj Artistisin.
            Kullanıcının cilt tipine, şikayetlerine, cilt hedeflerine, makyaj tercihlerine ve bilinen alerjilerine göre kişiselleştirilmiş kozmetik krem ve makyaj ürün önerileri sunmalısın.
            
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir giriş veya çıkış açıklaması bulunmamalıdır. SADECE saf geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "skinType": "Cilt Tipi (Kuru, Yağlı, Karma, Hassas, Normal)",
              "creamSuggestions": [
                {
                  "name": "Ürün Adı (örn: Cica Onarıcı Yatıştırıcı Krem)",
                  "category": "Ürün Kategorisi (örn: Nemlendirici, Serum, Temizleyici, Güneş Kremi)",
                  "activeIngredients": "Aktif Maddeler (örn: Centella Asiatica, Hyaluronic Acid)",
                  "description": "Neden önerildiğine dair detaylı açıklama",
                  "usageTip": "Nasıl kullanılacağına dair pratik ipucu"
                }
              ],
              "makeupSuggestions": [
                {
                  "name": "Ürün Adı (örn: Su Bazlı Gözenek Gizleyici Astar)",
                  "category": "Makyaj Kategorisi (örn: Fondöten, Kapatıcı, Astar (Primer), Sabitleyici Pudra)",
                  "activeIngredients": "Aktif Maddeler (örn: Silika, Çinko PCA)",
                  "description": "Neden önerildiğine dair açıklama",
                  "usageTip": "Nasıl kullanılacağına dair pratik ipucu"
                }
              ],
              "generalTips": "Cilt tipine uygun genel tavsiyeler ve bakım tüyoları (Maksimum 2-3 cümle)"
            }
            NOT: Lütfen hem 'creamSuggestions' hem de 'makeupSuggestions' listelerinde en az 3'er adet son derece detaylı ve profesyonel öneri sun. Önerdiğin ürün isimleri genel marka-bağımsız formülasyon isimleri olsun. Açıklamalar ve ipuçları TÜRKÇE, samimi ve son derece bilgilendirici olsun. KESİNLİKLE kullanıcının alerjisi olduğu bilinen içerikleri önerme!
        """.trimIndent()

        val userPrompt = """
            Cilt Bilgilerim:
            - Cilt Tipi: $skinType
            - Cilt Sorunları/Şikayetleri: $concerns
            - Cilt Bakım Hedefi: $goal
            - Makyaj Tercihi: $makeup
            - Alerjiler / Hassasiyetler: $allergies
            
            Lütfen cilt tipime ve profilime uygun, alerjilerimi (varsa) dikkate alan en iyi krem, serum, temizleyici (creamSuggestions) ve fondöten, kapatıcı, astar (makeupSuggestions) ürün tavsiyelerini JSON formatında üret.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(
                temperature = 0.5f,
                responseMimeType = "application/json"
            )
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)

            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            
            val moshi = com.squareup.moshi.Moshi.Builder()
                
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
        val systemPrompt = """
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
              "usageTips": "Bu içerik kombinasyonuna göre özel bir kullanım önerisi (Örn: Sabahları güneş kremi ile birlikte kullanın veya nemli cilde uygulayın.)",
              "confidenceScore": 85 // Metni okuma ve analiz güvenilirliğini gösteren 0-100 arası sayı
            }
            
            Önemli kurallar:
            1. GERÇEKÇİ OLUN: Her ürüne %90 ve üzeri puan VERMEYİN. Ortalama veya sorunlu ürünler için 40-70 arası, eğer kullanıcının belirttiği alerjilerden ("$allergies") biri varsa puanı %30'un altına düşür ve "Uyumsuz" etiketle.
            2. YUVARLAK ŞİŞELER: Görüntü yuvarlak veya kavisli bir şişeden çekilmiş olabilir, metinler eğik, bozuk veya eksik harfli olabilir. OCR hatalarını düzelt, eksik harfleri tahmin ederek doğru INCI isimlerini mantıksal olarak çıkar.
            3. Eğer resimdeki veya metindeki içerik listesi tamamen okunaksızsa veya kozmetik ürün içeriği değilse bile, kullanıcıya boş bir şablon yerine örnek bir analiz döndürme; productName alanını "Okunamadı" yap ve compatibilityExplanation alanında durumu açıkla.
            4. Tüm açıklamalar ve madde adları TÜRKÇE veya uluslararası kozmetik (INCI) isimleri ile verilmelidir.
            5. Alerjen ve tahriş edici maddeleri tespit ederken kullanıcının Hassas, Kuru veya Yağlı cilt tipini ve özellikle Alerjilerini göz önünde bulundur.
        """.trimIndent()

        val userPrompt = if (!rawIngredientsText.isNullOrBlank()) {
            "İşte analiz etmeniz için ürünün içerik listesi:\n" + rawIngredientsText
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
        val systemPrompt = """
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
        """.trimIndent()

        val userPrompt = "Cilt: $skinType, Sorunlar: $concerns, Hedef: $goal"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().build()
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
        val systemPrompt = """
            Sen kullanıcıların satın almak istediği kozmetik ürünleri onların cilt tipine göre analiz edip 'AL', 'ALMA' veya 'DİKKATLİ KULLAN' şeklinde net dermatolojik ve ekonomik tavsiye veren bir Güzellik ve Alışveriş Asistanısın.
            Geri dönüşün tam olarak belirtilen JSON şemasına uymalıdır. Yanıtında başka hiçbir açıklama bulunmamalıdır. SADECE geçerli bir JSON döndürmelisin.
            
            JSON Şeması:
            {
              "advice": "AL / ALMA / DİKKATLİ KULLAN",
              "reason": "Açıklama",
              "alternatives": ["Alternatif 1", "Alternatif 2"]
            }
        """.trimIndent()
        
        val userPrompt = "Ürün: $brand $productName. Cilt: $skinType, Sorunlar: $concerns, Hedef: $goal"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(PurchaseAdviceResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }

    suspend fun checkWeeklyInventory(
        inventoryJson: String,
        skinType: String,
        concerns: String,
        goal: String
    ): WeeklyInventoryCheckResponse? {
        val systemPrompt = """
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
        """.trimIndent()
        
        val userPrompt = "Cilt: $skinType, Envanter: $inventoryJson"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(WeeklyInventoryCheckResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }

    suspend fun fetchProductPrices(
        productName: String,
        category: String
    ): ProductPriceComparisonResponse? {
        val systemPrompt = """
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
        """.trimIndent()
        
        val userPrompt = "Ürün: $productName"
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.5f, responseMimeType = "application/json")
        )
        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return null
            val moshi = com.squareup.moshi.Moshi.Builder().build()
            moshi.adapter(ProductPriceComparisonResponse::class.java).fromJson(cleanJson(jsonText))
        } catch (e: Exception) { null }
    }
}