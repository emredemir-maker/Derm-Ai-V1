with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

old_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
)"""

new_class = """@com.squareup.moshi.JsonClass(generateAdapter = true)
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
)"""

content = content.replace(old_class, new_class)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
