package com.cookpal.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cookpal.data.local.entity.FavoriteRecipeEntity
import com.cookpal.data.local.entity.ProductEntity
import com.cookpal.data.local.entity.ShoppingItemEntity
import com.cookpal.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favorites: List<FavoriteRecipeEntity> = emptyList(),
    val shoppingItems: List<ShoppingItemEntity> = emptyList(),
    val selectedTab: Int = 0
)

class FavoritesViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllFavorites().collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
        viewModelScope.launch {
            repository.getAllShoppingItems().collect { items ->
                _uiState.update { it.copy(shoppingItems = items) }
            }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun deleteFavorite(recipe: FavoriteRecipeEntity) {
        viewModelScope.launch { repository.deleteFavorite(recipe) }
    }

    fun togglePurchased(item: ShoppingItemEntity) {
        viewModelScope.launch {
            repository.updateShoppingItem(item.copy(isPurchased = !item.isPurchased))
        }
    }

    fun deleteShoppingItem(item: ShoppingItemEntity) {
        viewModelScope.launch { repository.deleteShoppingItem(item) }
    }

    fun movePurchasedToProducts() {
        viewModelScope.launch {
            val purchased = _uiState.value.shoppingItems.filter { it.isPurchased }
            purchased.forEach { item ->
                repository.addProduct(
                    ProductEntity(
                        name = item.ingredientName,
                        quantity = item.quantity,
                        category = "Покупки",
                        expiryDate = null,
                        isAvailable = true
                    )
                )
            }
            repository.deletePurchasedItems()
        }
    }
}

class FavoritesViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FavoritesViewModel(repository) as T
}
