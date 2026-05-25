package com.cookpal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cookpal.ui.detail.DetailScreen
import com.cookpal.ui.favorites.FavoritesAndShoppingScreen
import com.cookpal.ui.products.ProductsScreen
import com.cookpal.ui.search.SearchScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "products") {
        composable("products") {
            ProductsScreen(navController = navController)
        }
        composable("search") {
            SearchScreen(navController = navController)
        }
        composable("favorites") {
            FavoritesAndShoppingScreen(initialTab = 0)
        }
        composable("shopping") {
            FavoritesAndShoppingScreen(initialTab = 1)
        }
        composable(
            route = "detail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: return@composable
            DetailScreen(recipeId = recipeId, navController = navController)
        }
    }
}
