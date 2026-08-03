with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "val userPrompt = if (!rawIngredientsText.isNullOrBlank())" in line:
        lines[i+1] = '            "İşte analiz etmeniz için ürünün içerik listesi:\\n" + rawIngredientsText\n'
        lines[i+2] = "" # Delete the blank line

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.writelines(lines)
