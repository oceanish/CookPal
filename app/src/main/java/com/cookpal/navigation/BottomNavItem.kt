package com.cookpal.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    data object Products : BottomNavItem("products", "Мои продукты", Icons.Default.Home)
    data object Search : BottomNavItem("search", "Поиск рецептов", Icons.Default.Search)
    data object Favorites : BottomNavItem("favorites", "Избранное", Icons.Default.Favorite)
    data object Shopping : BottomNavItem("shopping", "Список покупок", Icons.Default.ShoppingCart)
}
