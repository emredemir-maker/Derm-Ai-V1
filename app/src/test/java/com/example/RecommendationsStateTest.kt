package com.example

import com.example.data.database.ProductSuggestion
import com.example.ui.screens.RecommendationsHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationsStateTest {

    private val validCreamJson = """
        [
          {
            "name": "Işıltı Veren Günlük Nemlendirici",
            "category": "Nemlendirici",
            "activeIngredients": "C Vitamini, Skualen, Niasinamid",
            "description": "Cildin doğal nemini ve dengesini korur.",
            "usageTip": "Her sabah ve akşam temiz yüze dairesel masajla uygulayın."
          },
          {
            "name": "Nazik Arındırıcı Köpük Jel",
            "category": "Temizleyici",
            "activeIngredients": "Hiyalüronik Asit, Amino Asitler",
            "description": "Gözenekleri kirden arındırır.",
            "usageTip": "Günde iki kez ıslak yüzünüze uygulayın."
          }
        ]
    """.trimIndent()

    private val validMakeupJson = """
        [
          {
            "name": "Doğal Işıltılı Saten Fondöten",
            "category": "Fondöten",
            "activeIngredients": "Gliserin, Vitamin Kompleksi",
            "description": "Cildin doğal güzelliğini ortaya çıkarır.",
            "usageTip": "Nemli sünger ile uygulayın."
          }
        ]
    """.trimIndent()

    @Test
    fun testValidCreamJsonParsing() {
        val result = RecommendationsHelper.parseProductSuggestions(validCreamJson)
        assertFalse(result.isError)
        assertEquals(2, result.items.size)
        assertEquals("Işıltı Veren Günlük Nemlendirici", result.items[0].name)
        assertEquals("Nemlendirici", result.items[0].category)
        assertEquals("C Vitamini, Skualen, Niasinamid", result.items[0].activeIngredients)
    }

    @Test
    fun testValidMakeupJsonParsing() {
        val result = RecommendationsHelper.parseProductSuggestions(validMakeupJson)
        assertFalse(result.isError)
        assertEquals(1, result.items.size)
        assertEquals("Doğal Işıltılı Saten Fondöten", result.items[0].name)
        assertEquals("Fondöten", result.items[0].category)
    }

    @Test
    fun testCorruptedJsonDoesNotCrashAndProducesError() {
        val corruptedJson = "{ invalid json content ... "
        val result = RecommendationsHelper.parseProductSuggestions(corruptedJson)
        assertTrue(result.isError)
        assertNotNull(result.errorMessage)
        assertTrue(result.items.isEmpty())
    }

    @Test
    fun testEmptyJsonReturnsEmptyListWithoutError() {
        val result = RecommendationsHelper.parseProductSuggestions("")
        assertFalse(result.isError)
        assertTrue(result.items.isEmpty())

        val resultNull = RecommendationsHelper.parseProductSuggestions(null)
        assertFalse(resultNull.isError)
        assertTrue(resultNull.items.isEmpty())
    }

    @Test
    fun testSearchByProductNameAndActiveIngredients() {
        val creamResult = RecommendationsHelper.parseProductSuggestions(validCreamJson)
        val items = creamResult.items

        // Search by product name
        val nameMatch = RecommendationsHelper.filterProducts(items, "Arındırıcı", "Tümü")
        assertEquals(1, nameMatch.size)
        assertEquals("Nazik Arındırıcı Köpük Jel", nameMatch[0].name)

        // Search by active ingredients
        val ingredientMatch = RecommendationsHelper.filterProducts(items, "Skualen", "Tümü")
        assertEquals(1, ingredientMatch.size)
        assertEquals("Işıltı Veren Günlük Nemlendirici", ingredientMatch[0].name)
    }

    @Test
    fun testCategoryFilterReturnsMatchingDynamicSuggestionsOnly() {
        val creamResult = RecommendationsHelper.parseProductSuggestions(validCreamJson)
        val items = creamResult.items

        val cleanserOnly = RecommendationsHelper.filterProducts(items, "", "Temizleyici")
        assertEquals(1, cleanserOnly.size)
        assertEquals("Nazik Arındırıcı Köpük Jel", cleanserOnly[0].name)

        val nonExistentCategory = RecommendationsHelper.filterProducts(items, "", "Makyaj")
        assertEquals(0, nonExistentCategory.size)
    }

    @Test
    fun testSearchAndCategoryFilterWorkTogether() {
        val creamResult = RecommendationsHelper.parseProductSuggestions(validCreamJson)
        val items = creamResult.items

        // Category matches Nemlendirici AND search matches Niasinamid -> match
        val match = RecommendationsHelper.filterProducts(items, "Niasinamid", "Nemlendirici")
        assertEquals(1, match.size)

        // Category matches Temizleyici BUT search demands Niasinamid -> no match
        val noMatch = RecommendationsHelper.filterProducts(items, "Niasinamid", "Temizleyici")
        assertEquals(0, noMatch.size)
    }

    @Test
    fun testProductSuggestionModelHasOnlyRealFieldsAndNoPriceBrandOrPercentage() {
        val product = ProductSuggestion(
            name = "Örnek Ürün",
            category = "Serum",
            activeIngredients = "Niasinamid",
            description = "Açıklama",
            usageTip = "Kullanım İpucu"
        )
        val fieldNames = ProductSuggestion::class.java.declaredFields.map { it.name }
        assertTrue(fieldNames.contains("name"))
        assertTrue(fieldNames.contains("category"))
        assertTrue(fieldNames.contains("activeIngredients"))
        assertTrue(fieldNames.contains("description"))
        assertTrue(fieldNames.contains("usageTip"))

        assertFalse(fieldNames.contains("price"))
        assertFalse(fieldNames.contains("brand"))
        assertFalse(fieldNames.contains("store"))
        assertFalse(fieldNames.contains("matchPercentage"))
        assertFalse(fieldNames.contains("rating"))
        assertFalse(fieldNames.contains("lowestPrice"))
    }
}
