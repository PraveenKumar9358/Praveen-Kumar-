package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun ProfileScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    val wishlist by viewModel.wishlistItems.collectAsState()
    val addresses by viewModel.addresses.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // User Info Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ShopnovaBlueDark),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ShopnovaGold)
                    ) {
                        Text("PK", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Praveen Kumar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("+91 98765 43210 • praveen@shopnova.in", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ShopnovaGold
                        ) {
                            Text("Shopnova VIP Member", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(title = "Orders", count = orders.size.toString(), icon = Icons.Default.ReceiptLong, modifier = Modifier.weight(1f)) {
                    viewModel.navigateTo(AppDestination.ORDER_HISTORY)
                }
                QuickStatCard(title = "Wishlist", count = wishlist.size.toString(), icon = Icons.Default.Favorite, modifier = Modifier.weight(1f)) {
                    viewModel.navigateTo(AppDestination.WISHLIST)
                }
                QuickStatCard(title = "Addresses", count = addresses.size.toString(), icon = Icons.Default.LocationOn, modifier = Modifier.weight(1f)) {
                    viewModel.navigateTo(AppDestination.CHECKOUT)
                }
                QuickStatCard(title = "Coupons", count = "4", icon = Icons.Default.LocalOffer, modifier = Modifier.weight(1f)) {
                    viewModel.navigateTo(AppDestination.CART)
                }
            }
        }

        // Account Menu List
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    AccountMenuItem(icon = Icons.Default.ShoppingBag, title = "My Orders & Returns", subtitle = "Track, cancel, or return orders") {
                        viewModel.navigateTo(AppDestination.ORDER_HISTORY)
                    }
                    AccountMenuItem(icon = Icons.Default.FavoriteBorder, title = "Wishlist & Saved Items", subtitle = "View products you saved") {
                        viewModel.navigateTo(AppDestination.WISHLIST)
                    }
                    AccountMenuItem(icon = Icons.Default.HeadsetMic, title = "24x7 Customer Support", subtitle = "FAQs, Chat, and Ticket Resolution") {
                        viewModel.navigateTo(AppDestination.CUSTOMER_SUPPORT)
                    }
                    AccountMenuItem(icon = Icons.Default.AdminPanelSettings, title = "Admin & Catalog CMS", subtitle = "Manage products, inventory, orders & 10,000 bulk generator") {
                        viewModel.navigateTo(AppDestination.ADMIN_DASHBOARD)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatCard(title: String, count: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = ShopnovaBlue, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(2.dp))
            Text(count, fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
            Text(title, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AccountMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun CustomerSupportScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    var ticketSubject by remember { mutableStateOf("") }
    var ticketMessage by remember { mutableStateOf("") }
    var ticketSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("support_screen"),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ShopnovaBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Shopnova 24x7 Customer Support", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("We are here to help with your orders, payments, refunds, and delivery inquiries.", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                }
            }
        }

        // FAQs
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Frequently Asked Questions", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    FaqItem(q = "How do I track my delivery?", a = "Go to My Orders and click on any order to see real-time 6-stage BlueDart/Ekart tracking.")
                    FaqItem(q = "What is the 7-Day Return Policy?", a = "You can request a free pickup return on delivered items within 7 days. Refunds are credited to UPI/Bank instantly upon pickup.")
                    FaqItem(q = "What payment methods are supported?", a = "We accept UPI (GPay, PhonePe, Paytm), Credit/Debit Cards, Net Banking, and Cash on Delivery.")
                    FaqItem(q = "What does Shopnova Assured mean?", a = "Shopnova Assured products undergo a 6-step quality verification test and guarantee genuine brand warranty.")
                }
            }
        }

        // Raise a Ticket
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Raise a Support Ticket", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    if (ticketSubmitted) {
                        Surface(shape = RoundedCornerShape(8.dp), color = ShopnovaGreenLight, modifier = Modifier.fillMaxWidth()) {
                            Text("✓ Your ticket has been created! Our support executive will contact you within 2 hours.", color = ShopnovaGreen, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                        }
                    } else {
                        OutlinedTextField(
                            value = ticketSubject,
                            onValueChange = { ticketSubject = it },
                            label = { Text("Issue Subject (e.g., Refund status, Delivery delay)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = ticketMessage,
                            onValueChange = { ticketMessage = it },
                            label = { Text("Explain your issue in detail...") },
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (ticketSubject.isNotBlank() && ticketMessage.isNotBlank()) {
                                    ticketSubmitted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Support Request")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FaqItem(q: String, a: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(q, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.weight(1f))
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = TextSecondary)
        }
        if (expanded) {
            Spacer(Modifier.height(4.dp))
            Text(a, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
        }
        HorizontalDivider(modifier = Modifier.padding(top = 6.dp), color = DividerColor)
    }
}
