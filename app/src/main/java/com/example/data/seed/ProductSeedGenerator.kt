package com.example.data.seed

import com.example.data.model.Address
import com.example.data.model.Coupon
import com.example.data.model.NotificationItem
import com.example.data.model.Product
import com.example.data.model.Review
import kotlin.random.Random

object ProductSeedGenerator {

    private val categories = listOf(
        "Electronics",
        "Fashion",
        "Home & Kitchen",
        "Beauty",
        "Grocery",
        "Sports",
        "Books",
        "Toys"
    )

    private val brandsByCategory = mapOf(
        "Electronics" to listOf("NovaTech", "AeroSound", "VividVision", "PulseGear", "Zenith", "HyperVolt", "SonicMax", "QuantumX"),
        "Fashion" to listOf("UrbanNova", "AuraWeave", "VogueCraft", "DenimCo", "RoyalKhaadi", "StepSprint", "VelvetLoom", "IndigoBay"),
        "Home & Kitchen" to listOf("ChefNova", "HomeEssence", "PureLiving", "LuxeComfort", "EcoCraft", "PrestigeNova", "TimberBay", "ZenHaven"),
        "Beauty" to listOf("GlowNova", "DermaPure", "HerbAura", "LuxeOrganics", "VelvetSkin", "Botanica", "SaffronGlow", "AyurNova"),
        "Grocery" to listOf("FarmNova", "RoyalGrains", "NatureSpices", "OrganicHills", "GoldenHarvest", "PureBlend", "FreshRoots", "SpiceCraft"),
        "Sports" to listOf("ProSprint", "IronNova", "AeroFit", "FlexGym", "WillowCraft", "StaminaPro", "TitanFlex", "ActiveLife"),
        "Books" to listOf("NovaPress", "HeritageBooks", "WisdomHouse", "EpicTales", "MasteryPub", "HorizonReads", "MindCraft", "ScholarNova"),
        "Toys" to listOf("ToyNova", "RoboKids", "BrainBlocks", "FunSpark", "LittleGenius", "SpeedRacer", "PlayHaven", "CraftyJoy")
    )

    private val subcategoriesByCategory = mapOf(
        "Electronics" to listOf("Smartphones", "Laptops", "Wireless Earbuds", "Smart Watches", "Bluetooth Speakers", "Cameras", "Tablets", "Power Banks"),
        "Fashion" to listOf("Men's Shirts", "Women's Kurtis", "Denim Jeans", "Sneakers & Shoes", "Handbags & Wallets", "Silk Sarees", "Casual T-Shirts", "Jackets"),
        "Home & Kitchen" to listOf("Mixer Grinders", "Cookware Sets", "Office Chairs", "Coffee Tables", "Air Fryers", "Water Purifiers", "Bed Sheets", "LED Lamps"),
        "Beauty" to listOf("Face Serums", "Organic Hair Oils", "Liquid Lipsticks", "Face Washes", "Beard Kits", "Moisturizers", "Sunscreen SPF 50", "Perfumes"),
        "Grocery" to listOf("Basmati Rice", "Cold-Pressed Oils", "Dry Fruits & Nuts", "Masala Chai", "Organic Honey", "Filter Coffee", "Indian Spices", "Breakfast Cereals"),
        "Sports" to listOf("Cricket Bats", "Yoga Mats", "Dumbbell Sets", "Gym Gloves", "Running Shoes", "Speed Ropes", "Badminton Rackets", "Protein Shakers"),
        "Books" to listOf("Fiction Bestsellers", "Competitive Exam Guides", "Self Help & Growth", "Indian History", "Business & Finance", "Biographies", "Children Books", "Philosophy"),
        "Toys" to listOf("STEM Robotics Kits", "RC Drift Cars", "Wooden Puzzles", "Board Games", "Action Figures", "Building Blocks", "Soft Toys", "Art & Craft")
    )

