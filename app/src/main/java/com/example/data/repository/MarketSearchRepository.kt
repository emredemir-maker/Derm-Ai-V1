package com.example.data.repository

import java.net.URI
import java.net.URLEncoder

data class MarketSearchItem(
    val platformName: String,
    val searchUrl: String
)

object MarketSearchRepository {

    private val ALLOWED_DOMAINS = setOf(
        "trendyol.com",
        "www.trendyol.com",
        "hepsiburada.com",
        "www.hepsiburada.com",
        "amazon.com.tr",
        "www.amazon.com.tr",
        "watsons.com.tr",
        "www.watsons.com.tr",
        "gratis.com",
        "www.gratis.com"
    )

    fun getStoreSearchLinks(productName: String?): List<MarketSearchItem> {
        if (productName.isNullOrBlank()) {
            return emptyList()
        }

        val trimmedName = productName.trim()
        val encodedQuery = try {
            URLEncoder.encode(trimmedName, "UTF-8")
        } catch (e: Exception) {
            return emptyList()
        }

        val items = listOf(
            MarketSearchItem("Trendyol", "https://www.trendyol.com/sr?q=$encodedQuery"),
            MarketSearchItem("Hepsiburada", "https://www.hepsiburada.com/ara?q=$encodedQuery"),
            MarketSearchItem("Amazon.com.tr", "https://www.amazon.com.tr/s?k=$encodedQuery"),
            MarketSearchItem("Watsons", "https://www.watsons.com.tr/search?q=$encodedQuery"),
            MarketSearchItem("Gratis", "https://www.gratis.com/search?text=$encodedQuery")
        )

        return items.filter { item ->
            try {
                val uri = URI(item.searchUrl)
                val schemeMatches = uri.scheme?.equals("https", ignoreCase = true) == true
                val hostMatches = isAllowedHost(uri.host)
                schemeMatches && hostMatches
            } catch (e: Exception) {
                false
            }
        }
    }

    fun isAllowedHost(host: String?): Boolean {
        if (host.isNullOrBlank()) return false
        return ALLOWED_DOMAINS.contains(host.lowercase())
    }
}
