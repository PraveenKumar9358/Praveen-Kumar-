package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.Product
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@Composable
fun HomeScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val trendingProducts by viewModel.trendingProducts.collectAsState()
    val dealOfDayProducts by viewModel.dealOfDayProducts.collectAsState()
    val flashSaleProducts by viewModel.flashSaleProducts.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Category Quick Strip
        item {
            CategoryQuickStrip(
                selectedCategory = filterState.category,
                onSelectCategory = { cat ->
                    viewModel.setCategoryFilter(cat)
                    if (cat != "All") {
                        viewModel.navigateTo(AppDestination.SEARCH_CATALOG)
                    }
                }
            )
        }

        // 2. Hero Promotional Carousel
        item {
            HeroBannerCarousel(
                onBannerClick = {
                    viewModel.navigateTo(AppDestination.SEARCH_CATALOG)
                },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            )
        }

        // 3. Flash Deals with Countdown Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ShopnovaGold
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text("DEALS OF THE DAY", fontWeight = FontWeight.Black, fontSize = 11.5.sp, color = Color.Black, letterSpacing = 0.3.sp)
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ShopnovaRedLight
                            ) {
                                Text(
                                    text = "⏳ 04h : 18m",
                                    color = ShopnovaRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = "View All →",
                            color = ShopnovaBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.SEARCH_CATALOG) }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Horizontal scrolling deals
                    val dealsToShow = if (dealOfDayProducts.isNotEmpty()) dealOfDayProducts else allProducts.take(8)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        dealsToShow.forEach { product ->
                            Box(modifier = Modifier.width(162.dp)) {
                                ProductGridCard(
                                    product = product,
                                    isWishlisted = viewModel.isProductWishlisted(product.id),
                                    onProductClick = { viewModel.selectProduct(it) },
                                    onToggleWishlist = { viewModel.toggleWishlist(it) },
                                    onAddToCart = { viewModel.addToCart(it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Shopnova Assured Quality Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ShopnovaBlueSoft)
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = null,
                            tint = ShopnovaBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Shopnova Assured Guarantee",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "100% Original Products • 6-Step Quality Check • 7-Day Free Returns",
                            fontSize = 10.5.sp,
                            color = TextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // 5. Trending in India Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Text(
                        text = "🔥 Trending in India",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "See All",
                        color = ShopnovaBlue,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.navigateTo(AppDestination.SEARCH_CATALOG) }
                    )
                }

                // Trending 2-row grid
                val trendingList = if (trendingProducts.isNotEmpty()) trendingProducts else allProducts.drop(4).take(6)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    trendingList.chunked(2).forEach { rowProducts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowProducts.forEach { product ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductGridCard(
                                        product = product,
                                        isWishlisted = viewModel.isProductWishlisted(product.id),
                                        onProductClick = { viewModel.selectProduct(it) },
                                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                                        onAddToCart = { viewModel.addToCart(it) }
                                    )
                                }
                            }
                            if (rowProducts.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // 6. Indian Customer Trust & Benefits Strip
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Why Shop with Shopnova?",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BenefitItem(icon = Icons.Default.Replay, label = "7-Day Return")
                        BenefitItem(icon = Icons.Default.LocalShipping, label = "Free Express")
                        BenefitItem(icon = Icons.Default.Payments, label = "Cash on Delivery")
                        BenefitItem(icon = Icons.Default.Security, label = "Secure UPI/Card")
                    }
                }
            }
        }
    }
}

@Composable
fun BenefitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
        ) {
            Icon(icon, contentDescription = label, tint = ShopnovaBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
    }
}