    private val adjectives = listOf(
        "Ultra Pro", "Max Edition", "Prime Gold", "Elite Series", "Air Slim", "NextGen", "Smart AI", "Classic Wood",
        "Super Fast", "Handcrafted", "Dynamic", "Wireless Turbo", "Eco Pure", "Precision Crafted", "Premium Luxury",
        "High Velocity", "Comfort Fit", "Titanium Tough", "Signature Blend", "Pure Copper", "Ergonomic Flex"
    )

    private val colors = listOf("Midnight Blue", "Phantom Black", "Pearl White", "Emerald Green", "Rose Gold", "Space Gray", "Royal Navy", "Crimson Red")

    fun generateUniqueProducts(count: Int = 1000, seed: Long = 42L): List<Product> {
        val random = Random(seed)
        val list = ArrayList<Product>(count)

        // First, add our flagship hand-curated hero products
        list.addAll(getFeaturedHeroProducts())

        val startIdx = list.size
        for (i in startIdx until count) {
            val category = categories[i % categories.size]
            val subcats = subcategoriesByCategory[category] ?: listOf("General")
            val subcat = subcats[(i / categories.size) % subcats.size]
            val brands = brandsByCategory[category] ?: listOf("Shopnova")
            val brand = brands[(i * 3 + random.nextInt(100)) % brands.size]
            val adj = adjectives[(i * 7 + random.nextInt(100)) % adjectives.size]
            val color = colors[random.nextInt(colors.size)]

            val title = "$brand $adj $subcat #$i"
            val sku = "SN-${category.take(3).uppercase()}-${i + 1000}"

            val basePrice = when (category) {
                "Electronics" -> random.nextInt(999, 85000).toDouble()
                "Fashion" -> random.nextInt(399, 5999).toDouble()
                "Home & Kitchen" -> random.nextInt(499, 18999).toDouble()
                "Beauty" -> random.nextInt(199, 2999).toDouble()
                "Grocery" -> random.nextInt(99, 1499).toDouble()
                "Sports" -> random.nextInt(299, 8999).toDouble()
                "Books" -> random.nextInt(149, 1299).toDouble()
                "Toys" -> random.nextInt(249, 4999).toDouble()
                else -> random.nextInt(299, 3999).toDouble()
            }

            val discountPercent = random.nextInt(10, 75)
            val mrp = (basePrice / (1.0 - (discountPercent / 100.0))).coerceAtLeast(basePrice + 100).let {
                ((it.toInt() / 50) * 50 + 49).toDouble() // Clean Indian retail ending (.99/49)
            }
            val price = ((basePrice.toInt() / 10) * 10 + 9).toDouble()

            val rating = (35 + random.nextInt(15)) / 10.0f
            val ratingCount = random.nextInt(15, 14500)
            val stock = if (random.nextInt(100) < 5) 0 else random.nextInt(3, 120)

            val highlights = listOf(
                "Genuine 100% Original $brand item",
                "Specially engineered with $adj design",
                "Includes ${if (category == "Electronics") "Fast Charging & 1 Year Warranty" else "7-Day Easy Return Policy"}",
                "Best suited for Indian households and daily lifestyle",
                "Color: $color | Certified Quality Tested"
            )

            val specs = listOf(
                "Brand" to brand,
                "Model" to "$adj-X${i % 99}",
                "Category" to category,
                "Subcategory" to subcat,
                "Color" to color,
                "Origin" to "Made in India",
                "Warranty" to if (category == "Electronics") "1 Year Comprehensive Warranty" else "6 Months Stitch / Quality Guarantee"
            )

            val highlightsJson = highlights.joinToString(" | ")
            val specsJson = specs.joinToString(";") { "${it.first}:${it.second}" }

            list.add(
                Product(
                    id = "p-$i",
                    sku = sku,
                    title = title,
                    brand = brand,
                    category = category,
                    subcategory = subcat,
                    price = price,
                    mrp = mrp,
                    discountPercent = discountPercent,
                    rating = rating,
                    ratingCount = ratingCount,
                    stock = stock,
                    isAssured = random.nextBoolean(),
                    isTrending = (i % 7 == 0),
                    isDealOfDay = (i % 9 == 0),
                    isFlashSale = (i % 11 == 0),
                    description = "Experience premium performance and unmatched durability with the all-new $title. Meticulously designed for demanding Indian consumers, this $subcat delivers industry-leading reliability, elegant aesthetics, and incredible everyday value.",
                    highlightsJson = highlightsJson,
                    specsJson = specsJson,
                    color = color,
                    size = if (category == "Fashion") listOf("S", "M", "L", "XL", "XXL")[i % 5] else "Standard",
                    warranty = if (category == "Electronics") "1 Year Brand Warranty" else "30 Days Quality Guarantee",
                    returnPolicyDays = if (category == "Grocery") 0 else 7,
                    deliveryDays = random.nextInt(1, 4),
                    imageCategory = category.lowercase().replace(" & ", "_")
                )
            )
        }

        return list
    }

