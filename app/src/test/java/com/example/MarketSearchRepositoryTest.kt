package com.example

import com.example.data.repository.MarketSearchItem
import com.example.data.repository.MarketSearchRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class MarketSearchRepositoryTest {

    @Test
    fun testGeneratesHttpsLinksForFiveStores() {
        val links = MarketSearchRepository.getStoreSearchLinks("Niasinamid Serum")
        assertEquals(5, links.size)

        val names = links.map { it.platformName }
        assertTrue(names.contains("Trendyol"))
        assertTrue(names.contains("Hepsiburada"))
        assertTrue(names.contains("Amazon.com.tr"))
        assertTrue(names.contains("Watsons"))
        assertTrue(names.contains("Gratis"))

        links.forEach { item ->
            assertTrue("Link must start with https://", item.searchUrl.startsWith("https://"))
        }
    }

    @Test
    fun testEachLinkHostMatchesAllowedDomains() {
        val links = MarketSearchRepository.getStoreSearchLinks("Güneş Kremi")
        links.forEach { item ->
            val uri = URI(item.searchUrl)
            assertEquals("https", uri.scheme)
            assertTrue("Host ${uri.host} must be in allowed domains", MarketSearchRepository.isAllowedHost(uri.host))
        }
    }

    @Test
    fun testPercentEncodingForSpecificProductName() {
        val links = MarketSearchRepository.getStoreSearchLinks("C Vitamini %10 Serum")
        assertEquals(5, links.size)
        links.forEach { item ->
            assertTrue(item.searchUrl.contains("%2510") || item.searchUrl.contains("%25"))
            assertFalse(item.searchUrl.contains(" %10 "))
        }
    }

    @Test
    fun testTurkishCharactersInUrlEncoding() {
        val turkishQuery = "Salyangoz Özlü Nemlendirici ĞÜŞİÖÇ ğüşiöç"
        val links = MarketSearchRepository.getStoreSearchLinks(turkishQuery)
        assertEquals(5, links.size)
        links.forEach { item ->
            val uri = URI(item.searchUrl)
            assertNotNull(uri.host)
            assertFalse(item.searchUrl.contains("Ğ"))
            assertFalse(item.searchUrl.contains("ğ"))
            assertFalse(item.searchUrl.contains(" "))
        }
    }

    @Test
    fun testSpecialCharactersDoNotEscapeQuery() {
        val maliciousQuery = "Serum&host=evil.com?param=1/path"
        val links = MarketSearchRepository.getStoreSearchLinks(maliciousQuery)
        assertEquals(5, links.size)
        links.forEach { item ->
            val uri = URI(item.searchUrl)
            assertTrue(MarketSearchRepository.isAllowedHost(uri.host))
            assertFalse("Host must not be evil.com", item.searchUrl.contains("evil.com?"))
        }
    }

    @Test
    fun testEmptyOrBlankProductNameReturnsEmptyList() {
        assertTrue(MarketSearchRepository.getStoreSearchLinks("").isEmpty())
        assertTrue(MarketSearchRepository.getStoreSearchLinks("   ").isEmpty())
        assertTrue(MarketSearchRepository.getStoreSearchLinks(null).isEmpty())
    }

    @Test
    fun testUserInputCannotTamperHostOrScheme() {
        val inputWithProtocol = "https://phishing.com/test?query=abc"
        val links = MarketSearchRepository.getStoreSearchLinks(inputWithProtocol)
        assertEquals(5, links.size)
        links.forEach { item ->
            val uri = URI(item.searchUrl)
            assertEquals("https", uri.scheme)
            assertTrue(MarketSearchRepository.isAllowedHost(uri.host))
            assertTrue(item.searchUrl.contains("q=") || item.searchUrl.contains("k=") || item.searchUrl.contains("text="))
        }
    }

    @Test
    fun testResultModelContainsOnlyPlatformNameAndSearchUrl() {
        val fields = MarketSearchItem::class.java.declaredFields
            .filter { !it.isSynthetic && !it.name.startsWith("$") }
            .map { it.name }
        assertEquals("Expected 2 fields (platformName, searchUrl) but found: $fields", 2, fields.size)
        assertTrue(fields.contains("platformName"))
        assertTrue(fields.contains("searchUrl"))

        // Verify NO price, campaign, discount or cheapest store fields exist
        assertFalse(fields.contains("price"))
        assertFalse(fields.contains("campaign"))
        assertFalse(fields.contains("cheapest"))
        assertFalse(fields.contains("discount"))
        assertFalse(fields.contains("estimatedPrice"))
    }
}
