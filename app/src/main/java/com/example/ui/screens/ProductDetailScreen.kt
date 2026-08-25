package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopnovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val isWishlisted = viewModel.isProductWishlisted(product.id)
    val reviews by viewModel.getReviewsForProduct(product.id).collectAsState(initial = emptyList())
    val allProducts by viewModel.allProducts.collectAsState()
    val pincodeStatus by viewModel.pincodeStatus.collectAsState()

    var show3DView by remember { mutableStateOf(false) }
    var pincodeInput by remember { mutableStateOf("560034") }
    var selectedColor by remember { mutableStateOf(product.color) }
    var selectedSize by remember { mutableStateOf(product.size) }
    var showReviewDialog by remember { mutableStateOf(false) }

    var userRating by remember { mutableStateOf(5) }
    var reviewTitle by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var showAddedSnackbar by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(MarketplaceBackground)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // 1. Gallery / 3D Canvas
            item {
                Card(
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        // Switch between Standard & 3D Viewer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (show3DView) ShopnovaGold else ShopnovaBlueSoft,
                                modifier = Modifier.clickable { show3DView = !show3DView }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ViewInAr,
                                        contentDescription = null,
                                        tint = if (show3DView) Color.Black else ShopnovaBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = if (show3DView) "Switch to 2D Gallery" else "3D Interactive View",
                                        color = if (show3DView) Color.Black else ShopnovaBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.toggleWishlist(product) },
                                modifier = Modifier.testTag("detail_wishlist_btn")
                            ) {
                                Icon(
                                    if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = if (isWishlisted) ShopnovaRed else TextSecondary
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        if (show3DView) {
                            Simulated3DProductViewer(product = product)
                        } else {
                            // Standard Visual Stage with Category Graphics
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                getCategoryColor(product.category).copy(alpha = 0.08f),
                                                getCategoryColor(product.category).copy(alpha = 0.2f)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = getCategoryIcon(product.category, product.subcategory),
                                        contentDescription = null,
                                        tint = getCategoryColor(product.category),
                                        modifier = Modifier.size(80.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = product.brand,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextSecondary,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Official Product Showcase",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Title & Price Block
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = product.brand.uppercase(),
                            color = ShopnovaBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = product.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        // Rating badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ShopnovaGreen,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "%.1f".format(product.rating),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                }
                            }
                            Text(
                                text = "%,d Ratings & %,d Reviews".format(product.ratingCount, (product.ratingCount * 0.25).toInt()),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            if (product.isAssured) {
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ShopnovaBlueSoft
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(12.dp))
                                        Spacer(Modifier.width(2.dp))
                                        Text("Assured", color = ShopnovaBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Price & Discount row
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = product.formattedPrice,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = product.formattedMrp,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${product.discountPercent}% off",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopnovaGreen
                            )
                        }

                        Text(
                            text = "You Save ${product.formattedSavings} (Inclusive of all taxes)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ShopnovaGreen
                        )
                    }
                }
            }

            // 3. Bank Offers Block
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = ShopnovaGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Available Offers", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        OfferItem(text = "Bank Offer: 5% Unlimited Cashback on Shopnova Axis Bank Card")
                        OfferItem(text = "Special Price: Extra ₹1,000 off on UPI and Net Banking payments")
                        OfferItem(text = "No Cost EMI: Avail ₹${(product.price / 6).toInt()}/month on select credit cards")
                        OfferItem(text = "Partner Offer: Get free 3-month Gaana+ membership with this purchase")
                    }
                }
            }

            // 4. Pincode Delivery Checker
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Delivery & Pincode Checker", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = pincodeInput,
                                onValueChange = { if (it.length <= 6) pincodeInput = it },
                                label = { Text("Enter 6-digit Pincode") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.checkPincode(pincodeInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue)
                            ) {
                                Text("Check")
                            }
                        }
                        if (pincodeStatus != null) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = pincodeStatus ?: "",
                                color = if (pincodeStatus?.startsWith("✓") == true) ShopnovaGreen else ShopnovaRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 5. Highlights & Specifications
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Product Highlights", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        product.highlightsJson.split("|").forEach { hl ->
                            if (hl.isNotBlank()) {
                                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                    Text("• ", color = ShopnovaBlue, fontWeight = FontWeight.Black)
                                    Text(hl.trim(), fontSize = 12.5.sp, color = TextPrimary, lineHeight = 17.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        Text("Specifications", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        product.specsJson.split(";").forEach { spec ->
                            val parts = spec.split(":")
                            if (parts.size >= 2) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(parts[0].trim(), color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                    Text(parts[1].trim(), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.5f))
                                }
                                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            // 6. Ratings & Customer Reviews
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Customer Reviews", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            OutlinedButton(
                                onClick = { showReviewDialog = true },
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Write Review", fontSize = 11.sp)
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        if (reviews.isEmpty()) {
                            Text("No reviews yet. Be the first to review this product!", color = TextSecondary, fontSize = 12.sp)
                        } else {
                            reviews.forEach { rev ->
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (rev.rating >= 4) ShopnovaGreen else ShopnovaGold
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(rev.rating.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                                            }
                                        }
                                        Spacer(Modifier.width(6.dp))
                                        Text(rev.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    }
                                    Spacer(Modifier.height(3.dp))
                                    Text(rev.comment, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        "${rev.customerName} • ✓ Verified Buyer • ${rev.helpfulCount} helpful votes",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = DividerColor)
                                }
                            }
                        }
                    }
                }
            }

            // 7. Similar Products Carousel
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text("Similar Products You May Like", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    val similarList = allProducts.filter { it.category == product.category && it.id != product.id }.take(6)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        similarList.forEach { p ->
                            Box(modifier = Modifier.width(160.dp)) {
                                ProductGridCard(
                                    product = p,
                                    isWishlisted = viewModel.isProductWishlisted(p.id),
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

        // Sticky Bottom Action Bar
        Surface(
            color = SurfaceWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add to Cart Button
                OutlinedButton(
                    onClick = {
                        viewModel.addToCart(product, 1, selectedColor, selectedSize)
                        showAddedSnackbar = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(46.dp).testTag("detail_add_to_cart_btn")
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = ShopnovaBlue)
                    Spacer(Modifier.width(4.dp))
                    Text("Add to Cart", color = ShopnovaBlue, fontWeight = FontWeight.Bold)
                }

                // Buy Now Button
                Button(
                    onClick = { viewModel.buyNow(product) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ShopnovaGold),
                    modifier = Modifier.weight(1f).height(46.dp).testTag("detail_buy_now_btn")
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Black)
                    Spacer(Modifier.width(4.dp))
                    Text("Buy Now", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }

        // Added to Cart Confirmation Snackbar
        if (showAddedSnackbar) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp, start = 16.dp, end = 16.dp),
                action = {
                    TextButton(onClick = { showAddedSnackbar = false }) {
                        Text("OK", color = ShopnovaGold)
                    }
                }
            ) {
                Text("✓ ${product.title.take(30)}... added to your Cart!")
            }
        }

        // Write Review Dialog
        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                title = { Text("Write a Customer Review") },
                text = {
                    Column {
                        Text("Rating:")
                        Row {
                            (1..5).forEach { star ->
                                IconButton(onClick = { userRating = star }) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= userRating) ShopnovaGold else Color.LightGray
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reviewTitle,
                            onValueChange = { reviewTitle = it },
                            label = { Text("Review Headline") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            label = { Text("Detailed Review") },
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (reviewTitle.isNotBlank() && reviewComment.isNotBlank()) {
                                viewModel.submitReview(product.id, userRating, reviewTitle, reviewComment)
                                showReviewDialog = false
                                reviewTitle = ""
                                reviewComment = ""
                            }
                        }
                    ) {
                        Text("Submit Review")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReviewDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun OfferItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = ShopnovaGreen, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
    }
}
