import re

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

target = "data class ProfileAnalysisResult(val skinType: String, val concerns: List<String>, val goal: String, val explanation: String, val confidenceScore: Int = 0)"

replacement = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
    val confidenceScore: Int = 0,
    val faceMapRegions: List<FaceRegionIssue> = emptyList()
)"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
