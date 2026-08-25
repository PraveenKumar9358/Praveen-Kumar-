package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.*

@Composable
fun AnimatedOrderTrackingTimeline(
    order: Order,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        Triple("Order Placed", "We have received your order", Icons.Default.Receipt),
        Triple("Order Confirmed", "Seller has accepted the order", Icons.Default.CheckCircle),
        Triple("Packed at Hub", "Item packed in tamper-proof bag", Icons.Default.Inventory2),
        Triple("Shipped via BlueDart/Ekart", "Package in transit to nearest hub", Icons.Default.LocalShipping),
        Triple("Out for Delivery", "Courier executive assigned for delivery", Icons.Default.DeliveryDining),
        Triple("Delivered", "Delivered & Verified", Icons.Default.DoneAll)
    )

    val currentStep = when (order.statusEnum) {
        OrderStatus.PLACED -> 0
        OrderStatus.CONFIRMED -> 1
        OrderStatus.PACKED -> 2
        OrderStatus.SHIPPED -> 3
        OrderStatus.OUT_FOR_DELIVERY -> 4
        OrderStatus.DELIVERED -> 5
        OrderStatus.RETURN_REQUESTED -> -1
        OrderStatus.RETURNED -> -2
        OrderStatus.CANCELLED -> -3
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delivery Tracking",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        currentStep == 5 -> ShopnovaGreenLight
                        currentStep < 0 -> ShopnovaRedLight
                        else -> ShopnovaBlueSoft
                    }
                ) {
                    Text(
                        text = order.orderStatus.replace("_", " "),
                        color = when {
                            currentStep == 5 -> ShopnovaGreen
                            currentStep < 0 -> ShopnovaRed
                            else -> ShopnovaBlue
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            if (currentStep < 0) {
                // Return / Cancelled state
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (currentStep == -3) Icons.Default.Cancel else Icons.Default.AssignmentReturn,
                        contentDescription = null,
                        tint = ShopnovaRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (currentStep == -3) "Order Cancelled" else "Return Requested",
                            fontWeight = FontWeight.Bold,
                            color = ShopnovaRed,
                            fontSize = 14.sp
                        )
                        if (order.returnReason.isNotEmpty()) {
                            Text(
                                text = "Reason: ${order.returnReason}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                // Standard 6-step timeline
                steps.forEachIndexed { index, (title, subtitle, icon) ->
                    val isDone = index <= currentStep
                    val isCurrent = index == currentStep

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Step node with line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isDone -> ShopnovaGreen
                                            else -> Color(0xFFE0E0E0)
                                        }
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.Check else icon,
                                    contentDescription = null,
                                    tint = if (isDone) Color.White else Color(0xFF9E9E9E),
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            if (index < steps.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(34.dp)
                                        .background(
                                            if (index < currentStep) ShopnovaGreen else Color(0xFFE0E0E0)
                                        )
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        // Step info text
                        Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDone) TextPrimary else TextSecondary
                            )
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
