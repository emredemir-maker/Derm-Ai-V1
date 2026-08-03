with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

old_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
data class ProductPriceComparisonResponse(
    val prices: List<PricePlatform>,
    val platforms: List<PricePlatform> = emptyList(),
    val cheapestPlatform: String = "",
    val buyingAdvice: String = ""
)"""

new_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
data class ProductPriceComparisonResponse(
    val prices: List<PricePlatform>,
    val platforms: List<PricePlatform> = emptyList(),
    val cheapestPlatform: String = "",
    val buyingAdvice: String = "",
    val lowestPrice: String = ""
)"""

content = content.replace(old_class, new_class)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
