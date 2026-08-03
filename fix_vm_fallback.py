with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "val result = GeminiRepository.analyzeProductIngredients(" in line:
        # We need to find where _ingredientAnalysis.value = result is
        break

for i in range(len(lines)):
    if "_ingredientAnalysis.value = result" in lines[i]:
        lines[i] = """                if (result != null) {
                    _ingredientAnalysis.value = result
                } else {
                    _ingredientAnalysis.value = com.example.data.api.IngredientAnalysisResponse(
                        productName = "Hata",
                        compatibilityScore = 0,
                        compatibilityLabel = "Analiz Edilemedi",
                        compatibilityExplanation = "Ürün içeriği analiz edilemedi. Lütfen fotoğrafın net olduğundan veya metnin doğru olduğundan emin olun.",
                        finalVerdict = "Tekrar Deneyin"
                    )
                }
"""

with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "w") as f:
    f.writelines(lines)
