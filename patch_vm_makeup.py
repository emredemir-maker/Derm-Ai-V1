import re

with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "r") as f:
    content = f.read()

makeup_state = """
    private val _makeupAnalysisResult = MutableStateFlow<com.example.data.api.MakeupAnalysisResult?>(null)
    val makeupAnalysisResult: StateFlow<com.example.data.api.MakeupAnalysisResult?> = _makeupAnalysisResult.asStateFlow()
    
    private val _makeupPhotoPath = MutableStateFlow<String?>(null)
    val makeupPhotoPath: StateFlow<String?> = _makeupPhotoPath.asStateFlow()

    fun analyzeMakeup(photoPath: String) {
        viewModelScope.launch {
            _isScanLoading.value = true
            _makeupPhotoPath.value = photoPath
            try {
                val result = GeminiRepository.analyzeMakeup(photoPath)
                _makeupAnalysisResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isScanLoading.value = false
            }
        }
    }
"""

# add it near clearScanAnalysis
content = content.replace("fun clearScanAnalysis() {", makeup_state + "\n    fun clearScanAnalysis() {")

with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/data/api/GeminiRepository.kt", "r") as f:
    repo_content = f.read()

repo_func = """
    suspend fun analyzeMakeup(photoPath: String): com.example.data.api.MakeupAnalysisResult? {
        return GeminiApi.analyzeMakeup(photoPath)
    }
"""

repo_content = repo_content.replace("suspend fun fetchCustomRecommendations(", repo_func + "\n    suspend fun fetchCustomRecommendations(")

with open("app/src/main/java/com/example/data/api/GeminiRepository.kt", "w") as f:
    f.write(repo_content)

