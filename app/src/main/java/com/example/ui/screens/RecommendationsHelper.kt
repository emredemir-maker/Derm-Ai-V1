package com.example.ui.screens

import com.example.data.database.ProductSuggestion
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class RecommendationParseResult(
    val items: List<ProductSuggestion> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String? = null
)

object RecommendationsHelper {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val listType = Types.newParameterizedType(List::class.java, ProductSuggestion::class.java)

    fun parseProductSuggestions(json: String?): RecommendationParseResult {
        if (json.isNullOrBlank()) {
            return RecommendationParseResult(items = emptyList(), isError = false)
        }
        return try {
            val adapter = moshi.adapter<List<ProductSuggestion>>(listType)
            val parsed = adapter.fromJson(json)
            if (parsed != null) {
                RecommendationParseResult(items = parsed, isError = false)
            } else {
                RecommendationParseResult(
                    items = emptyList(),
                    isError = true,
                    errorMessage = "JSON verisi boş veya ayrıştırılamadı."
                )
            }
        } catch (e: Exception) {
            RecommendationParseResult(
                items = emptyList(),
                isError = true,
                errorMessage = "JSON formatı geçersiz: ${e.message}"
            )
        }
    }

    fun filterProducts(
        items: List<ProductSuggestion>,
        searchQuery: String,
        selectedCategory: String
    ): List<ProductSuggestion> {
        val query = searchQuery.trim().lowercase()
        return items.filter { item ->
            val matchesCategory = selectedCategory == "Tümü" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = query.isEmpty() ||
                    item.name.lowercase().contains(query) ||
                    item.category.lowercase().contains(query) ||
                    item.activeIngredients.lowercase().contains(query) ||
                    item.description.lowercase().contains(query)
            matchesCategory && matchesSearch
        }
    }
}
