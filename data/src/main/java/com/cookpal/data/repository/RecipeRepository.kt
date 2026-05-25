package com.cookpal.data.repository

import android.util.Log
import com.cookpal.data.local.dao.FavoriteRecipeDao
import com.cookpal.data.local.dao.ProductDao
import com.cookpal.data.local.dao.ShoppingItemDao
import com.cookpal.data.local.entity.FavoriteRecipeEntity
import com.cookpal.data.local.entity.ProductEntity
import com.cookpal.data.local.entity.ShoppingItemEntity
import com.cookpal.data.remote.RecipeInfoResponse
import com.cookpal.data.remote.RecipeSummary
import com.cookpal.data.remote.SpoonacularApi
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val api: SpoonacularApi,
    private val productDao: ProductDao,
    private val favoriteDao: FavoriteRecipeDao,
    private val shoppingDao: ShoppingItemDao
) {
    private val apiKey: String
        get() {
            val key = System.getProperty("SPOONACULAR_API_KEY")
                    ?: System.getenv("SPOONACULAR_API_KEY")
                    ?: ""
            // Log for debugging (do not log the actual key in production)
            if (key.isNotEmpty()) {
                Log.d("RecipeRepository", "API key loaded (length: ${key.length})")
            } else {
                Log.w("RecipeRepository", "API key is empty!")
            }
            return key
        }

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun addProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    suspend fun getAvailableProductNames(): List<String> =
        productDao.getAvailableProductNames()

    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>> =
        favoriteDao.getAllFavorites()

    suspend fun addFavorite(recipe: FavoriteRecipeEntity) {
        favoriteDao.insertFavorite(recipe)
    }

    suspend fun deleteFavorite(recipe: FavoriteRecipeEntity) {
        favoriteDao.deleteFavorite(recipe)
    }

    suspend fun isFavorite(apiRecipeId: Long): Boolean =
        favoriteDao.getFavoriteByApiId(apiRecipeId) != null

    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>> =
        shoppingDao.getAllItems()

    suspend fun addShoppingItem(item: ShoppingItemEntity) {
        shoppingDao.insertItem(item)
    }

    suspend fun addShoppingItems(items: List<ShoppingItemEntity>) {
        shoppingDao.insertItems(items)
    }

    suspend fun updateShoppingItem(item: ShoppingItemEntity) {
        shoppingDao.updateItem(item)
    }

    suspend fun deleteShoppingItem(item: ShoppingItemEntity) {
        shoppingDao.deleteItem(item)
    }

    suspend fun deletePurchasedItems() {
        shoppingDao.deletePurchasedItems()
    }

    suspend fun searchRecipes(query: String): List<RecipeSummary> {
        Log.d("RecipeRepository", "Searching for recipes with query: $query")
        Log.d("RecipeRepository", "API key length: ${apiKey.length}")
        try {
            val response = api.searchRecipes(query, apiKey)
            Log.d("RecipeRepository", "Search successful, got ${response.results.size} results")
            return response.results
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Search failed: ${e.message}", e)
            throw e
        }
    }

    suspend fun getRecipeInfo(id: Long): RecipeInfoResponse {
        return api.getRecipeInformation(id, apiKey)
    }
}
