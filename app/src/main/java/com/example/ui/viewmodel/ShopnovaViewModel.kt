package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ShopnovaDatabase
import com.example.data.model.*
import com.example.data.repository.ShopnovaRepository
import com.example.domain.ai.AiGeneratedProductResult
import com.example.domain.ai.GeminiProductAssistant
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppDestination {
    HOME,
    SEARCH_CATALOG,
    PRODUCT_DETAIL,
    CART,
    WISHLIST,
    CHECKOUT,
    ORDER_SUCCESS,
    ORDER_HISTORY,
    ORDER_DETAIL,
    PROFILE,
    CUSTOMER_SUPPORT,
    ADMIN_DASHBOARD,
    ADMIN_PRODUCTS,
    ADMIN_PRODUCT_EDIT,
    ADMIN_ORDERS,
    ADMIN_INVENTORY,
    ADMIN_COUPONS,
    ADMIN_GENERATOR
}

enum class SortOption(val label: String) {
    RELEVANCE("Relevance"),
    POPULARITY("Popularity"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    RATING("Customer Rating"),
    DISCOUNT("Discount %")
}

data class FilterState(
    val category: String = "All",
    val subcategory: String = "All",
    val minRating: Float = 0f,
    val maxPrice: Double = 100000.0,
    val minDiscount: Int = 0,
    val assuredOnly: Boolean = false,
    val inStockOnly: Boolean = false
)

class ShopnovaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShopnovaRepository

