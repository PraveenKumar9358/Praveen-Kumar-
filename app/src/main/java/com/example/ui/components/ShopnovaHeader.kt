package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopnovaTopHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    cartCount: Int,
    wishlistCount: Int,
    unreadNotificationCount: Int,
    onNavigate: (AppDestination) -> Unit,
    onAdminToggle: () -> Unit,
    isAdminMode: Boolean = false,
    showBackButton: Boolean = false,
    onBack: () -> Unit = {}
) {
    Surface(
        color = SurfaceWhite,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top Row: Brand Logo, Back button, Wishlist, Admin, Notifications, Cart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigate(AppDestination.HOME) }
                ) {
                    if (showBackButton) {
                        Surface(
                            shape = CircleShape,
                            color = SurfaceElevated,
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onBack() }
                                .padding(end = 4.dp)
                                .testTag("nav_back_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(6.dp))
                    }

                    // Shopnova Sleek Brand Logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ShopnovaBlue,
                            shadowElevation = 3.dp,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "SHOP",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "NOVA",
                            color = ShopnovaBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceElevated,
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Text(
                            text = "🇮🇳 IND",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                // Action Icons (Sleek Circular Pills)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Admin Switch Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAdminMode) ShopnovaBlue else SurfaceElevated,
                        modifier = Modifier
                            .clickable { onAdminToggle() }
                            .testTag("admin_toggle_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = if (isAdminMode) Color.White else TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isAdminMode) "ADMIN" else "Admin",
                                color = if (isAdminMode) Color.White else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Wishlist Icon (Sleek Circle)
                    Surface(
                        shape = CircleShape,
                        color = SurfaceElevated,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onNavigate(AppDestination.WISHLIST) }
                            .testTag("header_wishlist_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            BadgedBox(
                                badge = {
                                    if (wishlistCount > 0) {
                                        Badge(
                                            containerColor = ShopnovaBlue,
                                            contentColor = Color.White
                                        ) {
                                            Text(wishlistCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Cart Icon (Sleek Circle)
                    Surface(
                        shape = CircleShape,
                        color = SurfaceElevated,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onNavigate(AppDestination.CART) }
                            .testTag("header_cart_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(
                                            containerColor = ShopnovaBlue,
                                            contentColor = Color.White
                                        ) {
                                            Text(cartCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Sleek Search Bar Input (rounded-2xl)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceElevated,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AppDestination.SEARCH_CATALOG) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 11.dp)
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search Mobiles, Fashion, Electronics...",
                                color = TextMuted,
                                fontSize = 13.5.sp,
                                maxLines = 1
                            )
                        } else {
                            Text(
                                text = searchQuery,
                                color = TextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onSearchChange("") }
                        )
                    } else {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Voice Search",
                            tint = ShopnovaBlue,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}
