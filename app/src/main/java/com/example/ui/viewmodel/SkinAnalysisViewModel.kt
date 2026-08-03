package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiRepository
import com.example.data.api.ProfileAnalysisResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SkinAnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val _scanProfileAnalysis = MutableStateFlow<ProfileAnalysisResult?>(null)
    val scanProfileAnalysis: StateFlow<ProfileAnalysisResult?> = _scanProfileAnalysis.asStateFlow()

    private val _diaryAnalysis = MutableStateFlow<String?>(null)
    val diaryAnalysis: StateFlow<String?> = _diaryAnalysis.asStateFlow()

    private val _isScanLoading = MutableStateFlow(false)
    val isScanLoading: StateFlow<Boolean> = _isScanLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun analyzeScanForProfile(photoPath: String) {
        viewModelScope.launch {
            _isScanLoading.value = true
            _errorMessage.value = null
            try {
                val result = GeminiRepository.analyzeSkinForProfile(photoPath)
                if (result != null) {
                    _scanProfileAnalysis.value = result
                } else {
                    _errorMessage.value = "Analiz yapılamadı. Lütfen tekrar deneyin."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = e.localizedMessage ?: "Bilinmeyen bir hata oluştu."
            } finally {
                _isScanLoading.value = false
            }
        }
    }

    fun analyzeDiaryPhoto(photoPath: String, userNote: String) {
        viewModelScope.launch {
            _isScanLoading.value = true
            _errorMessage.value = null
            try {
                val result = GeminiRepository.analyzeSkinPhoto(photoPath, userNote)
                _diaryAnalysis.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = e.localizedMessage ?: "Bilinmeyen bir hata oluştu."
            } finally {
                _isScanLoading.value = false
            }
        }
    }

    fun clearScanAnalysis() {
        _scanProfileAnalysis.value = null
        _errorMessage.value = null
    }
    
    fun clearDiaryAnalysis() {
        _diaryAnalysis.value = null
        _errorMessage.value = null
    }
}
