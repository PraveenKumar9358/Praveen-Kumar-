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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItemWithProduct
import com.example.ui.components.getCategoryColor
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponError by viewModel.couponError.collectAsState()

    var couponInput by remember { mutableStateOf("") }

    val totalMrp = cartWithProducts.sumOf { it.product.mrp * it.cartItem.quantity }
    val subtotal = cartWithProducts.sumOf { it.product.price * it.cartItem.quantity }
    val productDiscount = totalMrp - subtotal

    var couponDiscount = 0.0
    appliedCoupon?.let { cp ->
        couponDiscount = ((subtotal * cp.discountPercent) / 100.0).coerceAtMost(cp.maxDiscount)
    }

    val deliveryFee = if (subtotal > 499 || subtotal == 0.0) 0.0 else 40.0
    val finalTotal = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("cart_screen")
    ) {
        if (cartWithProducts.isEmpty()) {
            // Empty Cart State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.RemoveShoppingCart,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Your Shopnova Cart is Empty",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Explore thousands of deals and add your favorite items!",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.navigateTo(AppDestination.HOME) },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue)
                ) {
                    Text("Shop Now")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Delivery address info banner
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ShopnovaBlueSoft)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Deliver to Praveen Kumar, Bengaluru - 560034", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Express Delivery in 1-2 Days", fontSize = 11.sp, color = ShopnovaGreen, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // Cart Items List
                items(cartWithProducts, key = { it.cartItem.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { viewModel.updateCartQuantity(item.cartItem.id, item.cartItem.quantity + 1) },
                        onDecrease = { viewModel.updateCartQuantity(item.cartItem.id, item.cartItem.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.cartItem.id) }
                    )
                }

                // Apply Coupon Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(1.5.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = ShopnovaGreen, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Apply Coupon", fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(10.dp))

                            if (appliedCoupon != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(ShopnovaGreenLight, RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text("✓ Code Applied: ${appliedCoupon?.code}", fontWeight = FontWeight.Bold, color = ShopnovaGreen, fontSize = 13.sp)
                                        Text("You saved ₹%,d with this coupon".format(couponDiscount.toLong()), fontSize = 11.sp, color = ShopnovaGreen)
                                    }
                                    Text(
                                        text = "REMOVE",
                                        color = ShopnovaRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable { viewModel.removeCoupon() }
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it.uppercase() },
                                        label = { Text("Coupon Code", fontSize = 12.sp) },
                                        placeholder = { Text("e.g. WELCOME100", fontSize = 12.sp) },
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.applyCoupon(couponInput) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("APPLY", fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (couponError != null) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(couponError ?: "", color = ShopnovaRed, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Price Details Breakdown Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(1.5.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("PRICE DETAILS", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            Spacer(Modifier.height(10.dp))

                            PriceRow(label = "Price (${cartWithProducts.sumOf { it.cartItem.quantity }} items)", value = "₹%,d".format(totalMrp.toLong()))
                            PriceRow(label = "Discount", value = "- ₹%,d".format(productDiscount.toLong()), isGreen = true)

                            if (appliedCoupon != null) {
                                PriceRow(label = "Coupon Savings", value = "- ₹%,d".format(couponDiscount.toLong()), isGreen = true)
                            }

                            PriceRow(
                                label = "Delivery Charges",
                                value = if (deliveryFee == 0.0) "FREE" else "₹40",
                                isGreen = deliveryFee == 0.0
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DividerColor)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                Text("₹%,d".format(finalTotal.toLong()), fontSize = 16.sp, fontWeight = FontWeight.Black, color = ShopnovaBlue)
                            }

                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "You will save ₹%,d on this order".format((productDiscount + couponDiscount).toLong()),
                                color = ShopnovaGreen,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Sticky Bottom Order CTA
            Surface(
                color = SurfaceWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("₹%,d".format(finalTotal.toLong()), fontSize = 18.sp, fontWeight = FontWeight.Black, color = ShopnovaBlue)
                        Text("View Price Details", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppDestination.CHECKOUT) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(46.dp).testTag("cart_place_order_btn")
                    ) {
                        Text("PLACE ORDER", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemWithProduct,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(1.5.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Category Icon Thumbnail
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(item.product.category, item.product.subcategory),
                        contentDescription = null,
                        tint = getCategoryColor(item.product.category),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                // Item Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.product.formattedPrice,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Black,
                            color = ShopnovaBlue
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = item.product.formattedMrp,
                            fontSize = 11.sp,
                            color = TextMuted,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${item.product.discountPercent}% Off",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShopnovaGreen
                        )
                    }
                    if (item.cartItem.selectedColor.isNotEmpty()) {
                        Text("Color: ${item.cartItem.selectedColor}", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))

            // Quantity Control and Remove
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(SurfaceElevated, RoundedCornerShape(8.dp))
                ) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp), tint = TextPrimary)
                    }
                    Text(
                        text = item.cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp), tint = TextPrimary)
                    }
                }

                TextButton(onClick = onRemove) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("REMOVE", color = TextSecondary, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, value: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextPrimary)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isGreen) ShopnovaGreen else TextPrimary
        )
    }
}