    private fun getFeaturedHeroProducts(): List<Product> {
        return listOf(
            Product(
                id = "hero-1",
                sku = "SN-ELEC-PRO1",
                title = "NovaTech Quantum 5G (Midnight Blue, 256 GB, 12 GB RAM)",
                brand = "NovaTech",
                category = "Electronics",
                subcategory = "Smartphones",
                price = 29999.0,
                mrp = 44999.0,
                discountPercent = 33,
                rating = 4.7f,
                ratingCount = 18420,
                stock = 45,
                isAssured = true,
                isTrending = true,
                isDealOfDay = true,
                isFlashSale = true,
                description = "Unleash supercharged speed with the NovaTech Quantum 5G. Featuring a 144Hz AMOLED TrueColor Display, Dimensity 9200+ Ultra chipset, 108MP OIS AI Camera, and 120W HyperCharge that powers 100% in 19 minutes.",
                highlightsJson = "12 GB RAM | 256 GB ROM | Expandable Upto 1 TB | 17.02 cm (6.7 inch) 1.5K 144Hz AMOLED Display | 108MP (OIS) + 8MP + 2MP Triple AI Camera | 32MP Front Selfie Camera | 5000 mAh Battery with 120W Flash Charger in Box | Dimensity 9200+ Flagship Processor",
                specsJson = "In The Box:Handset, 120W Adapter, Type-C Cable, TPU Case, SIM Ejector;Display:6.7 inch Full HD+ 144Hz AMOLED;Processor:MediaTek Dimensity 9200+ Octa Core 3.35GHz;OS:NovaOS 14 based on Android 14;Network:5G Dual SIM VoLTE;Warranty:1 Year Handset and 6 Months Accessories",
                color = "Midnight Blue",
                size = "12GB + 256GB",
                warranty = "1 Year Manufacturer Warranty",
                returnPolicyDays = 7,
                deliveryDays = 1,
                imageCategory = "electronics"
            ),
            Product(
                id = "hero-2",
                sku = "SN-ELEC-EAR1",
                title = "AeroSound SpaceBuds Pro ANC Wireless Earbuds (48h Playtime, LDAC)",
                brand = "AeroSound",
                category = "Electronics",
                subcategory = "Wireless Earbuds",
                price = 2499.0,
                mrp = 6999.0,
                discountPercent = 64,
                rating = 4.6f,
                ratingCount = 9540,
                stock = 120,
                isAssured = true,
                isTrending = true,
                isDealOfDay = true,
                isFlashSale = false,
                description = "Immerse in studio-grade audio with 45dB Active Noise Cancellation, 11mm Beryllium drivers, ultra-low 38ms latency gaming mode, and dual-mic AI ENC for crystal clear Indian voice calls in traffic.",
                highlightsJson = "45dB Hybrid Active Noise Cancellation (ANC) | 48 Hours Massive Battery Backup with Fast Charge (10min = 5hrs) | 11mm Hi-Res Audio Beryllium Dynamic Drivers | Quad AI-Mics with Environmental Noise Cancellation | IPX5 Sweat & Water Resistant",
                specsJson = "Bluetooth:v5.4 with LDAC & AAC codec;Battery:Case 500mAh, Earbuds 50mAh;Water Resistance:IPX5;Charging:Type-C Fast Charge;Controls:Smart Capacitive Touch Controls;Warranty:1 Year Replacement Warranty",
                color = "Obsidian Black",
                size = "Standard Fit",
                warranty = "1 Year Brand Replacement",
                returnPolicyDays = 7,
                deliveryDays = 2,
                imageCategory = "electronics"
            ),
            Product(
                id = "hero-3",
                sku = "SN-FASH-KUR1",
                title = "UrbanNova Pure Handloom Cotton Men's Mandarin Collar Casual Shirt",
                brand = "UrbanNova",
                category = "Fashion",
                subcategory = "Men's Shirts",
                price = 799.0,
                mrp = 1999.0,
                discountPercent = 60,
                rating = 4.4f,
                ratingCount = 4320,
                stock = 60,
                isAssured = true,
                isTrending = true,
                isDealOfDay = false,
                isFlashSale = true,
                description = "Crafted from 100% breathable natural combed cotton, this mandarin collar casual shirt offers unmatched all-day comfort in tropical Indian weather, perfect for both festive occasions and weekend outings.",
                highlightsJson = "100% Breathable Combed Cotton Fabric | Modern Mandarin Chinese Collar Style | Regular Slim Ergonomic Fit | Pre-shrunk Fabric with Anti-fade Dyeing | Machine Wash Friendly",
                specsJson = "Fabric:100% Cotton;Pattern:Solid Slub Weave;Sleeve:Full Sleeve with Roll-up Tab;Fit:Regular Slim Fit;Occasion:Casual & Semi-Formal;Wash Care:Gentle Machine Wash",
                color = "Classic White",
                size = "L (40)",
                warranty = "Quality Assurance Guaranteed",
                returnPolicyDays = 7,
                deliveryDays = 2,
                imageCategory = "fashion"
            ),
            Product(
                id = "hero-4",
                sku = "SN-HOME-MIX1",
                title = "ChefNova 1000W Heavy Duty Copper Motor Mixer Grinder (4 Jars)",
                brand = "ChefNova",
                category = "Home & Kitchen",
                subcategory = "Mixer Grinders",
                price = 3499.0,
                mrp = 7499.0,
                discountPercent = 53,
                rating = 4.8f,
                ratingCount = 12100,
                stock = 34,
                isAssured = true,
                isTrending = true,
                isDealOfDay = true,
                isFlashSale = true,
                description = "Built for tough Indian kitchen grinding tasks like turmeric, soaked lentils, and thick dosa batters. Powered by a 100% pure copper 1000W motor with dynamic air ventilation and 304 food-grade stainless steel jars.",
                highlightsJson = "1000W Ultra-Powerful Pure Copper Winded Motor | 4 Heavy-Gauge 304 Grade Stainless Steel Jars | Specialized Dura-Blades for Hard Turmeric Grinding | Overload Protector with Tri-Vent Cooling | 5 Years Motor Warranty",
                specsJson = "Power:1000 Watts;Motor:10000 RPM Pure Copper;Jars:1.5L Wet, 1.0L Dry, 0.4L Chutney, 1.5L Juicer Extractor;Speed:3 Speed with Incher Pulse;Safety:Automatic Overload Trip Switch;Warranty:2 Years Comprehensive + 5 Years Motor",
                color = "Royal Crimson",
                size = "4 Jar Set",
                warranty = "5 Years Motor Warranty",
                returnPolicyDays = 7,
                deliveryDays = 1,
                imageCategory = "home_kitchen"
            ),
            Product(
                id = "hero-5",
                sku = "SN-BEAU-SER1",
                title = "GlowNova 10% Vitamin C & Niacinamide Radiance Face Serum (30ml)",
                brand = "GlowNova",
                category = "Beauty",
                subcategory = "Face Serums",
                price = 499.0,
                mrp = 999.0,
                discountPercent = 50,
                rating = 4.6f,
                ratingCount = 8760,
                stock = 85,
                isAssured = true,
                isTrending = true,
                isDealOfDay = false,
                isFlashSale = false,
                description = "Dermatologically formulated for Indian skin types exposed to sun and pollution. Combines stable Ethyl Ascorbic Acid with Niacinamide and Hyaluronic Acid to fade dark spots and boost natural youthful radiance.",
                highlightsJson = "10% Active Vitamin C + 5% Niacinamide Complex | Fades Sun Spots, Hyper-pigmentation and Acne Marks | Infused with Hyaluronic Acid for 24h Deep Hydration | 100% Cruelty-Free, Paraben-Free, Sulfate-Free | Dermatologically Tested for Sensitive Skin",
                specsJson = "Volume:30 ml;Skin Type:All Skin Types;Key Ingredients:Vitamin C, Niacinamide, Hyaluronic Acid, Centella Asiatica;Form:Quick-Absorbing Lightweight Gel Serum;Shelf Life:24 Months;Origin:Formulated in India",
                color = "Clear Amber",
                size = "30 ml",
                warranty = "100% Authentic Guarantee",
                returnPolicyDays = 7,
                deliveryDays = 2,
                imageCategory = "beauty"
            )
        )
    }

