import re

with open("app/src/main/java/com/example/ui/viewmodel/SkinAnalysisViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("private val _scanProfileAnalysis = MutableStateFlow<ProfileAnalysisResult?>(null)",
"""private val _lastScannedPhotoPath = MutableStateFlow<String?>(null)
    val lastScannedPhotoPath: StateFlow<String?> = _lastScannedPhotoPath.asStateFlow()

    private val _scanProfileAnalysis = MutableStateFlow<ProfileAnalysisResult?>(null)""")

content = content.replace("fun analyzeScanForProfile(photoPath: String) {",
"""fun analyzeScanForProfile(photoPath: String) {
        _lastScannedPhotoPath.value = photoPath""")

with open("app/src/main/java/com/example/ui/viewmodel/SkinAnalysisViewModel.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("private val _scanProfileAnalysis = MutableStateFlow<com.example.data.api.ProfileAnalysisResult?>(null)",
"""private val _lastScannedPhotoPath = MutableStateFlow<String?>(null)
    val lastScannedPhotoPath: StateFlow<String?> = _lastScannedPhotoPath.asStateFlow()

    private val _scanProfileAnalysis = MutableStateFlow<com.example.data.api.ProfileAnalysisResult?>(null)""")

content = content.replace("fun analyzeScanForProfile(photoPath: String) {",
"""fun analyzeScanForProfile(photoPath: String) {
        _lastScannedPhotoPath.value = photoPath""")

with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "w") as f:
    f.write(content)
