package com.cookpal

import com.cookpal.data.remote.RecipeSummary
import com.cookpal.data.repository.RecipeRepository
import com.cookpal.ui.search.SearchViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {

    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        coEvery { repository.getAvailableProductNames() } returns listOf("Молоко", "Мясо", "Мука")
        coEvery { repository.searchRecipes(any()) } returns listOf(
            RecipeSummary(id = 1, title = "Борщ", image = ""),
            RecipeSummary(id = 2, title = "Суп", image = "")
        )

        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateQuery with short query clears suggestions`() = runTest(testDispatcher) {
        viewModel.updateQuery("М")
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.suggestions.isEmpty())
    }

    @Test
    fun `updateQuery with long query shows suggestions`() = runTest(testDispatcher) {
        viewModel.updateQuery("Мол")
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.suggestions.contains("Молоко"))
    }

    @Test
    fun `search updates results`() = runTest(testDispatcher) {
        viewModel.updateQuery("Борщ")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.results.size == 2)
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `search with empty query does nothing`() = runTest(testDispatcher) {
        viewModel.search()
        assert(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `clearResults resets state`() = runTest(testDispatcher) {
        viewModel.updateQuery("Борщ")
        viewModel.search()
        viewModel.clearResults()
        assert(viewModel.uiState.value.query.isEmpty())
        assert(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `search handles error gracefully`() = runTest(testDispatcher) {
        coEvery { repository.searchRecipes(any()) } throws RuntimeException("Network error")
        viewModel.updateQuery("Борщ")
        viewModel.search()
        testDispatcher.scheduler.advanceUntilIdle()
        assert(viewModel.uiState.value.error != null)
        assert(!viewModel.uiState.value.isLoading)
    }
}