    fun getDefaultCoupons(): List<Coupon> {
        return listOf(
            Coupon("WELCOME100", 20, 100.0, 499.0, "Flat ₹100 Off on your first Shopnova order above ₹499"),
            Coupon("SUPERNOVA20", 20, 500.0, 1499.0, "Get 20% Off up to ₹500 on all Electronics and Fashion"),
            Coupon("FESTIVE500", 15, 1000.0, 2999.0, "Mega Festive Savings: Flat 15% Off up to ₹1,000"),
            Coupon("FREESHIP", 100, 50.0, 0.0, "Zero Delivery Fee on any order today")
        )
    }

    fun getDefaultAddresses(): List<Address> {
        return listOf(
            Address(
                id = 1,
                fullName = "Praveen Kumar",
                phone = "9876543210",
                altPhone = "9876500000",
                houseNo = "Flat 402, Royal Palms Heights",
                area = "Koramangala 4th Block, 80 Feet Road",
                landmark = "Near Sony World Signal",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560034",
                addressType = "Home",
                isDefault = true
            ),
            Address(
                id = 2,
                fullName = "Praveen Kumar",
                phone = "9876543210",
                altPhone = "",
                houseNo = "Tech Park Tower B, 6th Floor",
                area = "Electronic City Phase 1",
                landmark = "Opposite Infosys Gate 1",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560100",
                addressType = "Work",
                isDefault = false
            )
        )
    }