    init {
        val db = ShopnovaDatabase.getDatabase(application)
        repository = ShopnovaRepository(db)

        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }
    }

    // Navigation state
    private val _currentDestination = MutableStateFlow(AppDestination.HOME)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _navigationStack = mutableListOf<AppDestination>()

    fun navigateTo(destination: AppDestination) {
        _navigationStack.add(_currentDestination.value)
        _currentDestination.value = destination
    }

    fun navigateBack(): Boolean {
        if (_navigationStack.isNotEmpty()) {
            _currentDestination.value = _navigationStack.removeAt(_navigationStack.size - 1)
            return true
        }
        if (_currentDestination.value != AppDestination.HOME) {
            _currentDestination.value = AppDestination.HOME
            return true
        }
        return false
    }

    // Products Data
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingProducts: StateFlow<List<Product>> = repository.trendingProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dealOfDayProducts: StateFlow<List<Product>> = repository.dealOfDayProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashSaleProducts: StateFlow<List<Product>> = repository.flashSaleProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Product for detail view
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
        navigateTo(AppDestination.PRODUCT_DETAIL)
    }

    fun selectProductById(id: String) {
        viewModelScope.launch {
            val product = repository.getProductById(id)
            if (product != null) {
                _selectedProduct.value = product
                navigateTo(AppDestination.PRODUCT_DETAIL)
            }
        }
    }

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RELEVANCE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String) {
        _filterState.value = _filterState.value.copy(category = category)
    }

    fun updateFilters(newFilterState: FilterState) {
        _filterState.value = newFilterState
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    // Filtered & Sorted Products computation
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _searchQuery,
        _filterState,
        _sortOption
    ) { products, query, filters, sort ->
        var list = products

        // Search query filter
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.brand.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.subcategory.lowercase().contains(q) ||
                it.sku.lowercase().contains(q)
            }
        }

        // Category filter
        if (filters.category != "All") {
            list = list.filter { it.category.equals(filters.category, ignoreCase = true) }
        }

        // Subcategory filter
        if (filters.subcategory != "All") {
            list = list.filter { it.subcategory.equals(filters.subcategory, ignoreCase = true) }
        }

        // Price filter
        list = list.filter { it.price <= filters.maxPrice }

        // Min rating
        if (filters.minRating > 0) {
            list = list.filter { it.rating >= filters.minRating }
        }

        // Min discount
        if (filters.minDiscount > 0) {
            list = list.filter { it.discountPercent >= filters.minDiscount }
        }

        // Assured
        if (filters.assuredOnly) {
            list = list.filter { it.isAssured }
        }

        // In Stock
        if (filters.inStockOnly) {
            list = list.filter { it.stock > 0 }
        }

        // Sorting
        when (sort) {
            SortOption.RELEVANCE -> list
            SortOption.POPULARITY -> list.sortedByDescending { it.ratingCount }
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.price }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.DISCOUNT -> list.sortedByDescending { it.discountPercent }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart State
    val cartItems: StateFlow<List<CartItem>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartWithProducts: StateFlow<List<CartItemWithProduct>> = combine(
        cartItems,
        allProducts
    ) { items, products ->
        val productMap = products.associateBy { it.id }
        items.mapNotNull { item ->
            productMap[item.productId]?.let { product ->
                CartItemWithProduct(item, product)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Applied Coupon
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _couponError = MutableStateFlow<String?>(null)
    val couponError: StateFlow<String?> = _couponError.asStateFlow()

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            _couponError.value = null
            val coupon = repository.getCoupon(code)
            if (coupon != null) {
                val total = cartWithProducts.value.sumOf { it.product.price * it.cartItem.quantity }
                if (total >= coupon.minOrder) {
                    _appliedCoupon.value = coupon
                } else {
                    _couponError.value = "Minimum order value of ₹%,d required for this coupon".format(coupon.minOrder.toLong())
                }
            } else {
                _couponError.value = "Invalid or expired coupon code"
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponError.value = null
    }

    // Cart Actions
    fun addToCart(product: Product, quantity: Int = 1, color: String = "", size: String = "") {
        viewModelScope.launch {
            repository.addToCart(product.id, quantity, color.ifEmpty { product.color }, size.ifEmpty { product.size })
        }
    }

    fun buyNow(product: Product) {
        viewModelScope.launch {
            repository.addToCart(product.id, 1, product.color, product.size)
            navigateTo(AppDestination.CHECKOUT)
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(cartItemId)
        }
    }

    // Wishlist State
    val wishlistItems: StateFlow<List<WishlistItem>> = repository.allWishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistWithProducts: StateFlow<List<Product>> = combine(
        wishlistItems,
        allProducts
    ) { items, products ->
        val productMap = products.associateBy { it.id }
        items.mapNotNull { productMap[it.productId] }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isWishlisted = wishlistItems.value.any { it.productId == product.id }
            repository.toggleWishlist(product.id, isWishlisted)
        }
    }

    fun isProductWishlisted(productId: String): Boolean {
        return wishlistItems.value.any { it.productId == productId }
    }

    // Addresses State
    val addresses: StateFlow<List<Address>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAddressId = MutableStateFlow<Long?>(null)
    val selectedAddressId: StateFlow<Long?> = _selectedAddressId.asStateFlow()

    fun selectAddress(id: Long) {
        _selectedAddressId.value = id
    }

    fun saveAddress(address: Address) {
        viewModelScope.launch {
            repository.saveAddress(address)
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
        }
    }

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
        }
    }

    // Checkout & Payment State
    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.UPI)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    fun placeCurrentOrder() {
        viewModelScope.launch {
            val items = cartWithProducts.value
            if (items.isEmpty()) return@launch

            val addressList = addresses.value
            val address = addressList.find { it.id == _selectedAddressId.value } ?: addressList.firstOrNull() ?: Address(
                fullName = "Praveen Kumar",
                phone = "9876543210",
                houseNo = "Flat 402",
                area = "Koramangala",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560034"
            )

            val totalMrp = items.sumOf { it.product.mrp * it.cartItem.quantity }
            val subtotal = items.sumOf { it.product.price * it.cartItem.quantity }
            var couponDiscount = 0.0
            _appliedCoupon.value?.let { cp ->
                couponDiscount = ((subtotal * cp.discountPercent) / 100.0).coerceAtMost(cp.maxDiscount)
            }
            val deliveryFee = if (subtotal > 499) 0.0 else 40.0
            val finalTotal = (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)

            val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 2)
            val expectedDate = sdf.format(cal.time)

            val orderId = "SN-" + (100000 + Random().nextInt(900000))
            val itemsSummary = items.joinToString("; ") { "${it.product.title} (x${it.cartItem.quantity})" }

            val order = Order(
                id = orderId,
                customerName = address.fullName,
                customerPhone = address.phone,
                addressLine = address.fullAddressText,
                city = address.city,
                state = address.state,
                pincode = address.pincode,
                addressType = address.addressType,
                itemsSummaryJson = itemsSummary,
                totalAmount = finalTotal,
                discountAmount = (totalMrp - subtotal) + couponDiscount,
                deliveryFee = deliveryFee,
                paymentMethod = _selectedPaymentMethod.value.label,
                paymentStatus = "SUCCESS",
                orderStatus = OrderStatus.CONFIRMED.name,
                expectedDeliveryDate = expectedDate
            )

            repository.placeOrder(order)
            _lastPlacedOrder.value = order
            _appliedCoupon.value = null
            navigateTo(AppDestination.ORDER_SUCCESS)
        }
    }

    // Orders State
    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    fun selectOrder(order: Order) {
        _selectedOrder.value = order
        navigateTo(AppDestination.ORDER_DETAIL)
    }

    fun requestReturn(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.requestReturn(orderId, reason)
            val updated = repository.getOrderById(orderId)
            _selectedOrder.value = updated
        }
    }

    // Reviews State
    fun getReviewsForProduct(productId: String): Flow<List<Review>> {
        return repository.getReviewsForProduct(productId)
    }

    fun submitReview(productId: String, rating: Int, title: String, comment: String) {
        viewModelScope.launch {
            repository.submitReview(
                Review(
                    productId = productId,
                    customerName = "Praveen Kumar",
                    rating = rating,
                    title = title,
                    comment = comment,
                    isVerifiedBuyer = true
                )
            )
        }
    }

    // Pincode Delivery checker
    private val _pincodeStatus = MutableStateFlow<String?>(null)
    val pincodeStatus: StateFlow<String?> = _pincodeStatus.asStateFlow()

    fun checkPincode(pincode: String) {
        if (pincode.length == 6 && pincode.all { it.isDigit() }) {
            _pincodeStatus.value = "✓ Delivery Available! Guaranteed delivery by tomorrow 7 PM. Free Delivery eligible."
        } else {
            _pincodeStatus.value = "Please enter a valid 6-digit Indian PIN code."
        }
    }

    // Notifications
    val notifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    // ADMIN CMS & CONTROL STATE
    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    fun startEditProduct(product: Product?) {
        _editingProduct.value = product
        navigateTo(AppDestination.ADMIN_PRODUCT_EDIT)
    }

    fun saveProductAdmin(product: Product) {
        viewModelScope.launch {
            if (_allProductsContains(product.id)) {
                repository.updateProduct(product)
            } else {
                repository.insertProduct(product)
            }
            navigateTo(AppDestination.ADMIN_PRODUCTS)
        }
    }

    private fun _allProductsContains(id: String): Boolean {
        return allProducts.value.any { it.id == id }
    }

    fun deleteProductAdmin(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun updateStockAdmin(id: String, newStock: Int) {
        viewModelScope.launch {
            repository.updateStock(id, newStock)
        }
    }

    fun updateOrderStatusAdmin(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            if (_selectedOrder.value?.id == orderId) {
                _selectedOrder.value = repository.getOrderById(orderId)
            }
        }
    }

    // Deterministic Bulk Products Seeder (100, 500, 1000, 5000, 10000)
    private val _isSeeding = MutableStateFlow(false)
    val isSeeding: StateFlow<Boolean> = _isSeeding.asStateFlow()

    private val _seedingMessage = MutableStateFlow<String?>(null)
    val seedingMessage: StateFlow<String?> = _seedingMessage.asStateFlow()

    fun seedBulkProducts(count: Int) {
        viewModelScope.launch {
            _isSeeding.value = true
            _seedingMessage.value = "Generating $count unique categorized products with Indian pricing & specs..."
            try {
                repository.seedProducts(count)
                _seedingMessage.value = "Successfully generated and seeded $count unique products into Shopnova!"
            } catch (e: Exception) {
                _seedingMessage.value = "Seeding completed: ${e.message}"
            } finally {
                _isSeeding.value = false
            }
        }
    }

    // AI Product Assistant Generation
    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _aiGeneratedResult = MutableStateFlow<AiGeneratedProductResult?>(null)
    val aiGeneratedResult: StateFlow<AiGeneratedProductResult?> = _aiGeneratedResult.asStateFlow()

    fun generateProductWithAi(prompt: String) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            try {
                val result = GeminiProductAssistant.generateProductContent(prompt)
                _aiGeneratedResult.value = result
            } finally {
                _isAiGenerating.value = false
            }
        }
    }
}
