package com.cookpal

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cookpal.ui.products.ProductsScreen
import com.cookpal.ui.theme.CookPalTheme
import org.junit.Rule
import org.junit.Test

class ProductsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productsScreen_showsEmptyMessage() {
        composeTestRule.setContent {
            CookPalTheme {
                ProductsScreen(navController = androidx.navigation.testing.TestNavHostController(
                    androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
                ))
            }
        }

        composeTestRule.onNodeWithText("Продуктов пока нет").assertExists()
    }

    @Test
    fun productsScreen_showsFab() {
        composeTestRule.setContent {
            CookPalTheme {
                ProductsScreen(navController = androidx.navigation.testing.TestNavHostController(
                    androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
                ))
            }
        }

        composeTestRule.onNodeWithText("Мои продукты").assertExists()
    }
}
