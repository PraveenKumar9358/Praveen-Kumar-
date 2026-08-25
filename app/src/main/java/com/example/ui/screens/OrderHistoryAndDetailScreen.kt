package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.components.AnimatedOrderTrackingTimeline
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun OrderHistoryScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("order_history_screen")
    ) {
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No Orders Yet", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Your placed orders will appear here for tracking.", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppDestination.HOME) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderHistoryCard(
                        order = order,
                        onClick = { viewModel.selectOrder(order) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.id, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ShopnovaBlue)
                    Text("Placed on ${order.formattedDate}", fontSize = 11.sp, color = TextSecondary)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (order.statusEnum) {
                        OrderStatus.DELIVERED -> ShopnovaGreenLight
                        OrderStatus.RETURN_REQUESTED, OrderStatus.RETURNED, OrderStatus.CANCELLED -> ShopnovaRedLight
                        else -> ShopnovaBlueSoft
                    }
                ) {
                    Text(
                        text = order.orderStatus.replace("_", " "),
                        color = when (order.statusEnum) {
                            OrderStatus.DELIVERED -> ShopnovaGreen
                            OrderStatus.RETURN_REQUESTED, OrderStatus.RETURNED, OrderStatus.CANCELLED -> ShopnovaRed
                            else -> ShopnovaBlue
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = order.itemsSummaryJson,
                fontSize = 13.sp,
                color = TextPrimary,
                maxLines = 2
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total: ${order.formattedTotal}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
                Text("Track & Details →", color = ShopnovaBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OrderDetailScreen(
    order: Order,
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    var showReturnDialog by remember { mutableStateOf(false) }
    var returnReason by remember { mutableStateOf("Damaged or defective item") }

    val returnOptions = listOf(
        "Damaged or defective item",
        "Item did not match description",
        "Size/Fitting issue",
        "Received incorrect item",
        "Product no longer needed"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("order_detail_screen"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Order Header Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Order ID: ${order.id}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = ShopnovaBlue)
                    Text("Placed: ${order.formattedDate} • Payment: ${order.paymentMethod} (${order.paymentStatus})", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        // 2. Animated Tracking Timeline
        item {
            AnimatedOrderTrackingTimeline(order = order)
        }

        // 3. Shipping & Delivery Address Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Spacer(Modifier.height(6.dp))
                    Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(order.addressLine, fontSize = 12.sp, color = TextPrimary)
                    Text("${order.city}, ${order.state} - ${order.pincode}", fontSize = 12.sp, color = TextPrimary)
                    Text("Phone: ${order.customerPhone}", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // 4. Items and Price Summary
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Items in Order", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(order.itemsSummaryJson, fontSize = 13.sp, color = TextPrimary)

                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = DividerColor)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Paid Amount:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(order.formattedTotal, fontWeight = FontWeight.Black, fontSize = 15.sp, color = ShopnovaGreen)
                    }
                }
            }
        }

        // 5. Returns & Actions
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Order Actions & Support", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(10.dp))

                    if (order.statusEnum == OrderStatus.DELIVERED) {
                        Button(
                            onClick = { showReturnDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopnovaRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Request 7-Day Return / Refund")
                        }
                    } else if (order.statusEnum == OrderStatus.RETURN_REQUESTED) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ShopnovaRedLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Return pickup scheduled for tomorrow. Refund will be credited within 24 hours of pickup.",
                                color = ShopnovaRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppDestination.CUSTOMER_SUPPORT) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = ShopnovaBlue)
                        Spacer(Modifier.width(6.dp))
                        Text("Contact 24x7 Customer Support", color = ShopnovaBlue)
                    }
                }
            }
        }
    }

    // 7-Day Return Dialog
    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = { showReturnDialog = false },
            title = { Text("Request 7-Day Return") },
            text = {
                Column {
                    Text("Select reason for return:", fontSize = 13.sp, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    returnOptions.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { returnReason = reason }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = returnReason == reason, onClick = { returnReason = reason })
                            Spacer(Modifier.width(6.dp))
                            Text(reason, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.requestReturn(order.id, returnReason)
                        showReturnDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopnovaRed)
                ) {
                    Text("Confirm Return")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReturnDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
