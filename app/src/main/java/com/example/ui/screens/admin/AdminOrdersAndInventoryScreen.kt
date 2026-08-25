package com.example.ui.screens.admin

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
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun AdminOrdersScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()
    var selectedOrderForStatusUpdate by remember { mutableStateOf<Order?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .padding(10.dp)
            .testTag("admin_orders_screen")
    ) {
        Text("Customer Order Fulfillment (${allOrders.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

        if (allOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders placed yet.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allOrders, key = { it.id }) { order ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(order.id, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ShopnovaBlue)
                                    Text("Customer: ${order.customerName} (${order.customerPhone})", fontSize = 12.sp, color = TextPrimary)
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ShopnovaBlueSoft
                                ) {
                                    Text(
                                        order.orderStatus.replace("_", " "),
                                        color = ShopnovaBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(6.dp))
                            Text("Address: ${order.addressLine}, ${order.city} - ${order.pincode}", fontSize = 11.5.sp, color = TextSecondary)
                            Text("Items: ${order.itemsSummaryJson}", fontSize = 12.sp, color = TextPrimary)
                            Text("Payment: ${order.paymentMethod} • Total: ${order.formattedTotal}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ShopnovaGreen)

                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = DividerColor)
                            Spacer(Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { selectedOrderForStatusUpdate = order },
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Update Status", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Status Update Dialog
    if (selectedOrderForStatusUpdate != null) {
        val ord = selectedOrderForStatusUpdate!!
        AlertDialog(
            onDismissRequest = { selectedOrderForStatusUpdate = null },
            title = { Text("Update Order Status: ${ord.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    OrderStatus.values().forEach { status ->
                        val isCurrent = ord.orderStatus == status.name
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCurrent) ShopnovaBlueSoft else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateOrderStatusAdmin(ord.id, status.name)
                                    selectedOrderForStatusUpdate = null
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(status.name.replace("_", " "), fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
                                if (isCurrent) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedOrderForStatusUpdate = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminInventoryScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .padding(10.dp)
            .testTag("admin_inventory_screen")
    ) {
        Text("Inventory & Stock Refill", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(allProducts, key = { it.id }) { product ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                            Text("SKU: ${product.sku} • ${product.category}", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                "Current Stock: ${product.stock} units",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (product.stock < 10) ShopnovaRed else ShopnovaGreen
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalButton(
                                onClick = { viewModel.updateStockAdmin(product.id, product.stock + 10) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("+10", fontSize = 11.sp)
                            }
                            FilledTonalButton(
                                onClick = { viewModel.updateStockAdmin(product.id, product.stock + 50) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("+50", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminGeneratorScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val isSeeding by viewModel.isSeeding.collectAsState()
    val seedingMessage by viewModel.seedingMessage.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .padding(12.dp)
            .testTag("admin_generator_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Engine Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ShopnovaBlueDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ShopnovaGold, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("10,000 Products Catalog Engine", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Deterministic generator creating realistic Indian marketplace catalog items across 8 categories with accurate MRPs, selling prices, specifications, and warranty badges.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(6.dp), color = ShopnovaGold) {
                        Text(
                            text = "Current Database Products: %,d".format(allProducts.size),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Generator Actions
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Batch Generation Size", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    if (isSeeding) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = ShopnovaBlue)
                            Spacer(Modifier.height(10.dp))
                            Text(seedingMessage ?: "Generating catalog products...", fontSize = 12.sp, color = TextPrimary)
                        }
                    } else {
                        GeneratorButton(count = 100, label = "+100 Quick Demo Products", desc = "Instant (~1 sec)") { viewModel.seedBulkProducts(100) }
                        Spacer(Modifier.height(8.dp))
                        GeneratorButton(count = 500, label = "+500 Curated Catalog Items", desc = "Multi-category mix (~2 sec)") { viewModel.seedBulkProducts(500) }
                        Spacer(Modifier.height(8.dp))
                        GeneratorButton(count = 1000, label = "+1,000 Complete Marketplace", desc = "Comprehensive depth (~3 sec)") { viewModel.seedBulkProducts(1000) }
                        Spacer(Modifier.height(8.dp))
                        GeneratorButton(count = 5000, label = "+5,000 Mega Store Catalog", desc = "All subcategories (~6 sec)") { viewModel.seedBulkProducts(5000) }
                        Spacer(Modifier.height(8.dp))
                        GeneratorButton(count = 10000, label = "⚡ FULL 10,000 MASTER CATALOG", desc = "10,000 Unique Indian Products (~10 sec)", isGold = true) { viewModel.seedBulkProducts(10000) }
                    }

                    if (!isSeeding && seedingMessage != null) {
                        Spacer(Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = ShopnovaGreenLight, modifier = Modifier.fillMaxWidth()) {
                            Text(seedingMessage ?: "", color = ShopnovaGreen, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratorButton(count: Int, label: String, desc: String, isGold: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isGold) ShopnovaGold else ShopnovaBlue
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = if (isGold) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(desc, color = if (isGold) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        }
    }
}
