package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        Product::class,
        CartItem::class,
        WishlistItem::class,
        Order::class,
        Address::class,
        Review::class,
        Coupon::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ShopnovaDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun reviewDao(): ReviewDao
    abstract fun couponDao(): CouponDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: ShopnovaDatabase? = null

        fun getDatabase(context: Context): ShopnovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShopnovaDatabase::class.java,
                    "shopnova_marketplace.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
