package com.example

import com.example.data.database.InventoryItem
import com.example.ui.screens.InventoryHelper
import com.example.ui.screens.InventoryStatus
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class InventoryHelperTest {

    @Test
    fun testExpiryDateCalculation() {
        val cal = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 15, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val opened = cal.timeInMillis
        val expiry = InventoryHelper.calculateExpiryDate(opened, 12)

        val expectedCal = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 15, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        assertEquals(expectedCal.time, expiry)
    }

    @Test
    fun testLeapYearAndMonthEnd() {
        // Feb 29, 2024 (leap year) + 12 months -> Feb 28, 2025
        val cal = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 29, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expiry = InventoryHelper.calculateExpiryDate(cal.timeInMillis, 12)
        val expectedCal = Calendar.getInstance().apply {
            set(2025, Calendar.FEBRUARY, 28, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        assertEquals(expectedCal.time, expiry)
    }

    @Test
    fun testInventoryStatusClassification() {
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        // Expired (opened long ago, 1 month shelf life)
        val expiredItemStatus = InventoryHelper.getInventoryStatus(now - (60L * dayInMillis), 1)
        assertEquals(InventoryStatus.EXPIRED, expiredItemStatus)
    }

    @Test
    fun testSearchAndFilter() {
        val items = listOf(
            InventoryItem(id = 1, name = "Nemlendirici Krem", brand = "La Roche", type = "Cilt Bakımı", category = "Krem", ingredients = "Glycerin, Niacinamide", notes = "Harika"),
            InventoryItem(id = 2, name = "Mat Fondöten", brand = "Maybelline", type = "Makyaj", category = "Fondöten", ingredients = "Dimethicone, Talc", notes = null)
        )

        val filteredByType = InventoryHelper.filterAndSearchItems(items, "", "Makyaj")
        assertEquals(1, filteredByType.size)
        assertEquals("Mat Fondöten", filteredByType[0].name)

        val filteredByQuery = InventoryHelper.filterAndSearchItems(items, "Niacinamide", "Tümü")
        assertEquals(1, filteredByQuery.size)
        assertEquals(1, filteredByQuery[0].id)
    }

    @Test
    fun testWeeklyCheckValidation() {
        val emptyList = emptyList<InventoryItem>()
        assertFalse(InventoryHelper.canRunWeeklyCheck(emptyList))

        val noIngredientsList = listOf(
            InventoryItem(id = 1, name = "Krem", brand = "Brand", type = "Cilt Bakımı", category = "Krem", ingredients = "")
        )
        assertFalse(InventoryHelper.canRunWeeklyCheck(noIngredientsList))

        val validList = listOf(
            InventoryItem(id = 1, name = "Krem", brand = "Brand", type = "Cilt Bakımı", category = "Krem", ingredients = "Water, Glycerin")
        )
        assertTrue(InventoryHelper.canRunWeeklyCheck(validList))
    }

    @Test
    fun testCompatibilityText() {
        assertEquals("Analiz edilmedi", InventoryHelper.getCompatibilityText(0))
        assertEquals("%85 uyum", InventoryHelper.getCompatibilityText(85))
    }
}
