import re

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

target = """    suspend fun analyzeMakeup(photoPath: String): MakeupAnalysisResult? {
        return GeminiApi.analyzeMakeup(photoPath)
    }"""

content = content.replace(target, "")

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
