package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ShopnovaTopHeader
import com.example.ui.screens.*
import com.example.ui.screens.admin.*
import com.example.ui.theme.MarketplaceBackground
import com.example.ui.theme.ShopnovaBlue
import com.example.ui.theme.ShopnovaGold
import com.example.ui.theme.ShopnovaTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ShopnovaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopnovaTheme {
                ShopnovaMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ShopnovaMainApp(viewModel: ShopnovaViewModel) {
    val destination by viewModel.currentDestination.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val selectedOrder by viewModel.selectedOrder.collectAsState()
    val editingProduct by viewModel.editingProduct.collectAsState()

    val totalCartCount = cartItems.sumOf { it.quantity }
    val unreadNotifications = notifications.count { !it.isRead }

    val isTopLevelScreen = destination == AppDestination.HOME ||
            destination == AppDestination.SEARCH_CATALOG ||
            destination == AppDestination.WISHLIST ||
            destination == AppDestination.CART ||
            destination == AppDestination.PROFILE

    val isAdminScreen = destination == AppDestination.ADMIN_DASHBOARD ||
            destination == AppDestination.ADMIN_PRODUCTS ||
            destination == AppDestination.ADMIN_PRODUCT_EDIT ||
            destination == AppDestination.ADMIN_ORDERS ||
            destination == AppDestination.ADMIN_INVENTORY ||
            destination == AppDestination.ADMIN_COUPONS ||
            destination == AppDestination.ADMIN_GENERATOR

    // Handle System Back Press
    BackHandler(enabled = destination != AppDestination.HOME) {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            ShopnovaTopHeader(
                searchQuery = searchQuery,
                onSearchChange = { q ->
                    viewModel.setSearchQuery(q)
                    if (destination != AppDestination.SEARCH_CATALOG && q.isNotBlank()) {
                        viewModel.navigateTo(AppDestination.SEARCH_CATALOG)
                    }
                },
                onSearchSubmit = {
                    viewModel.navigateTo(AppDestination.SEARCH_CATALOG)
                },
                cartCount = totalCartCount,
                wishlistCount = wishlistItems.size,
                unreadNotificationCount = unreadNotifications,
                onNavigate = { dest -> viewModel.navigateTo(dest) },
                onAdminToggle = {
                    if (isAdminScreen) {
                        viewModel.navigateTo(AppDestination.HOME)
                    } else {
                        viewModel.navigateTo(AppDestination.ADMIN_DASHBOARD)
                    }
                },
                isAdminMode = isAdminScreen,
                showBackButton = !isTopLevelScreen,
                onBack = { viewModel.navigateBack() }
            )
        },
        bottomBar = {
            if (!isAdminScreen) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 2.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        icon = { Icon(if (destination == AppDestination.HOME) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = if (destination == AppDestination.HOME) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium) },
                        selected = destination == AppDestination.HOME,
                        onClick = { viewModel.navigateTo(AppDestination.HOME) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShopnovaBlue,
                            selectedTextColor = ShopnovaBlue,
                            unselectedIconColor = com.example.ui.theme.TextSecondary,
                            unselectedTextColor = com.example.ui.theme.TextSecondary,
                            indicatorColor = com.example.ui.theme.ShopnovaBlueSoft
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        icon = { Icon(if (destination == AppDestination.SEARCH_CATALOG) Icons.Filled.Category else Icons.Outlined.Category, contentDescription = "Categories") },
                        label = { Text("Categories", fontSize = 11.sp, fontWeight = if (destination == AppDestination.SEARCH_CATALOG) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium) },
                        selected = destination == AppDestination.SEARCH_CATALOG,
                        onClick = { viewModel.navigateTo(AppDestination.SEARCH_CATALOG) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShopnovaBlue,
                            selectedTextColor = ShopnovaBlue,
                            unselectedIconColor = com.example.ui.theme.TextSecondary,
                            unselectedTextColor = com.example.ui.theme.TextSecondary,
                            indicatorColor = com.example.ui.theme.ShopnovaBlueSoft
                        ),
                        modifier = Modifier.testTag("nav_item_catalog")
                    )

                    NavigationBarItem(
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (wishlistItems.isNotEmpty()) {
                                        Badge(containerColor = ShopnovaBlue, contentColor = Color.White) {
                                            Text(wishlistItems.size.toString(), fontSize = 9.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(if (destination == AppDestination.WISHLIST) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "Wishlist")
                            }
                        },
                        label = { Text("Wishlist", fontSize = 11.sp, fontWeight = if (destination == AppDestination.WISHLIST) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium) },
                        selected = destination == AppDestination.WISHLIST,
                        onClick = { viewModel.navigateTo(AppDestination.WISHLIST) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShopnovaBlue,
                            selectedTextColor = ShopnovaBlue,
                            unselectedIconColor = com.example.ui.theme.TextSecondary,
                            unselectedTextColor = com.example.ui.theme.TextSecondary,
                            indicatorColor = com.example.ui.theme.ShopnovaBlueSoft
                        ),
                        modifier = Modifier.testTag("nav_item_wishlist")
                    )

                    NavigationBarItem(
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge(containerColor = ShopnovaBlue, contentColor = Color.White) {
                                            Text(totalCartCount.toString(), fontSize = 9.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(if (destination == AppDestination.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = { Text("Cart", fontSize = 11.sp, fontWeight = if (destination == AppDestination.CART) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium) },
                        selected = destination == AppDestination.CART,
                        onClick = { viewModel.navigateTo(AppDestination.CART) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShopnovaBlue,
                            selectedTextColor = ShopnovaBlue,
                            unselectedIconColor = com.example.ui.theme.TextSecondary,
                            unselectedTextColor = com.example.ui.theme.TextSecondary,
                            indicatorColor = com.example.ui.theme.ShopnovaBlueSoft
                        ),
                        modifier = Modifier.testTag("nav_item_cart")
                    )

                    NavigationBarItem(
                        icon = { Icon(if (destination == AppDestination.PROFILE) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Account") },
                        label = { Text("Account", fontSize = 11.sp, fontWeight = if (destination == AppDestination.PROFILE) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium) },
                        selected = destination == AppDestination.PROFILE,
                        onClick = { viewModel.navigateTo(AppDestination.PROFILE) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ShopnovaBlue,
                            selectedTextColor = ShopnovaBlue,
                            unselectedIconColor = com.example.ui.theme.TextSecondary,
                            unselectedTextColor = com.example.ui.theme.TextSecondary,
                            indicatorColor = com.example.ui.theme.ShopnovaBlueSoft
                        ),
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MarketplaceBackground)
        ) {
            when (destination) {
                AppDestination.HOME -> HomeScreen(viewModel = viewModel)
                AppDestination.SEARCH_CATALOG -> SearchCatalogScreen(viewModel = viewModel)
                AppDestination.PRODUCT_DETAIL -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(product = product, viewModel = viewModel)
                    } ?: HomeScreen(viewModel = viewModel)
                }
                AppDestination.CART -> CartScreen(viewModel = viewModel)
                AppDestination.WISHLIST -> WishlistScreen(viewModel = viewModel)
                AppDestination.CHECKOUT -> CheckoutScreen(viewModel = viewModel)
                AppDestination.ORDER_SUCCESS -> OrderSuccessScreen(viewModel = viewModel)
                AppDestination.ORDER_HISTORY -> OrderHistoryScreen(viewModel = viewModel)
                AppDestination.ORDER_DETAIL -> {
                    selectedOrder?.let { order ->
                        OrderDetailScreen(order = order, viewModel = viewModel)
                    } ?: OrderHistoryScreen(viewModel = viewModel)
                }
                AppDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
                AppDestination.CUSTOMER_SUPPORT -> CustomerSupportScreen(viewModel = viewModel)
                AppDestination.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                AppDestination.ADMIN_PRODUCTS -> AdminProductsScreen(viewModel = viewModel)
                AppDestination.ADMIN_PRODUCT_EDIT -> AdminProductEditScreen(productToEdit = editingProduct, viewModel = viewModel)
                AppDestination.ADMIN_ORDERS -> AdminOrdersScreen(viewModel = viewModel)
                AppDestination.ADMIN_INVENTORY -> AdminInventoryScreen(viewModel = viewModel)
                AppDestination.ADMIN_COUPONS -> AdminDashboardScreen(viewModel = viewModel)
                AppDestination.ADMIN_GENERATOR -> AdminGeneratorScreen(viewModel = viewModel)
            }
        }
    }
}
