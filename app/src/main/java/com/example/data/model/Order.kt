package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val label: String, val stepIndex: Int) {
    PLACED("Order Placed", 0),
    CONFIRMED("Order Confirmed", 1),
    PACKED("Packed at Hub", 2),
    SHIPPED("Shipped with Ekart", 3),
    OUT_FOR_DELIVERY("Out for Delivery", 4),
    DELIVERED("Delivered", 5),
    RETURN_REQUESTED("Return Requested", -1),
    RETURNED("Returned & Refunded", -2),
    CANCELLED("Cancelled", -3)
}

enum class PaymentMethod(val label: String) {
    UPI("UPI (GPay / PhonePe / Paytm)"),
    CREDIT_DEBIT_CARD("Credit / Debit Card"),
    NET_BANKING("Net Banking"),
    CASH_ON_DELIVERY("Cash on Delivery (COD)"),
    SHOPNOVA_WALLET("Shopnova Pay")
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String,
    val customerName: String,
    val customerPhone: String,
    val addressLine: String,
    val city: String,
    val state: String,
    val pincode: String,
    val addressType: String = "Home",
    val itemsSummaryJson: String,
    val totalAmount: Double,
    val discountAmount: Double,
    val deliveryFee: Double = 0.0,
    val paymentMethod: String,
    val paymentStatus: String = "SUCCESS",
    val orderStatus: String = OrderStatus.PLACED.name,
    val expectedDeliveryDate: String,
    val returnReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val statusEnum: OrderStatus
        get() = try {
            OrderStatus.valueOf(orderStatus)
        } catch (e: Exception) {
            OrderStatus.PLACED
        }

    val formattedTotal: String
        get() = "₹%,d".format(totalAmount.toLong())

    val formattedDate: String
        get() {
            val sdf = java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.ENGLISH)
            return sdf.format(java.util.Date(createdAt))
        }
}
