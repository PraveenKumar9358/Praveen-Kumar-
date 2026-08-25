package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.OrderStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    val totalRevenue = allOrders.sumOf { it.totalAmount }
    val lowStockCount = allProducts.count { it.stock < 10 }
    val activeOrdersCount = allOrders.count { it.statusEnum != OrderStatus.DELIVERED && it.statusEnum != OrderStatus.CANCELLED && it.statusEnum != OrderStatus.RETURNED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Admin Header
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ShopnovaBlueDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Shopnova Admin Portal", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("Store Management & 10,000 Catalog Engine", color = ShopnovaGold, fontSize = 12.sp)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = ShopnovaGold) {
                            Text("ADMIN", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminMetricCard(
                        title = "Catalog Products",
                        value = "%,d".format(allProducts.size),
                        icon = Icons.Default.Inventory2,
                        color = ShopnovaBlue,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.navigateTo(AppDestination.ADMIN_PRODUCTS) }

                    AdminMetricCard(
                        title = "Total Revenue",
                        value = "₹%,d".format(totalRevenue.toLong()),
                        icon = Icons.Default.CurrencyRupee,
                        color = ShopnovaGreen,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.navigateTo(AppDestination.ADMIN_ORDERS) }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminMetricCard(
                        title = "Total Orders",
                        value = "%,d".format(allOrders.size),
                        icon = Icons.Default.ShoppingBag,
                        color = ShopnovaBlueDark,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.navigateTo(AppDestination.ADMIN_ORDERS) }

                    AdminMetricCard(
                        title = "Low Stock Alerts",
                        value = "$lowStockCount Items",
                        icon = Icons.Default.Warning,
                        color = if (lowStockCount > 0) ShopnovaRed else ShopnovaGreen,
                        modifier = Modifier.weight(1f)
                    ) { viewModel.navigateTo(AppDestination.ADMIN_INVENTORY) }
                }
            }
        }

        // Quick Admin Actions
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Management Modules", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    AdminActionRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "10,000 Products Generator Engine",
                        subtitle = "Generate & seed 100 to 10,000 realistic Indian products with 1-click",
                        badge = "10,000 Engine"
                    ) { viewModel.navigateTo(AppDestination.ADMIN_GENERATOR) }

                    AdminActionRow(
                        icon = Icons.Default.Psychology,
                        title = "Add Product via Gemini AI",
                        subtitle = "AI auto-generates Title, Specs, Description & MRP from your prompt",
                        badge = "Gemini AI"
                    ) { viewModel.startEditProduct(null) }

                    AdminActionRow(
                        icon = Icons.Default.LocalShipping,
                        title = "Order Fulfillment & Status",
                        subtitle = "Process shipping, update BlueDart tracking, approve returns",
                        badge = "$activeOrdersCount Active"
                    ) { viewModel.navigateTo(AppDestination.ADMIN_ORDERS) }

                    AdminActionRow(
                        icon = Icons.Default.Warehouse,
                        title = "Inventory & Stock Replenishment",
                        subtitle = "Quick bulk refill stock counters and monitor inventory",
                        badge = "Inventory"
                    ) { viewModel.navigateTo(AppDestination.ADMIN_INVENTORY) }
                }
            }
        }

        // Recent Orders List Preview
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Customer Orders", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Manage All →",
                            color = ShopnovaBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.ADMIN_ORDERS) }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    if (allOrders.isEmpty()) {
                        Text("No orders placed yet.", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        allOrders.take(4).forEach { ord ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectOrder(ord) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(ord.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ShopnovaBlue)
                                    Text("${ord.customerName} • ${ord.formattedTotal}", fontSize = 11.sp, color = TextSecondary)
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ShopnovaBlueSoft
                                ) {
                                    Text(ord.orderStatus.replace("_", " "), color = ShopnovaBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            HorizontalDivider(color = DividerColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 17.sp, color = TextPrimary)
            Text(title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AdminActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, badge: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(ShopnovaBlueSoft)
        ) {
            Icon(icon, contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Spacer(Modifier.width(6.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = ShopnovaGold.copy(alpha = 0.2f)) {
                    Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ShopnovaBlueDark, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                }
            }
            Text(subtitle, fontSize = 11.sp, color = TextSecondary, lineHeight = 14.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
    HorizontalDivider(color = DividerColor)
}