    fun getDefaultReviews(productId: String): List<Review> {
        return listOf(
            Review(
                productId = productId,
                customerName = "Rahul Sharma",
                rating = 5,
                title = "Outstanding quality and lightning fast delivery!",
                comment = "Ordered this during the flash sale and received it within 24 hours in Bengaluru. The build quality is top-notch and completely matches the specifications. Shopnova packaging was super secure with tamper-proof seal.",
                isVerifiedBuyer = true,
                helpfulCount = 38
            ),
            Review(
                productId = productId,
                customerName = "Ananya Iyer",
                rating = 5,
                title = "Total value for money, highly recommended",
                comment = "I was hesitant initially but the product exceeded my expectations! Looks very premium, finishing is great. Customer support was also very responsive when I inquired about warranty registration.",
                isVerifiedBuyer = true,
                helpfulCount = 24
            ),
            Review(
                productId = productId,
                customerName = "Vikramaditya Rao",
                rating = 4,
                title = "Great purchase, minor feedback on packaging",
                comment = "Product works flawlessly and is original. The battery and performance are exactly as advertised. Giving 4 stars only because courier reached in the evening instead of morning slot. Overall 100% satisfied.",
                isVerifiedBuyer = true,
                helpfulCount = 12
            )
        )
    }

    fun getDefaultNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                title = "Mega Supernova Sale is Live! 🎉",
                message = "Grab up to 70% off on Smartphones, Laptops, Kitchenware and Fashion brands today with extra 10% instant bank discounts.",
                type = "OFFER"
            ),
            NotificationItem(
                title = "Welcome to Shopnova India 🛍️",
                message = "Use coupon code WELCOME100 to get instant ₹100 off on your first purchase above ₹499.",
                type = "SYSTEM"
            )
        )
    }
}
