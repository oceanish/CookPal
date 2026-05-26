package com.cookpal

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.cookpal.CookPalApp
import com.cookpal.di.AppContainer
import com.cookpal.ui.products.ProductsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductsScreenTest {

    @get:Rule
    val androidComposeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        // No mocking needed for now
    }

    @Test
    fun productsScreen_showsEmptyMessage() {
        androidComposeRule.setContent {
            MaterialTheme {
                ProductsScreen(navController = TestNavHostController(
                    androidComposeRule.activity
                ))
            }
        }

        androidComposeRule.onNodeWithText("Продуктов пока нет").assertExists()
    }

    @Test
    fun productsScreen_showsFab() {
        androidComposeRule.setContent {
            MaterialTheme {
                ProductsScreen(navController = TestNavHostController(
                    androidComposeRule.activity
                ))
            }
        }

        androidComposeRule.onNodeWithText("Мои продукты").assertExists()
    }
}