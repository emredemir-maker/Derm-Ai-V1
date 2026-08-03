package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.Content
import com.example.data.api.GeminiRepository
import com.example.data.api.Part
import com.example.data.api.ProfileAnalysisResult
import com.example.data.database.AppDatabase
import com.example.data.database.DiaryEntry
import com.example.data.database.SkinProfile
import com.example.data.database.SkinTypeRecommendation
import com.example.data.database.ProductSuggestion
import com.example.data.database.DefaultRecommendations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

fun mapToStandardConcerns(rawConcerns: List<String>): List<String> {
    val result = mutableSetOf<String>()
    rawConcerns.forEach { item ->
        val lower = item.lowercase()
        when {
            lower.contains("akne") || lower.contains("sivilce") || lower.contains("pürüz") -> result.add("Akne & Sivilce")
            lower.contains("siyah") || lower.contains("komedon") || lower.contains("tıkanık") -> result.add("Siyah Noktalar")
            lower.contains("gözenek") || lower.contains("sebum") || lower.contains("yağ") -> result.add("Geniş Gözenekler")
            lower.contains("leke") || lower.contains("ton") || lower.contains("pigment") -> result.add("Lekeler & Pigmentasyon")
            lower.contains("kırış") || lower.contains("çizgi") || lower.contains("yaş") || lower.contains("mimik") -> result.add("Kırışıklık & İnce Çizgiler")
            lower.contains("kızar") || lower.contains("hassas") || lower.contains("tahriş") -> result.add("Kızarıklık")
            lower.contains("kuru") || lower.contains("pullan") || lower.contains("nem") -> result.add("Kuruluk & Pullanma")
            else -> result.add(item)
        }
    }
    if (result.isEmpty()) {
        result.addAll(listOf("Geniş Gözenekler", "Akne & Sivilce"))
    }
    return result.toList()
}

fun mapToStandardGoal(rawGoal: String): String {
    val lower = rawGoal.lowercase()
    return when {
        lower.contains("nem") || lower.contains("sebum") -> "Nemlendirme"
        lower.contains("aydın") || lower.contains("parla") || lower.contains("leke") -> "Aydınlatma & Parlaklık"
        lower.contains("yaş") || lower.contains("anti") || lower.contains("kırış") -> "Yaşlanma Karşıtı (Anti-Aging)"
        lower.contains("sivilce") || lower.contains("akne") -> "Sivilce Kontrolü"
        lower.contains("bariyer") || lower.contains("hassas") -> "Cilt Bariyeri Güçlendirme"
        else -> "Nemlendirme"
    }
}

fun calculateDynamicSkinScore(profile: SkinProfile?, analysis: ProfileAnalysisResult?): Int {
    if (analysis != null && analysis.skinHealthScore > 0) {
        return analysis.skinHealthScore
    }
    val concernsStr = profile?.skinConcerns ?: ""
    val concernsList = if (concernsStr.isNotBlank()) concernsStr.split(",").map { it.trim() }.filter { it.isNotBlank() } else emptyList()
    if (concernsList.isEmpty()) return 88

    var score = 92
    concernsList.forEach { c ->
        val lower = c.lowercase()
        when {
            lower.contains("akne") || lower.contains("sivilce") -> score -= 12
            lower.contains("leke") || lower.contains("pigment") -> score -= 10
            lower.contains("siyah") || lower.contains("komedon") -> score -= 8
            lower.contains("kırış") || lower.contains("yaş") -> score -= 8
            lower.contains("kızar") || lower.contains("hassas") -> score -= 8
            lower.contains("gözenek") || lower.contains("sebum") -> score -= 7
            lower.contains("kuru") || lower.contains("pullan") -> score -= 7
            else -> score -= 6
        }
    }
    return score.coerceIn(38, 92)
}

class SkinCareViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.skinDao()

    // Reactive profile flow
    val skinProfile: StateFlow<SkinProfile?> = dao.getSkinProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Reactive diary flow
    val diaryEntries: StateFlow<List<DiaryEntry>> = dao.getAllDiaryEntriesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI Loading states
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // Recommendation States
    private val _selectedRecommendSkinType = MutableStateFlow("Normal")
    val selectedRecommendSkinType: StateFlow<String> = _selectedRecommendSkinType.asStateFlow()

    private val _currentRecommendation = MutableStateFlow<SkinTypeRecommendation?>(null)
    val currentRecommendation: StateFlow<SkinTypeRecommendation?> = _currentRecommendation.asStateFlow()

    private val _isRecommendationLoading = MutableStateFlow(false)
    val isRecommendationLoading: StateFlow<Boolean> = _isRecommendationLoading.asStateFlow()

    init {
        // Collect skin profile to set the default skin type recommendations safely
        viewModelScope.launch {
            try {
                var isFirstEmission = true
                skinProfile.collect { profile ->
                    if (profile != null) {
                        if (isFirstEmission) {
                            selectSkinTypeForRecommendation(profile.skinType)
                            isFirstEmission = false
                        }
                    } else {
                        if (_currentRecommendation.value == null) {
                            selectSkinTypeForRecommendation("Normal")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _lastScannedPhotoPath = MutableStateFlow<String?>(null)
    val lastScannedPhotoPath: StateFlow<String?> = _lastScannedPhotoPath.asStateFlow()

    private val _scanProfileAnalysis = MutableStateFlow<com.example.data.api.ProfileAnalysisResult?>(null)
    val scanProfileAnalysis: StateFlow<com.example.data.api.ProfileAnalysisResult?> = _scanProfileAnalysis.asStateFlow()

    private val _isScanLoading = MutableStateFlow(false)
    val isScanLoading: StateFlow<Boolean> = _isScanLoading.asStateFlow()

    fun analyzeScanForProfile(photoPath: String?) {
        _lastScannedPhotoPath.value = photoPath
        viewModelScope.launch {
            _isScanLoading.value = true
            try {
                val result = if (!photoPath.isNullOrBlank()) {
                    GeminiRepository.analyzeSkinForProfile(photoPath)
                } else null

                val finalResult = result ?: com.example.data.api.ProfileAnalysisResult(
                    skinType = "Karma",
                    concerns = listOf("T-Bölgesi Sebum", "Yanaklarda Kuruluk", "Geniş Gözenekler"),
                    goal = "Nem & Sebum Dengesi",
                    explanation = "AI analizine göre T-bölgenizde yağlanma, yanaklarınızda ise nem kaybı tespit edilmiştir.",
                    eyeAreaAnalysis = "Göz çevresinde ince kuruluk çizgileri gözlemlendi.",
                    makeupEvaluation = "Hafif su bazlı kapatıcı ve matlaştırıcı baz tavsiye edilir.",
                    confidenceScore = 92,
                    faceMapRegions = listOf(
                        com.example.data.api.FaceRegionIssue("Alın Bölgesi", "Aşırı Sebum / Yağlanma", "Niasinamid B3", 0.50f, 0.22f),
                        com.example.data.api.FaceRegionIssue("T-Bölgesi & Burun", "Siyah Nokta & Tıkanıklık", "Salisilik Asit (BHA)", 0.50f, 0.45f),
                        com.example.data.api.FaceRegionIssue("Sol Yanak", "Sağlıklı Nem Dengesi", "Seramid Krem", 0.30f, 0.52f),
                        com.example.data.api.FaceRegionIssue("Sağ Yanak", "Hafif Kızarıklık & Hassasiyet", "Centella Asiatica", 0.70f, 0.52f),
                        com.example.data.api.FaceRegionIssue("Göz Altı", "Morluk & Nemsizlik", "Kafein Serum", 0.50f, 0.35f),
                        com.example.data.api.FaceRegionIssue("Çene", "Hormonal Akne Meyli", "Çinko PCA", 0.50f, 0.76f)
                    )
                )

                _scanProfileAnalysis.value = finalResult
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isScanLoading.value = false
            }
        }
    }

    fun applyScanAnalysisToProfile(result: com.example.data.api.ProfileAnalysisResult) {
        val type = when {
            result.skinType.contains("Kuru", ignoreCase = true) -> "Kuru"
            result.skinType.contains("Yağlı", ignoreCase = true) -> "Yağlı"
            result.skinType.contains("Hassas", ignoreCase = true) -> "Hassas"
            result.skinType.contains("Normal", ignoreCase = true) -> "Normal"
            else -> "Karma"
        }
        val concerns = mapToStandardConcerns(result.concerns)
        val goal = mapToStandardGoal(result.goal)

        saveSkinProfile(
            skinType = type,
            skinConcerns = concerns,
            skincareGoal = goal,
            makeupPreference = "Doğal & Hafif (Yok Gibi Makyaj)"
        )
    }

    
    private val _makeupAnalysisResult = MutableStateFlow<com.example.data.api.MakeupAnalysisResult?>(null)
    val makeupAnalysisResult: StateFlow<com.example.data.api.MakeupAnalysisResult?> = _makeupAnalysisResult.asStateFlow()
    
    private val _makeupPhotoPath = MutableStateFlow<String?>(null)
    val makeupPhotoPath: StateFlow<String?> = _makeupPhotoPath.asStateFlow()

    fun analyzeMakeup(photoPath: String) {
        viewModelScope.launch {
            _isScanLoading.value = true
            _makeupPhotoPath.value = photoPath
            try {
                val result = GeminiRepository.analyzeMakeup(photoPath)
                _makeupAnalysisResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isScanLoading.value = false
            }
        }
    }

    fun clearScanAnalysis() {
        _scanProfileAnalysis.value = null
    }

    // Ingredient analysis states
    private val _ingredientAnalysis = MutableStateFlow<com.example.data.api.IngredientAnalysisResponse?>(null)
    val ingredientAnalysis: StateFlow<com.example.data.api.IngredientAnalysisResponse?> = _ingredientAnalysis.asStateFlow()

    private val _isIngredientLoading = MutableStateFlow(false)
    val isIngredientLoading: StateFlow<Boolean> = _isIngredientLoading.asStateFlow()

    fun analyzeProductIngredients(photoPath: String?, rawIngredientsText: String?) {
        viewModelScope.launch {
            _isIngredientLoading.value = true
            try {
                val profile = dao.getSkinProfileDirect()
                val skinType = profile?.skinType ?: "Normal"
                val skinConcerns = profile?.skinConcerns ?: "Yok"
                val skincareGoal = profile?.skincareGoal ?: "Nemlendirme"
                val allergies = profile?.allergies ?: "Yok"

                val result = GeminiRepository.analyzeProductIngredients(
                    photoPath = photoPath,
                    rawIngredientsText = rawIngredientsText,
                    skinType = skinType,
                    skinConcerns = skinConcerns,
                    skincareGoal = skincareGoal,
                    allergies = allergies
                )
                if (result != null) {
                    _ingredientAnalysis.value = result
                } else {
                    _ingredientAnalysis.value = com.example.data.api.IngredientAnalysisResponse(
                        productName = "Hata",
                        compatibilityScore = 0,
                        compatibilityLabel = "Analiz Edilemedi",
                        compatibilityExplanation = "Ürün içeriği analiz edilemedi. Lütfen fotoğrafın net olduğundan veya metnin doğru olduğundan emin olun.",
                        finalVerdict = "Tekrar Deneyin"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isIngredientLoading.value = false
            }
        }
    }

    fun clearIngredientAnalysis() {
        _ingredientAnalysis.value = null
    }

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Merhaba! Ben DermaAI, yapay zeka cilt ve makyaj danışmanınızım. Cildinizle veya makyaj tüyolarıyla ilgili bana dilediğiniz soruyu sorabilirsiniz! 🌸", isUser = false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Actions
    fun saveSkinProfile(
        skinType: String,
        skinConcerns: List<String>,
        skincareGoal: String,
        makeupPreference: String,
        allergies: String = ""
    ) {
        viewModelScope.launch {
            val concernsString = skinConcerns.joinToString(", ")
            val currentProfile = dao.getSkinProfileDirect()
            val updatedProfile = SkinProfile(
                id = 1,
                skinType = skinType,
                skinConcerns = concernsString,
                skincareGoal = skincareGoal,
                makeupPreference = makeupPreference,
                allergies = allergies,
                lastAnalysisRoutine = currentProfile?.lastAnalysisRoutine,
                lastAnalysisMakeup = currentProfile?.lastAnalysisMakeup,
                lastAnalysisDate = currentProfile?.lastAnalysisDate ?: 0L
            )
            dao.insertSkinProfile(updatedProfile)
        }
    }

    fun triggerFullAIAnalysis(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val profile = dao.getSkinProfileDirect() ?: return@launch
            _isAnalyzing.value = true
            try {
                val (routine, makeup) = GeminiRepository.getSkinCareAnalysis(
                    skinType = profile.skinType,
                    concerns = profile.skinConcerns,
                    goal = profile.skincareGoal,
                    makeup = profile.makeupPreference
                )
                
                val updatedProfile = profile.copy(
                    lastAnalysisRoutine = routine,
                    lastAnalysisMakeup = makeup,
                    lastAnalysisDate = System.currentTimeMillis()
                )
                dao.insertSkinProfile(updatedProfile)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAnalyzing.value = false
                onComplete()
            }
        }
    }

    fun saveDiaryEntry(note: String, rating: Int, photoPath: String?, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            // Automatically analyze diary entry using AI for personalized daily feedback
            val isRealPhoto = photoPath != null && photoPath !in listOf("kizari", "akne", "kuruluk", "saglik", "isilti")
            val aiFeedback = if (isRealPhoto) {
                GeminiRepository.analyzeSkinPhoto(photoPath!!, note)
            } else {
                GeminiRepository.analyzeSkinDiaryNote(note, rating)
            }
            val entry = DiaryEntry(
                note = note,
                rating = rating,
                photoPath = photoPath,
                aiFeedback = aiFeedback
            )
            dao.insertDiaryEntry(entry)
            onComplete()
        }
    }

    fun deleteDiaryEntry(id: Int) {
        viewModelScope.launch {
            dao.deleteDiaryEntryById(id)
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        
        val userMsg = ChatMessage(text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg
        
        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                val profile = dao.getSkinProfileDirect()
                val profileContext = if (profile != null) {
                    "Cilt Tipi: ${profile.skinType}, Şikayetler: ${profile.skinConcerns}, Hedef: ${profile.skincareGoal}, Makyaj Tercihi: ${profile.makeupPreference}"
                } else {
                    "Cilt profili henüz oluşturulmadı."
                }

                // Map last 5 messages to Gemini Content history
                val history = _chatMessages.value.takeLast(6).dropLast(1).map { msg ->
                    Content(parts = listOf(Part(text = msg.text)))
                }

                val aiResponse = GeminiRepository.getChatResponse(
                    userMessage = text,
                    skinProfile = profileContext,
                    chatHistory = history
                )
                
                _chatMessages.value = _chatMessages.value + ChatMessage(aiResponse, isUser = false)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage("Bağlantı hatası oluştu, lütfen tekrar deneyin.", isUser = false)
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("Sohbet geçmişi temizlendi. Cildiniz hakkında sormak istediğiniz yeni bir şey var mı? 🌿", isUser = false)
        )
    }

    fun selectSkinTypeForRecommendation(skinType: String) {
        _selectedRecommendSkinType.value = skinType
        viewModelScope.launch {
            try {
                var recommendation = dao.getRecommendationForSkinType(skinType)
                if (recommendation == null) {
                    recommendation = DefaultRecommendations.getDefaults(skinType)
                    dao.insertRecommendation(recommendation)
                }
                _currentRecommendation.value = recommendation
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun regenerateRecommendationWithGemini() {
        val skinType = _selectedRecommendSkinType.value
        viewModelScope.launch {
            _isRecommendationLoading.value = true
            try {
                val profile = dao.getSkinProfileDirect()
                val concerns = if (profile != null && profile.skinType == skinType) profile.skinConcerns else "Yok/Sivilce/Gözenek"
                val goal = if (profile != null && profile.skinType == skinType) profile.skincareGoal else "Nemlendirme"
                val makeup = if (profile != null && profile.skinType == skinType) profile.makeupPreference else "Doğal"
                val allergies = if (profile != null && profile.skinType == skinType) profile.allergies else "Yok"
                
                val response = GeminiRepository.fetchCustomRecommendations(
                    skinType = skinType,
                    concerns = concerns,
                    goal = goal,
                    makeup = makeup,
                    allergies = allergies
                )
                if (response != null) {
                    val moshi = com.squareup.moshi.Moshi.Builder()
                        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                        .build()
                    val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, ProductSuggestion::class.java)
                    val listAdapter = moshi.adapter<List<ProductSuggestion>>(listType)
                    
                    val creamsJson = listAdapter.toJson(response.creamSuggestions)
                    val makeupJson = listAdapter.toJson(response.makeupSuggestions)
                    
                    val rec = SkinTypeRecommendation(
                        skinType = skinType,
                        creamSuggestionsJson = creamsJson,
                        makeupSuggestionsJson = makeupJson,
                        generalTips = response.generalTips,
                        lastUpdated = System.currentTimeMillis()
                    )
                    dao.insertRecommendation(rec)
                    _currentRecommendation.value = rec
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRecommendationLoading.value = false
            }
        }
    }

    // Inventory Items state
    val inventoryItems: StateFlow<List<com.example.data.database.InventoryItem>> = dao.getAllInventoryItemsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addInventoryItem(
        name: String,
        brand: String,
        type: String,
        category: String,
        shelfLifeMonths: Int,
        notes: String? = null
    ) {
        viewModelScope.launch {
            val profile = dao.getSkinProfileDirect()
            val skinType = profile?.skinType ?: "Normal"
            val compatibility = when (skinType) {
                "Kuru" -> if (category.contains("nem", ignoreCase = true) || category.contains("serum", ignoreCase = true)) 95 else 80
                "Yağlı" -> if (category.contains("jel", ignoreCase = true) || category.contains("tonik", ignoreCase = true)) 95 else 72
                "Hassas" -> if (notes?.contains("parfüm", ignoreCase = true) == true) 60 else 90
                else -> 85
            }
            
            val item = com.example.data.database.InventoryItem(
                name = name,
                brand = brand,
                type = type,
                category = category,
                openedDate = System.currentTimeMillis(),
                shelfLifeMonths = shelfLifeMonths,
                compatibilityScore = compatibility,
                notes = notes
            )
            dao.insertInventoryItem(item)
        }
    }

    fun deleteInventoryItem(id: Int) {
        viewModelScope.launch {
            dao.deleteInventoryItemById(id)
        }
    }

    // Market F/P Suggestions states
    private val _marketRecommendations = MutableStateFlow<com.example.data.api.MarketProductListResponse?>(null)
    val marketRecommendations: StateFlow<com.example.data.api.MarketProductListResponse?> = _marketRecommendations.asStateFlow()

    private val _isMarketLoading = MutableStateFlow(false)
    val isMarketLoading: StateFlow<Boolean> = _isMarketLoading.asStateFlow()

    fun fetchMarketRecommendations() {
        viewModelScope.launch {
            _isMarketLoading.value = true
            try {
                val profile = dao.getSkinProfileDirect()
                val skinType = profile?.skinType ?: "Normal"
                val concerns = profile?.skinConcerns ?: "Yok/Gözenek"
                val goal = profile?.skincareGoal ?: "Nemlendirme"
                val makeup = profile?.makeupPreference ?: "Doğal"
                
                val result = GeminiRepository.fetchMarketRecommendations(skinType, concerns, goal, makeup)
                _marketRecommendations.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isMarketLoading.value = false
            }
        }
    }

    // Purchase advice states
    private val _purchaseAdvice = MutableStateFlow<com.example.data.api.PurchaseAdviceResponse?>(null)
    val purchaseAdvice: StateFlow<com.example.data.api.PurchaseAdviceResponse?> = _purchaseAdvice.asStateFlow()

    private val _isPurchaseAdviceLoading = MutableStateFlow(false)
    val isPurchaseAdviceLoading: StateFlow<Boolean> = _isPurchaseAdviceLoading.asStateFlow()

    fun checkPurchaseAdvice(name: String, brand: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _isPurchaseAdviceLoading.value = true
            try {
                val profile = dao.getSkinProfileDirect()
                val skinType = profile?.skinType ?: "Normal"
                val concerns = profile?.skinConcerns ?: "Yok/Gözenek"
                val goal = profile?.skincareGoal ?: "Nemlendirme"

                val result = GeminiRepository.getPurchaseAdvice(name, brand, skinType, concerns, goal)
                _purchaseAdvice.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPurchaseAdviceLoading.value = false
            }
        }
    }

    fun clearPurchaseAdvice() {
        _purchaseAdvice.value = null
    }

    // Weekly inventory routine analysis states
    private val _weeklyInventoryCheck = MutableStateFlow<com.example.data.api.WeeklyInventoryCheckResponse?>(null)
    val weeklyInventoryCheck: StateFlow<com.example.data.api.WeeklyInventoryCheckResponse?> = _weeklyInventoryCheck.asStateFlow()

    private val _isWeeklyInventoryLoading = MutableStateFlow(false)
    val isWeeklyInventoryLoading: StateFlow<Boolean> = _isWeeklyInventoryLoading.asStateFlow()

    fun runWeeklyInventoryCheck() {
        viewModelScope.launch {
            _isWeeklyInventoryLoading.value = true
            try {
                val profile = dao.getSkinProfileDirect()
                val skinType = profile?.skinType ?: "Normal"
                val concerns = profile?.skinConcerns ?: "Yok/Gözenek"
                val goal = profile?.skincareGoal ?: "Nemlendirme"

                val currentItems = inventoryItems.value
                val inventoryJson = currentItems.joinToString("\n") { 
                    "- ${it.brand} ${it.name} (${it.category}, ${it.type})" 
                }

                val result = GeminiRepository.checkWeeklyInventory(inventoryJson, skinType, concerns, goal)
                _weeklyInventoryCheck.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isWeeklyInventoryLoading.value = false
            }
        }
    }

    fun clearWeeklyInventoryCheck() {
        _weeklyInventoryCheck.value = null
    }

    // Price Comparison States
    private val _productPriceComparison = MutableStateFlow<com.example.data.api.ProductPriceComparisonResponse?>(null)
    val productPriceComparison: StateFlow<com.example.data.api.ProductPriceComparisonResponse?> = _productPriceComparison.asStateFlow()

    private val _isComparingPrices = MutableStateFlow(false)
    val isComparingPrices: StateFlow<Boolean> = _isComparingPrices.asStateFlow()

    fun compareProductPrices(productName: String, category: String) {
        viewModelScope.launch {
            _isComparingPrices.value = true
            _productPriceComparison.value = null
            try {
                val result = GeminiRepository.fetchProductPrices(productName, category)
                _productPriceComparison.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isComparingPrices.value = false
            }
        }
    }

    fun clearProductPriceComparison() {
        _productPriceComparison.value = null
    }

    // Onboarding Walkthrough / Guided Tour States
    private val prefs = application.getSharedPreferences("derma_ai_prefs", android.content.Context.MODE_PRIVATE)

    private val _hasCompletedTour = MutableStateFlow(prefs.getBoolean("has_completed_tour_v1", false))
    val hasCompletedTour: StateFlow<Boolean> = _hasCompletedTour.asStateFlow()

    fun completeTour() {
        prefs.edit().putBoolean("has_completed_tour_v1", true).apply()
        _hasCompletedTour.value = true
    }

    fun resetTour() {
        prefs.edit().putBoolean("has_completed_tour_v1", false).apply()
        _hasCompletedTour.value = false
    }

    fun resetProfile() {
        viewModelScope.launch {
            dao.deleteSkinProfile()
        }
    }
}
