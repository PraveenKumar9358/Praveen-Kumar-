package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductGridCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun WishlistScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val wishlistProducts by viewModel.wishlistWithProducts.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("wishlist_screen")
    ) {
        if (wishlistProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        Icons.Default.HeartBroken,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Your Wishlist is Empty",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Save items you love by tapping the heart icon on any product!",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppDestination.HOME) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue)
                    ) {
                        Text("Explore Deals")
                    }
                }
            }
        } else {
            Text(
                text = "My Wishlist (${wishlistProducts.size} Items)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wishlistProducts, key = { it.id }) { product ->
                    ProductGridCard(
                        product = product,
                        isWishlisted = true,
                        onProductClick = { viewModel.selectProduct(it) },
                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                        onAddToCart = { viewModel.addToCart(it) }
                    )
                }
            }
        }
    }
}
