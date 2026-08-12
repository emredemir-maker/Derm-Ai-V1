package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "skin_profile")
data class SkinProfile(
    @PrimaryKey val id: Int = 1,
    val userName: String = "",
    val age: Int = 0,
    val gender: String = "",
    val skinType: String,
    val skinConcerns: String, // Comma-separated list (e.g. "Akne, Lekeler")
    val skincareGoal: String,
    val makeupPreference: String,
    val allergies: String = "", // Added for user allergies (e.g. "Parfüm, Paraben")
    val lastAnalysisRoutine: String? = null,
    val lastAnalysisMakeup: String? = null,
    val lastAnalysisDate: Long = 0L,
    val lastFaceAnalysisJson: String? = null,
    val lastFacePhotoPath: String? = null,
    val lastFaceAnalysisDate: Long = 0L
)

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val note: String,
    val rating: Int, // 1 to 5 (or emoji representation)
    val photoPath: String?, // Internal path, asset name, or mock image code
    val aiFeedback: String? = null
)

@JsonClass(generateAdapter = true)
data class ProductSuggestion(
    val name: String,
    val category: String, // e.g. "Nemlendirici", "Serum", "Güneş Kremi", "Temizleyici", "Fondöten", "Kapatıcı", "Astar (Primer)"
    val activeIngredients: String, // e.g. "Hiyalüronik Asit, Seramidler"
    val description: String, // Why it's recommended
    val usageTip: String // How to use
)

@Entity(tableName = "skin_type_recommendations")
data class SkinTypeRecommendation(
    @PrimaryKey val skinType: String, // "Kuru", "Yağlı", "Karma", "Hassas", "Normal"
    val creamSuggestionsJson: String, // JSON representation of List<ProductSuggestion>
    val makeupSuggestionsJson: String, // JSON representation of List<ProductSuggestion>
    val generalTips: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val brand: String,
    val type: String, // "Makyaj" veya "Cilt Bakımı"
    val category: String, // e.g. "Fondöten", "Nemlendirici", "Serum", "Güneş Kremi"
    val openedDate: Long = System.currentTimeMillis(),
    val shelfLifeMonths: Int = 12, // Expiry period after opening in months
    val compatibilityScore: Int = 0, // Compatibility score with user's skin profile
    val ingredients: String = "", // Ingredients list
    val notes: String? = null
)
