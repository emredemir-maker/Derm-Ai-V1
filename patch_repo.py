import re

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

repo_func = """
    suspend fun analyzeMakeup(photoPath: String): MakeupAnalysisResult? {
        return GeminiApi.analyzeMakeup(photoPath)
    }
"""

content = content.replace("suspend fun fetchCustomRecommendations(", repo_func + "\n    suspend fun fetchCustomRecommendations(")

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
