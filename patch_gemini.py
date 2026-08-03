with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "r") as f:
    content = f.read()

# 1. Update ProfileAnalysisResult
content = content.replace(
    "data class ProfileAnalysisResult(val skinType: String, val concerns: List<String>, val goal: String, val explanation: String)",
    "data class ProfileAnalysisResult(val skinType: String, val concerns: List<String>, val goal: String, val explanation: String, val confidenceScore: Int = 0)"
)

# 2. Update IngredientAnalysisResponse
old_ingredient = """data class IngredientAnalysisResponse(
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
new_ingredient = """data class IngredientAnalysisResponse(
    val productName: String = "",
    val compatibilityScore: Int = 0,
    val compatibilityLabel: String = "",
    val compatibilityExplanation: String = "",
    val detectedIngredients: List<String> = emptyList(),
    val allergensAndIrritants: List<IngredientIssue> = emptyList(),
    val beneficialIngredients: List<IngredientBenefit> = emptyList(),
    val finalVerdict: String = "",
    val usageTips: String = "",
    val confidenceScore: Int = 0
)"""
content = content.replace(old_ingredient, new_ingredient)

# 3. Update analyzeSkinForProfile prompt
old_skin_prompt = """            ŞABLON:
            SKIN_TYPE: [Değer]
            CONCERNS: [Değerler]
            GOAL: [Değer]
            EXPLANATION: [Açıklama]"""
new_skin_prompt = """            ŞABLON:
            SKIN_TYPE: [Değer]
            CONCERNS: [Değerler]
            GOAL: [Değer]
            EXPLANATION: [Açıklama]
            CONFIDENCE: [Değer]"""
content = content.replace(old_skin_prompt, new_skin_prompt)

old_skin_values = """            - EXPLANATION için: Cildi fotoğraftan nasıl analiz ettiğini açıklayan samimi, Türkçe, maksimum 2 kısa cümlelik bir açıklama."""
new_skin_values = """            - EXPLANATION için: Cildi fotoğraftan nasıl analiz ettiğini açıklayan samimi, Türkçe, maksimum 2 kısa cümlelik bir açıklama.
            - CONFIDENCE için: 0 ile 100 arasında bir tamsayı. Görüntü kalitesi ve analizine ne kadar güvendiğini belirt."""
content = content.replace(old_skin_values, new_skin_values)

# 4. Update analyzeSkinForProfile parser
old_parser = """            var explanation = ""
            
            val lines = text.split("\\n")"""
new_parser = """            var explanation = ""
            var confidenceScore = 85
            
            val lines = text.split("\\n")"""
content = content.replace(old_parser, new_parser)

old_switch = """                        "EXPLANATION" -> {
                            explanation = value
                        }
                    }"""
new_switch = """                        "EXPLANATION" -> {
                            explanation = value
                        }
                        "CONFIDENCE" -> {
                            confidenceScore = value.toIntOrNull() ?: 85
                        }
                    }"""
content = content.replace(old_switch, new_switch)

old_return = """ProfileAnalysisResult(skinType, concernsList, goal, explanation)"""
new_return = """ProfileAnalysisResult(skinType, concernsList, goal, explanation, confidenceScore)"""
content = content.replace(old_return, new_return)

# 5. Update analyzeProductIngredients prompt
old_ing_schema = """              "usageTips": "Bu içerik kombinasyonuna göre özel bir kullanım önerisi (Örn: Sabahları güneş kremi ile birlikte kullanın veya nemli cilde uygulayın.)"
            }"""
new_ing_schema = """              "usageTips": "Bu içerik kombinasyonuna göre özel bir kullanım önerisi (Örn: Sabahları güneş kremi ile birlikte kullanın veya nemli cilde uygulayın.)",
              "confidenceScore": 85 // Metni okuma ve analiz güvenilirliğini gösteren 0-100 arası sayı
            }"""
content = content.replace(old_ing_schema, new_ing_schema)

with open("app/src/main/java/com/example/data/api/GeminiApi.kt", "w") as f:
    f.write(content)
