package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class Address(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phone: String,
    val altPhone: String = "",
    val houseNo: String,
    val area: String,
    val landmark: String = "",
    val city: String,
    val state: String,
    val pincode: String,
    val addressType: String = "Home", // Home, Work, Other
    val isDefault: Boolean = false
) {
    val fullAddressText: String
        get() = buildString {
            append(houseNo)
            if (area.isNotEmpty()) append(", $area")
            if (landmark.isNotEmpty()) append(", Near $landmark")
            append(", $city, $state - $pincode")
        }
}

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val customerName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val isVerifiedBuyer: Boolean = true,
    val helpfulCount: Int = (1..45).random(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey val code: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minOrder: Double,
    val description: String,
    val isActive: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String = "ORDER", // ORDER, OFFER, SYSTEM
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
