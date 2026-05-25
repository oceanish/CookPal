package com.cookpal

import com.cookpal.data.local.entity.ProductEntity
import com.cookpal.data.repository.RecipeRepository
import com.cookpal.ui.products.ProductsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ProductsViewModelTest {

    private lateinit var repository: RecipeRepository
    private lateinit var viewModel: ProductsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        coEvery { repository.getAllProducts() } returns MutableStateFlow(emptyList())

        viewModel = ProductsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `showAddDialog updates state`() = runTest(testDispatcher) {
        viewModel.showAddDialog()
        assert(viewModel.uiState.value.showAddDialog)
    }

    @Test
    fun `hideAddDialog updates state`() = runTest(testDispatcher) {
        viewModel.showAddDialog()
        viewModel.hideAddDialog()
        assert(!viewModel.uiState.value.showAddDialog)
    }

    @Test
    fun `updateName changes newProductName`() = runTest(testDispatcher) {
        viewModel.updateName("Молоко")
        assert(viewModel.uiState.value.newProductName == "Молоко")
    }

    @Test
    fun `addProduct with blank name does not call repository`() = runTest(testDispatcher) {
        viewModel.addProduct()
        coVerify(exactly = 0) { repository.addProduct(any()) }
    }

    @Test
    fun `addProduct with valid name calls repository`() = runTest(testDispatcher) {
        viewModel.updateName("Молоко")
        viewModel.updateCategory("Молочные")
        viewModel.addProduct()

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { repository.addProduct(any()) }
        assert(!viewModel.uiState.value.showAddDialog)
    }

    @Test
    fun `deleteProduct calls repository`() = runTest(testDispatcher) {
        val product = ProductEntity(
            id = 1,
            name = "Хлеб",
            category = "Выпечка",
            quantity = "1",
            expiryDate = null
        )
        viewModel.deleteProduct(product)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { repository.deleteProduct(product) }
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assert(state.products.isEmpty())
        assert(!state.showAddDialog)
        assert(state.newProductName.isEmpty())
    }
}
