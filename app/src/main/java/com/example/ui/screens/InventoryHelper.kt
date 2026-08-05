package com.example.ui.screens

import com.example.data.database.InventoryItem
import java.util.Calendar
import java.util.Date

enum class InventoryStatus {
    ACTIVE,
    EXPIRING_SOON,
    EXPIRED
}

object InventoryHelper {

    fun calculateExpiryDate(openedDate: Long, shelfLifeMonths: Int): Date {
        val calendar = Calendar.getInstance().apply {
            time = Date(openedDate)
            add(Calendar.MONTH, shelfLifeMonths)
        }
        return calendar.time
    }

    fun getInventoryStatus(openedDate: Long, shelfLifeMonths: Int): InventoryStatus {
        val expiryDate = calculateExpiryDate(openedDate, shelfLifeMonths)
        val now = Date()
        if (now.after(expiryDate)) {
            return InventoryStatus.EXPIRED
        }

        val thirtyDaysLater = Calendar.getInstance().apply {
            time = now
            add(Calendar.DAY_OF_YEAR, 30)
        }.time

        if (expiryDate.before(thirtyDaysLater) || expiryDate == thirtyDaysLater) {
            return InventoryStatus.EXPIRING_SOON
        }

        return InventoryStatus.ACTIVE
    }

    fun filterAndSearchItems(
        items: List<InventoryItem>,
        query: String,
        typeFilter: String // "Tümü", "Cilt Bakımı", "Makyaj"
    ): List<InventoryItem> {
        return items.filter { item ->
            val matchesType = when (typeFilter) {
                "Cilt Bakımı" -> item.type.equals("Cilt Bakımı", ignoreCase = true)
                "Makyaj" -> item.type.equals("Makyaj", ignoreCase = true)
                else -> true
            }

            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                item.name.lowercase().contains(q) ||
                item.brand.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.ingredients.lowercase().contains(q) ||
                (item.notes?.lowercase()?.contains(q) == true)

            matchesType && matchesQuery
        }
    }

    fun getCompatibilityText(score: Int): String {
        return if (score == 0) "Analiz edilmedi" else "%$score uyum"
    }

    fun canRunWeeklyCheck(items: List<InventoryItem>): Boolean {
        if (items.isEmpty()) return false
        return items.any { it.ingredients.isNotBlank() }
    }
}
