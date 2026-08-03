with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

old_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
)"""

new_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
)"""

content = content.replace(old_class, new_class)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
