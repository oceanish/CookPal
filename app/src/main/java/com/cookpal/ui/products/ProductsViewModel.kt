package com.cookpal.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cookpal.data.local.entity.ProductEntity
import com.cookpal.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<ProductEntity> = emptyList(),
    val showAddDialog: Boolean = false,
    val newProductName: String = "",
    val newProductCategory: String = "",
    val newProductQuantity: String = "1",
    val newProductExpiry: String = ""
)

class ProductsViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllProducts().collect { products ->
                _uiState.update { it.copy(products = products) }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(newProductName = name) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(newProductCategory = category) }
    }

    fun updateQuantity(quantity: String) {
        _uiState.update { it.copy(newProductQuantity = quantity) }
    }

    fun updateExpiry(expiry: String) {
        _uiState.update { it.copy(newProductExpiry = expiry) }
    }

    fun addProduct() {
        val state = _uiState.value
        if (state.newProductName.isBlank()) return
        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    name = state.newProductName.trim(),
                    category = state.newProductCategory.trim(),
                    quantity = state.newProductQuantity.trim(),
                    expiryDate = state.newProductExpiry.trim().ifBlank { null }
                )
            )
        }
        _uiState.update {
            it.copy(
                showAddDialog = false,
                newProductName = "",
                newProductCategory = "",
                newProductQuantity = "1",
                newProductExpiry = ""
            )
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }
}

class ProductsViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ProductsViewModel(repository) as T
}
