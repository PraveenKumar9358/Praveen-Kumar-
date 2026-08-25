package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.ui.components.ProductGridCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.ShopnovaViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchCatalogScreen(
    viewModel: ShopnovaViewModel,
    modifier: Modifier = Modifier
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MarketplaceBackground)
            .testTag("search_catalog_screen")
    ) {
        // Quick Filters & Sort Bar
        Surface(
            color = SurfaceWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sort Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceElevated,
                    modifier = Modifier.clickable { showSortSheet = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = null, tint = ShopnovaBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Sort: ${sortOption.label}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Filter Button with active indicator
                val hasActiveFilters = filterState.category != "All" || filterState.minRating > 0 || filterState.assuredOnly
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (hasActiveFilters) ShopnovaBlue else SurfaceElevated,
                    modifier = Modifier.clickable { showFilterSheet = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            tint = if (hasActiveFilters) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (filterState.category != "All") "Filters (1+)" else "Filters",
                            color = if (hasActiveFilters) Color.White else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Quick Category Filter Tags
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Electronics", "Fashion", "Home & Kitchen", "Beauty", "Grocery", "Sports", "Books", "Toys").forEach { cat ->
                val isSelected = filterState.category.equals(cat, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCategoryFilter(cat) },
                    label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ShopnovaBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Results count
        PaddingValues(horizontal = 12.dp, vertical = 4.dp).let {
            Text(
                text = "Showing %,d products".format(filteredProducts.size),
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        if (filteredProducts.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No Products Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Try adjusting your search query or removing filters.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.updateFilters(FilterState())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            // Products 2-column Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 80.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.id }) { product ->
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

    // Sort Bottom Sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Sort By",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(12.dp))
                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setSortOption(option)
                                showSortSheet = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option.label,
                            fontSize = 14.sp,
                            fontWeight = if (sortOption == option) FontWeight.Bold else FontWeight.Normal,
                            color = if (sortOption == option) ShopnovaBlue else TextPrimary
                        )
                        if (sortOption == option) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = ShopnovaBlue)
                        }
                    }
                }
            }
        }
    }

    // Dynamic Filter Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Filter Products",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(12.dp))

                // Assured Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Shopnova Assured Only", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = filterState.assuredOnly,
                        onCheckedChange = { viewModel.updateFilters(filterState.copy(assuredOnly = it)) }
                    )
                }

                // In Stock Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("In Stock Only", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = filterState.inStockOnly,
                        onCheckedChange = { viewModel.updateFilters(filterState.copy(inStockOnly = it)) }
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Min Rating
                Text("Minimum Rating", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0f to "All", 3.5f to "3.5★+", 4.0f to "4.0★+", 4.5f to "4.5★+").forEach { (rating, label) ->
                        val isSelected = filterState.minRating == rating
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateFilters(filterState.copy(minRating = rating)) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.updateFilters(FilterState())
                            showFilterSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear All")
                    }
                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ShopnovaBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply Filters")
                    }
                }
            }
        }
    }
}
