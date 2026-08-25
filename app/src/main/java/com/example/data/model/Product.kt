package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val sku: String,
    val title: String,
    val brand: String,
    val category: String,
    val subcategory: String,
    val price: Double,
    val mrp: Double,
    val discountPercent: Int,
    val rating: Float = 4.5f,
    val ratingCount: Int = 120,
    val stock: Int,
    val isAssured: Boolean = true,
    val isTrending: Boolean = false,
    val isDealOfDay: Boolean = false,
    val isFlashSale: Boolean = false,
    val description: String,
    val highlightsJson: String = "",
    val specsJson: String = "",
    val color: String = "Midnight Blue",
    val size: String = "Standard",
    val warranty: String = "1 Year Brand Warranty",
    val returnPolicyDays: Int = 7,
    val deliveryDays: Int = 2,
    val imageCategory: String = "electronics",
    val createdAt: Long = System.currentTimeMillis()
) {
    val formattedPrice: String
        get() = "₹%,d".format(price.toLong())

    val formattedMrp: String
        get() = "₹%,d".format(mrp.toLong())

    val savingsAmount: Double
        get() = (mrp - price).coerceAtLeast(0.0)

    val formattedSavings: String
        get() = "₹%,d".format(savingsAmount.toLong())

    val inStock: Boolean
        get() = stock > 0
}
