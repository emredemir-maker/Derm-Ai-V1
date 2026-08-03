with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

old_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
data class MarketProductListResponse(val products: List<MarketProduct>)"""

new_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
data class MarketProductListResponse(val products: List<MarketProduct> = emptyList(), val recommendations: List<MarketProduct> = emptyList())"""

content = content.replace(old_class, new_class)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
