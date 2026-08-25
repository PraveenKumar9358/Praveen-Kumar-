package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HeroBannerCarousel(
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentBannerIndex by remember { mutableStateOf(0) }
    val bannerCount = 3

    // Auto-scroll loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentBannerIndex = (currentBannerIndex + 1) % bannerCount
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ShopnovaBlueDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { onBannerClick("banner_$currentBannerIndex") }
                .testTag("hero_banner_carousel")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentBannerIndex) {
                    0 -> {
                        // Banner 1: Sleek Hero Festive Banner
                        Image(
                            painter = painterResource(id = R.drawable.shopnova_hero_banner_1787656398499),
                            contentDescription = "Mega Festive Sale",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay for Sleek Contrast
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF0F172A).copy(alpha = 0.88f),
                                            Color(0xFF1E293B).copy(alpha = 0.5f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "LIMITED PERIOD OFFER",
                                color = Color(0xFF93C5FD),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "FESTIVE SALE\nUP TO 80% OFF",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 24.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                shadowElevation = 4.dp,
                                modifier = Modifier.clickable { onBannerClick("deals") }
                            ) {
                                Text(
                                    text = "SHOP NOW",
                                    color = ShopnovaBlueDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    1 -> {
                        // Banner 2: Flagship Electronics
                        Image(
                            painter = painterResource(id = R.drawable.shopnova_electronics_banner_1787656410456),
                            contentDescription = "Flagship Smartphones",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1E1B4B).copy(alpha = 0.9f),
                                            Color(0xFF312E81).copy(alpha = 0.45f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(18.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ShopnovaGreen,
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Text(
                                    text = "⚡ 5G FLAGSHIP DEALS",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Next-Gen Audio & Phones\nFrom ₹999",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 23.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ShopnovaBlue,
                                modifier = Modifier.clickable { onBannerClick("electronics") }
                            ) {
                                Text(
                                    text = "Explore Tech →",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        // Banner 3: Fashion & Lifestyle Special
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF4C1D95),
                                            Color(0xFF6D28D9),
                                            Color(0xFF2563EB)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                                Text(
                                    text = "TRENDING COLLECTIONS",
                                    color = Color(0xFFDDD6FE),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Fashion Supernova\nMin 50% - 70% Off",
                                    color = Color.White,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 23.sp
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Extra 10% Instant Discount on UPI",
                                    color = ShopnovaGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Sleek Page indicator dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(bannerCount) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (index == currentBannerIndex) 18.dp else 6.dp, 5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (index == currentBannerIndex) Color.White else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryQuickStrip(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        CategoryMeta("All", Icons.Default.AllInclusive, Color(0xFFEFF6FF), Color(0xFFBFDBFE), ShopnovaBlue),
        CategoryMeta("Electronics", Icons.Default.Devices, Color(0xFFFFF7ED), Color(0xFFFED7AA), Color(0xFFEA580C)),
        CategoryMeta("Fashion", Icons.Default.Checkroom, Color(0xFFECFDF5), Color(0xFFA7F3D0), Color(0xFF059669)),
        CategoryMeta("Home & Kitchen", Icons.Default.Kitchen, Color(0xFFFAF5FF), Color(0xFFE9D5FF), Color(0xFF7C3AED)),
        CategoryMeta("Beauty", Icons.Default.Spa, Color(0xFFFFF1F2), Color(0xFFFECDD3), Color(0xFFE11D48)),
        CategoryMeta("Grocery", Icons.Default.LocalGroceryStore, Color(0xFFF0FDF4), Color(0xFFBBF7D0), Color(0xFF16A34A)),
        CategoryMeta("Sports", Icons.Default.FitnessCenter, Color(0xFFF0FDFA), Color(0xFF99F6E4), Color(0xFF0D9488)),
        CategoryMeta("Books", Icons.Default.MenuBook, Color(0xFFFEF2F2), Color(0xFFFECACA), Color(0xFFDC2626)),
        CategoryMeta("Toys", Icons.Default.SmartToy, Color(0xFFFFFBEB), Color(0xFFFDE68A), Color(0xFFD97706))
    )

    Surface(
        color = SurfaceWhite,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { item ->
                val isSelected = selectedCategory.equals(item.name, ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectCategory(item.name) }
                        .testTag("cat_pill_${item.name}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) ShopnovaBlue else item.bgColor)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ShopnovaBlueDark else item.borderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = if (isSelected) Color.White else item.accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = item.name,
                        fontSize = 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) ShopnovaBlue else TextSecondary,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

data class CategoryMeta(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val bgColor: Color,
    val borderColor: Color,
    val accentColor: Color
)
