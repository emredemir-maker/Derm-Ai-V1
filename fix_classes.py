classes_str = """
data class ProfileAnalysisResult(val skinType: String, val concerns: List<String>, val goal: String, val explanation: String)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class GeminiRecommendationResponse(
    val skinType: String,
    val creamSuggestions: List<com.example.data.database.ProductSuggestion>,
    val makeupSuggestions: List<com.example.data.database.ProductSuggestion>,
    val generalTips: String
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class IngredientIssue(val ingredientName: String, val severity: String, val riskDescription: String)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class IngredientBenefit(val ingredientName: String, val benefitDescription: String)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class IngredientAnalysisResponse(
    val productName: String,
    val compatibilityScore: Int,
    val compatibilityLabel: String,
    val compatibilityExplanation: String,
    val detectedIngredients: List<String>,
    val allergensAndIrritants: List<IngredientIssue>,
    val beneficialIngredients: List<IngredientBenefit>,
    val finalVerdict: String,
    val usageTips: String
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class MarketProduct(
    val name: String,
    val brand: String,
    val category: String,
    val priceSegment: String,
    val matchScore: Int,
    val reason: String,
    val estimatedPrice: String = "",
    val keyActiveIngredients: String = "",
    val prosDescription: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class MarketProductListResponse(val products: List<MarketProduct>)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class PurchaseAdviceResponse(
    val advice: String,
    val reason: String,
    val alternatives: List<String>,
    val reasoning: String = "",
    val positiveIngredients: String = "",
    val riskyIngredients: String = "",
    val alternativeSuggestions: String = "",
    val suitabilityScore: Int = 0,
    val verdict: String = ""
)

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class InventoryConflict(
    val products: List<String>,
    val reason: String,
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
    val buyingAdvice: String = ""
)
"""

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

# Insert classes after imports
lines = content.splitlines()
import_end_idx = 0
for i, line in enumerate(lines):
    if line.startswith("import "):
        import_end_idx = i

lines.insert(import_end_idx + 1, classes_str)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write("\n".join(lines))
