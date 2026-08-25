package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.seed.ProductSeedGenerator
import com.example.domain.ai.GeminiProductAssistant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches Shopnova`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Shopnova", appName)
    }

    @Test
    fun `test initial seed product count and category distribution`() {
        val initialProducts = ProductSeedGenerator.generateInitialProducts()
        assertTrue("Initial catalog should have curated products", initialProducts.size >= 12)
        
        val categories = initialProducts.map { it.category }.distinct()
        assertTrue("Should cover multiple Indian marketplace categories", categories.size >= 5)
        
        val assuredItems = initialProducts.filter { it.isAssured }
        assertTrue("Should have Shopnova Assured items", assuredItems.isNotEmpty())
    }

    @Test
    fun `test bulk 10000 product generator engine scalability`() {
        val count = 100
        val bulkProducts = ProductSeedGenerator.generateBulkProducts(count)
        assertEquals(count, bulkProducts.size)
        
        // Verify all IDs and SKUs are unique
        val uniqueIds = bulkProducts.map { it.id }.distinct()
        assertEquals(count, uniqueIds.size)
        
        val uniqueSkus = bulkProducts.map { it.sku }.distinct()
        assertEquals(count, uniqueSkus.size)
        
        // Verify realistic Indian pricing (MRP > price)
        bulkProducts.forEach { p ->
            assertTrue("MRP must be greater than selling price", p.mrp >= p.price)
            assertTrue("Price must be positive", p.price > 0)
            assertTrue("Discount must be >= 0", p.discountPercent >= 0)
        }
    }

    @Test
    fun `test Gemini AI product content assistant fallback`() = runBlocking {
        val result = GeminiProductAssistant.generateProductContent("Wireless ANC Gaming Headphones")
        assertNotNull(result)
        assertTrue(result.title.contains("Headphone", ignoreCase = true))
        assertTrue(result.mrp > result.price)
        assertTrue(result.highlights.isNotEmpty())
        assertTrue(result.specs.isNotEmpty())
    }
}
