with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

old_classes = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
)"""

new_classes = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
    val usageTips: String = ""
)"""

content = content.replace(old_classes, new_classes)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
