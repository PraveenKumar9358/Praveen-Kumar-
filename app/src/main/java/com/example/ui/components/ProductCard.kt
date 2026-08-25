package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.theme.*

@Composable
fun ProductGridCard(
    product: Product,
    isWishlisted: Boolean,
    onProductClick: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = tween(120),
        label = "card_scale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp, hoveredElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onProductClick(product) }
                )
            }
            .testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            // Product Visual / Category Icon Presentation with Sleek Slate Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceElevated)
            ) {
                // Category Visual Representation
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = getCategoryIcon(product.category, product.subcategory),
                        contentDescription = product.title,
                        tint = getCategoryColor(product.category),
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = product.brand.uppercase(),
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 0.8.sp
                    )
                }

                // Discount Badge (Top Left)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ShopnovaGreen,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${product.discountPercent}% OFF",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                // Wishlist Toggle (Top Right)
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clickable { onToggleWishlist(product) }
                        .testTag("wishlist_btn_${product.id}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) ShopnovaRed else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Assured Badge (Bottom Left)
                if (product.isAssured) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ShopnovaBlueDark,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = ShopnovaGold, modifier = Modifier.size(10.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("Assured", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 2.dp, end = 2.dp, bottom = 2.dp)
            ) {
                // Title
                Text(
                    text = product.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(Modifier.height(4.dp))

                // Rating Pill & Review count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (product.rating >= 4.0f) ShopnovaGreen else ShopnovaGold,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "%.1f".format(product.rating),
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(1.dp))
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                    Text(
                        text = "(%,d)".format(product.ratingCount),
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(Modifier.height(5.dp))

                // Price Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.formattedPrice,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Black,
                        color = ShopnovaBlue
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = product.formattedMrp,
                        fontSize = 10.5.sp,
                        color = TextMuted,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Delivery info & Add CTA
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (product.price > 499) "Free Delivery" else "+₹40 Delivery",
                        color = if (product.price > 499) ShopnovaGreen else TextMuted,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ShopnovaBlue,
                        modifier = Modifier
                            .clickable { onAddToCart(product) }
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("ADD", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Electronics" -> Color(0xFF1976D2)
        "Fashion" -> Color(0xFFE91E63)
        "Home & Kitchen" -> Color(0xFFFB8C00)
        "Beauty" -> Color(0xFF9C27B0)
        "Grocery" -> Color(0xFF43A047)
        "Sports" -> Color(0xFF00ACC1)
        "Books" -> Color(0xFF8D6E63)
        "Toys" -> Color(0xFFFF5722)
        else -> ShopnovaBlue
    }
}

fun getCategoryIcon(category: String, subcategory: String = ""): androidx.compose.ui.graphics.vector.ImageVector {
    return when {
        subcategory.contains("Phone", true) || subcategory.contains("Smartphone", true) -> Icons.Default.Smartphone
        subcategory.contains("Laptop", true) -> Icons.Default.Laptop
        subcategory.contains("Earbud", true) || subcategory.contains("Audio", true) -> Icons.Default.Headphones
        subcategory.contains("Watch", true) -> Icons.Default.Watch
        subcategory.contains("Camera", true) -> Icons.Default.PhotoCamera
        category == "Electronics" -> Icons.Default.Devices
        category == "Fashion" -> Icons.Default.Checkroom
        category == "Home & Kitchen" -> Icons.Default.Kitchen
        category == "Beauty" -> Icons.Default.Spa
        category == "Grocery" -> Icons.Default.LocalGroceryStore
        category == "Sports" -> Icons.Default.FitnessCenter
        category == "Books" -> Icons.Default.MenuBook
        category == "Toys" -> Icons.Default.SmartToy
        else -> Icons.Default.ShoppingBag
    }
}
