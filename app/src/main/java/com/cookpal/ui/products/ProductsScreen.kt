package com.cookpal.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cookpal.CookPalApp
import com.cookpal.data.local.entity.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as CookPalApp
    val viewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModelFactory(app.container.recipeRepository)
    )
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мои продукты") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog() }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить продукт")
            }
        }
    ) { padding ->
        if (state.products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Продуктов пока нет", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.products, key = { it.id }) { product ->
                    ProductItem(
                        product = product,
                        onDelete = { viewModel.deleteProduct(product) }
                    )
                }
            }
        }
    }

    if (state.showAddDialog) {
        AddProductDialog(
            name = state.newProductName,
            category = state.newProductCategory,
            quantity = state.newProductQuantity,
            expiry = state.newProductExpiry,
            onNameChange = viewModel::updateName,
            onCategoryChange = viewModel::updateCategory,
            onQuantityChange = viewModel::updateQuantity,
            onExpiryChange = viewModel::updateExpiry,
            onConfirm = viewModel::addProduct,
            onDismiss = viewModel::hideAddDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductItem(
    product: ProductEntity,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            if (value == androidx.compose.material3.DismissValue.DismissedToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissContent = {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Row {
                            Text(
                                product.category,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "\u00D7${product.quantity}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        product.expiryDate?.let {
                            Text(
                                "Годен до: $it",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AddProductDialog(
    name: String,
    category: String,
    quantity: String,
    expiry: String,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onExpiryChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить продукт") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Название") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = onCategoryChange,
                    label = { Text("Категория") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Количество") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = expiry,
                    onValueChange = onExpiryChange,
                    label = { Text("Срок годности") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Добавить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
