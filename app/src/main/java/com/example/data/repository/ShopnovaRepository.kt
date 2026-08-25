package com.example.data.repository

import com.example.data.local.ShopnovaDatabase
import com.example.data.model.*
import com.example.data.seed.ProductSeedGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ShopnovaRepository(private val database: ShopnovaDatabase) {

    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val orderDao = database.orderDao()
    private val addressDao = database.addressDao()
    private val reviewDao = database.reviewDao()
    private val couponDao = database.couponDao()
    private val notificationDao = database.notificationDao()

    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val trendingProducts: Flow<List<Product>> = productDao.getTrendingProducts()
    val dealOfDayProducts: Flow<List<Product>> = productDao.getDealOfDayProducts()
    val flashSaleProducts: Flow<List<Product>> = productDao.getFlashSaleProducts()

    suspend fun getProductById(id: String): Product? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category)

    fun searchProducts(query: String): Flow<List<Product>> =
        productDao.searchProducts(query)

    suspend fun getProductCount(): Int = withContext(Dispatchers.IO) {
        productDao.getProductCount()
    }

    suspend fun insertProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(id: String) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(id)
    }

    suspend fun updateStock(id: String, newStock: Int) = withContext(Dispatchers.IO) {
        productDao.updateStock(id, newStock)
    }

    suspend fun seedProducts(count: Int) = withContext(Dispatchers.IO) {
        val products = ProductSeedGenerator.generateUniqueProducts(count)
        productDao.insertProducts(products)
    }

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val count = productDao.getProductCount()
        if (count == 0) {
            // Seed initial 200 rich products
            val products = ProductSeedGenerator.generateUniqueProducts(200)
            productDao.insertProducts(products)

            // Seed default coupons
            couponDao.insertCoupons(ProductSeedGenerator.getDefaultCoupons())

            // Seed default addresses
            ProductSeedGenerator.getDefaultAddresses().forEach {
                addressDao.insertAddress(it)
            }

            // Seed default reviews for hero products
            listOf("hero-1", "hero-2", "hero-3", "hero-4", "hero-5").forEach { pId ->
                ProductSeedGenerator.getDefaultReviews(pId).forEach { rev ->
                    reviewDao.insertReview(rev)
                }
            }

            // Seed default notifications
            ProductSeedGenerator.getDefaultNotifications().forEach {
                notificationDao.insertNotification(it)
            }
        }
    }

    // Cart
    val allCartItems: Flow<List<CartItem>> = cartDao.getAllCartItems()

    suspend fun addToCart(productId: String, quantity: Int = 1, color: String = "", size: String = "") = withContext(Dispatchers.IO) {
        val existing = cartDao.getCartItemByProductId(productId)
        if (existing != null) {
            cartDao.updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            cartDao.insertCartItem(
                CartItem(
                    productId = productId,
                    quantity = quantity,
                    selectedColor = color,
                    selectedSize = size
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteCartItem(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, quantity)
        }
    }

    suspend fun removeFromCart(cartItemId: Long) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    // Wishlist
    val allWishlistItems: Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()

    fun isWishlisted(productId: String): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    suspend fun toggleWishlist(productId: String, isCurrentlyWishlisted: Boolean) = withContext(Dispatchers.IO) {
        if (isCurrentlyWishlisted) {
            wishlistDao.deleteWishlist(productId)
        } else {
            wishlistDao.insertWishlist(WishlistItem(productId = productId))
        }
    }

    // Orders
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    suspend fun getOrderById(id: String): Order? = withContext(Dispatchers.IO) {
        orderDao.getOrderById(id)
    }

    suspend fun placeOrder(order: Order) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
        cartDao.clearCart()

        // Create order confirmation notification
        notificationDao.insertNotification(
            NotificationItem(
                title = "Order Confirmed: #${order.id} 📦",
                message = "Your order for ${order.formattedTotal} has been successfully placed! Expected delivery by ${order.expectedDeliveryDate}.",
                type = "ORDER"
            )
        )
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
        notificationDao.insertNotification(
            NotificationItem(
                title = "Order Update: #$orderId",
                message = "Status changed to: $status",
                type = "ORDER"
            )
        )
    }

    suspend fun requestReturn(orderId: String, reason: String) = withContext(Dispatchers.IO) {
        orderDao.requestReturn(orderId, reason)
        notificationDao.insertNotification(
            NotificationItem(
                title = "Return Request Received: #$orderId 🔄",
                message = "Our executive will verify the return reason ($reason) within 24-48 hours.",
                type = "ORDER"
            )
        )
    }

    // Addresses
    val allAddresses: Flow<List<Address>> = addressDao.getAllAddresses()

    suspend fun saveAddress(address: Address) = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            addressDao.resetDefaults()
        }
        if (address.id == 0L) {
            addressDao.insertAddress(address)
        } else {
            addressDao.updateAddress(address)
        }
    }

    suspend fun deleteAddress(id: Long) = withContext(Dispatchers.IO) {
        addressDao.deleteAddress(id)
    }

    suspend fun setDefaultAddress(id: Long) = withContext(Dispatchers.IO) {
        addressDao.resetDefaults()
        addressDao.setDefault(id)
    }

    // Reviews
    fun getReviewsForProduct(productId: String): Flow<List<Review>> =
        reviewDao.getReviewsForProduct(productId)

    suspend fun submitReview(review: Review) = withContext(Dispatchers.IO) {
        reviewDao.insertReview(review)
    }

    // Coupons
    val allCoupons: Flow<List<Coupon>> = couponDao.getAllCoupons()

    suspend fun getCoupon(code: String): Coupon? = withContext(Dispatchers.IO) {
        couponDao.getCoupon(code.trim().uppercase())
    }

    // Notifications
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }
}
