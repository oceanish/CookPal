package com.cookpal.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cookpal.data.local.entity.FavoriteRecipeEntity
import com.cookpal.data.local.entity.ShoppingItemEntity
import com.cookpal.data.remote.RecipeInfoResponse
import com.cookpal.data.repository.RecipeRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val recipe: RecipeInfoResponse? = null,
    val isLoading: Boolean = false,
    val isFavorite: Boolean = false,
    val missingIngredients: List<String> = emptyList(),
    val addedToShoppingList: Boolean = false,
    val error: String? = null
)

class DetailViewModel(
    private val repository: RecipeRepository,
    private val recipeId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    init {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val recipe = repository.getRecipeInfo(recipeId)
                val isFav = repository.isFavorite(recipeId)
                val productNames = repository.getAvailableProductNames()
                val missing = recipe.extendedIngredients
                    ?.filter { ing ->
                        productNames.none { p ->
                            p.lowercase() == ing.name.lowercase()
                        }
                    }
                    ?.mapNotNull { it.original ?: it.name }
                    ?: emptyList()

                _uiState.update {
                    it.copy(
                        recipe = recipe,
                        isLoading = false,
                        isFavorite = isFav,
                        missingIngredients = missing
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки")
                }
            }
        }
    }

    fun toggleFavorite() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                repository.deleteFavoriteByApiId(recipeId)
                _uiState.update { it.copy(isFavorite = false) }
            } else {
                val ingredientsJson = try {
                    val listType = Types.newParameterizedType(List::class.java, String::class.java)
                    moshi.adapter<List<String>>(listType).toJson(
                        recipe.extendedIngredients.map { it.name }
                    )
                } catch (_: Exception) { "" }

                val instructionsJson = recipe.instructions ?: ""
                repository.addFavorite(
                    FavoriteRecipeEntity(
                        apiRecipeId = recipeId,
                        title = recipe.title,
                        imageUrl = recipe.image,
                        ingredientsJson = ingredientsJson,
                        instructionsJson = instructionsJson
                    )
                )
                _uiState.update { it.copy(isFavorite = true) }
            }
        }
    }

    fun addMissingToShoppingList() {
        val missing = _uiState.value.missingIngredients
        if (missing.isEmpty()) return
        viewModelScope.launch {
            val items = missing.map { name ->
                ShoppingItemEntity(
                    ingredientName = name,
                    quantity = "1",
                    isPurchased = false
                )
            }
            repository.addShoppingItems(items)
            _uiState.update { it.copy(addedToShoppingList = true) }
        }
    }
}

class DetailViewModelFactory(
    private val repository: RecipeRepository,
    private val recipeId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        DetailViewModel(repository, recipeId) as T
}
