package com.cookpal.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cookpal.data.remote.RecipeSummary
import com.cookpal.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val suggestions: List<String> = emptyList(),
    val results: List<RecipeSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SearchViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.length >= 2) {
            viewModelScope.launch {
                val names = repository.getAvailableProductNames()
                _uiState.update {
                    it.copy(suggestions = names.filter { n ->
                        n.contains(query, ignoreCase = true)
                    })
                }
            }
        } else {
            _uiState.update { it.copy(suggestions = emptyList()) }
        }
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, suggestions = emptyList()) }
            try {
                val results = repository.searchRecipes(query)
                _uiState.update { it.copy(results = results, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Ошибка запроса")
                }
            }
        }
    }

    fun clearResults() {
        _uiState.update { it.copy(results = emptyList(), query = "", error = null) }
    }
}

class SearchViewModelFactory(
    private val repository: RecipeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SearchViewModel(repository) as T
}
