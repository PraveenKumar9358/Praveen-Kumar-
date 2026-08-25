package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.seed.ProductSeedGenerator
import com.example.ui.components.ProductGridCard
import com.example.ui.theme.ShopnovaTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun shopnova_product_card_screenshot() {
        val sampleProduct = ProductSeedGenerator.generateInitialProducts().first()
        composeTestRule.setContent {
            ShopnovaTheme {
                ProductGridCard(
                    product = sampleProduct,
                    isWishlisted = true,
                    onProductClick = {},
                    onToggleWishlist = {},
                    onAddToCart = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/shopnova_card.png")
    }
}
