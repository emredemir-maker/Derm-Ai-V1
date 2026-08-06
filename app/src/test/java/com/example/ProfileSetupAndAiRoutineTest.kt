package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.api.GeminiRepository
import com.example.data.database.AppDatabase
import com.example.data.database.SkinDao
import com.example.data.database.SkinProfile
import com.example.ui.viewmodel.SkinCareViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ProfileSetupAndAiRoutineTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var database: AppDatabase
    private lateinit var dao: SkinDao
    private lateinit var viewModel: SkinCareViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.skinDao()

        viewModel = SkinCareViewModel(ApplicationProvider.getApplicationContext(), dao, Dispatchers.IO)
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun testProfileSetupAndAiRoutineSuccess() = runBlocking(Dispatchers.IO) {
        val mockRoutineResponse = "1. BÖLÜM: CİLT BAKIM RUTİNİ VE TAVSİYELERİ\n- Sabah: Nazik Yüz Yıkama Jeli\n- Akşam: Salisilik Asit Tonik\n\n2. BÖLÜM: MAKYAJ TAVSİYELERİ\n- Hafif Nemlendirici Renkli Krem"
        val fakeClient = FakeAiModelClient(mockResponse = mockRoutineResponse)
        GeminiRepository.aiClient = fakeClient

        var successCalled = false

        viewModel.saveProfileAndGenerateAIRoutine(
            userName = "Ayşe",
            age = 29,
            gender = "Kadın",
            skinType = "Karma",
            skinConcerns = listOf("Akne & Sivilce", "Geniş Gözenekler"),
            skincareGoal = "Sivilce Kontrolü",
            makeupPreference = "Doğal & Hafif",
            onSuccess = { successCalled = true }
        )

        var savedProfile: SkinProfile? = null
        for (i in 1..40) {
            savedProfile = dao.getSkinProfileDirect()
            if (savedProfile?.lastAnalysisRoutine != null) break
            delay(100)
        }

        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull("Saved profile should not be null", savedProfile)
        assertEquals("Ayşe", savedProfile?.userName)
        assertEquals(29, savedProfile?.age)
        assertEquals("Kadın", savedProfile?.gender)
        assertEquals("Karma", savedProfile?.skinType)
        assertTrue(savedProfile?.lastAnalysisRoutine?.contains("Nazik Yüz Yıkama Jeli") == true)
        assertTrue(successCalled)
        assertNull(viewModel.analysisError.value)
    }

    @Test
    fun testProfileSetupAndAiRoutineFailurePreservesProfile() = runBlocking(Dispatchers.IO) {
        val fakeClient = FakeAiModelClient(shouldThrow = true, exceptionToThrow = RuntimeException("Ağ hatası"))
        GeminiRepository.aiClient = fakeClient

        var successCalled = false

        viewModel.saveProfileAndGenerateAIRoutine(
            userName = "Mehmet",
            age = 34,
            gender = "Erkek",
            skinType = "Kuru",
            skinConcerns = listOf("Kuruluk & Pullanma"),
            skincareGoal = "Yoğun Nemlendirme",
            makeupPreference = "Makyaj Kullanmıyorum",
            onSuccess = { successCalled = true }
        )

        var savedProfile: SkinProfile? = null
        for (i in 1..40) {
            savedProfile = dao.getSkinProfileDirect()
            if (savedProfile != null) break
            delay(100)
        }

        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull("Saved profile should not be null", savedProfile)
        assertEquals("Mehmet", savedProfile?.userName)
        assertEquals(34, savedProfile?.age)
        assertEquals("Erkek", savedProfile?.gender)
        assertFalse(successCalled) // Did not navigate to home screen
        assertNotNull(viewModel.analysisError.value)
        assertTrue(viewModel.analysisError.value?.contains("Yapay zeka yanıtı alınamadı") == true || viewModel.analysisError.value?.contains("hata") == true)
    }

    @Test
    fun testProfileSetupWithEmptyMakeupPreferencePreservesEmptyValue() = runBlocking(Dispatchers.IO) {
        val mockRoutineResponse = "1. BÖLÜM: CİLT BAKIM RUTİNİ\n- Sabah: Su ile yıka\n\n2. BÖLÜM: MAKYAJ TAVSİYELERİ\n- Makyaj kullanılmıyor."
        val fakeClient = FakeAiModelClient(mockResponse = mockRoutineResponse)
        GeminiRepository.aiClient = fakeClient

        var successCalled = false

        viewModel.saveProfileAndGenerateAIRoutine(
            userName = "Zeynep",
            skinType = "Normal",
            skinConcerns = emptyList(),
            skincareGoal = "Nemlendirme",
            makeupPreference = "",
            onSuccess = { successCalled = true }
        )

        var savedProfile: SkinProfile? = null
        for (i in 1..40) {
            savedProfile = dao.getSkinProfileDirect()
            if (savedProfile?.lastAnalysisRoutine != null) break
            delay(100)
        }

        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull("Saved profile should not be null", savedProfile)
        assertEquals("Zeynep", savedProfile?.userName)
        assertEquals("", savedProfile?.makeupPreference)
        assertTrue(successCalled)
    }
}
