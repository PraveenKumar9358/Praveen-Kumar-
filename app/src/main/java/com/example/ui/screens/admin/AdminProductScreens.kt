package com.example.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ShopnovaViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    var searchAdminQuery by remember { mutableStateOf("") }

    val filteredList = if (searchAdminQuery.isBlank()) allProducts else {
        allProducts.filter {
            it.title.contains(searchAdminQuery, true) ||
            it.brand.contains(searchAdminQuery, true) ||
            it.category.contains(searchAdminQuery, true)
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(SurfaceWhite).padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Products Catalog (${allProducts.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Button(
                        onClick = { viewModel.startEditProduct(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Product")
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchAdminQuery,
                    onValueChange = { searchAdminQuery = it },
                    placeholder = { Text("Search catalog by name, brand, category...") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        modifier = modifier.fillMaxSize().testTag("admin_products_screen")
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MarketplaceBackground),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList, key = { it.id }) { product ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(ShopnovaBlueSoft)
                        ) {
                            Icon(getCategoryIcon(product.category, product.subcategory), contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                            Text("${product.brand} • ${product.category}", fontSize = 11.sp, color = TextSecondary)
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(product.formattedPrice, fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextPrimary)
                                Spacer(Modifier.width(6.dp))
                                Text("Stock: ${product.stock}", fontSize = 11.sp, color = if (product.stock < 10) ShopnovaRed else ShopnovaGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Actions: Edit and Delete
                        IconButton(onClick = { viewModel.startEditProduct(product) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ShopnovaBlue, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { viewModel.deleteProductAdmin(product.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = ShopnovaRed, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductEditScreen(
    productToEdit: Product?,
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(productToEdit?.title ?: "") }
    var brand by remember { mutableStateOf(productToEdit?.brand ?: "Shopnova") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "Electronics") }
    var subcategory by remember { mutableStateOf(productToEdit?.subcategory ?: "Audio") }
    var priceStr by remember { mutableStateOf(productToEdit?.price?.toInt()?.toString() ?: "1999") }
    var mrpStr by remember { mutableStateOf(productToEdit?.mrp?.toInt()?.toString() ?: "4999") }
    var stockStr by remember { mutableStateOf(productToEdit?.stock?.toString() ?: "50") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var highlightsStr by remember { mutableStateOf(productToEdit?.highlightsJson ?: "100% Genuine Brand Certified|1 Year Warranty|7-Day Easy Return") }
    var specsStr by remember { mutableStateOf(productToEdit?.specsJson ?: "Warranty: 1 Year; Origin: India") }
    var isAssured by remember { mutableStateOf(productToEdit?.isAssured ?: true) }

    // AI Modal State
    var showAiModal by remember { mutableStateOf(false) }
    var aiPrompt by remember { mutableStateOf("") }
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val aiResult by viewModel.aiGeneratedResult.collectAsState()

    // Observe AI Result
    LaunchedEffect(aiResult) {
        aiResult?.let { res ->
            title = res.title
            brand = res.brand
            category = res.category
            subcategory = res.subcategory
            priceStr = res.price.toInt().toString()
            mrpStr = res.mrp.toInt().toString()
            description = res.description
            highlightsStr = res.highlights.joinToString("|")
            specsStr = res.specs.entries.joinToString("; ") { "${it.key}: ${it.value}" }
            showAiModal = false
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .padding(12.dp)
            .testTag("admin_product_edit_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Generator Header Banner
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ShopnovaBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ShopnovaGold, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Gemini AI Product Generator", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("Auto-generate title, description, specifications, and pricing from an idea prompt.", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                    }

                    Button(
                        onClick = { showAiModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaGold),
                        modifier = Modifier.testTag("ai_generate_product_btn")
                    ) {
                        Text("Use AI", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Form Fields
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (productToEdit == null) "Create New Product" else "Edit Product", fontSize = 15.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = subcategory, onValueChange = { subcategory = it }, label = { Text("Subcategory") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = stockStr, onValueChange = { stockStr = it }, label = { Text("Stock Units") }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Selling Price (₹)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = mrpStr, onValueChange = { mrpStr = it }, label = { Text("MRP (₹)") }, modifier = Modifier.weight(1f))
                    }

                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, maxLines = 3, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = highlightsStr, onValueChange = { highlightsStr = it }, label = { Text("Highlights (separated by |)") }, maxLines = 2, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = specsStr, onValueChange = { specsStr = it }, label = { Text("Specifications (key: value; key: value)") }, maxLines = 3, modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shopnova Assured Badge")
                        Switch(checked = isAssured, onCheckedChange = { isAssured = it })
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val price = priceStr.toDoubleOrNull() ?: 999.0
                            val mrp = mrpStr.toDoubleOrNull() ?: (price * 1.8)
                            val discount = if (mrp > 0) (((mrp - price) / mrp) * 100).toInt() else 0
                            val stock = stockStr.toIntOrNull() ?: 50

                            val newProduct = Product(
                                id = productToEdit?.id ?: "prod_${UUID.randomUUID().toString().take(8)}",
                                sku = productToEdit?.sku ?: "SN-SKU-${(1000..9999).random()}",
                                title = title.ifBlank { "Shopnova Special Product" },
                                brand = brand.ifBlank { "Shopnova" },
                                category = category.ifBlank { "Electronics" },
                                subcategory = subcategory.ifBlank { "General" },
                                price = price,
                                mrp = mrp,
                                discountPercent = discount,
                                stock = stock,
                                description = description.ifBlank { "High quality product on Shopnova." },
                                highlightsJson = highlightsStr,
                                specsJson = specsStr,
                                isAssured = isAssured
                            )
                            viewModel.saveProductAdmin(newProduct)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Save & Publish Product", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // AI Generator Modal
    if (showAiModal) {
        AlertDialog(
            onDismissRequest = { showAiModal = false },
            title = { Text("Gemini AI Product Generator") },
            text = {
                Column {
                    Text("Enter a product concept or keyword:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = aiPrompt,
                        onValueChange = { aiPrompt = it },
                        placeholder = { Text("e.g. Wireless ANC Earbuds with 40h battery") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isAiGenerating) {
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Gemini is designing product specs & pricing...", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (aiPrompt.isNotBlank()) {
                            viewModel.generateProductWithAi(aiPrompt)
                        }
                    },
                    enabled = !isAiGenerating
                ) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiModal = false }) { Text("Cancel") }
            }
        )
    }
}
