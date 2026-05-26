package com.cookpal.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cookpal.CookPalApp
import com.cookpal.data.local.entity.FavoriteRecipeEntity
import com.cookpal.data.local.entity.ShoppingItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesAndShoppingScreen(initialTab: Int = 0) {
    val app = LocalContext.current.applicationContext as CookPalApp
    val viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModelFactory(app.container.recipeRepository),
    )
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Избранное и покупки") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("Избранное") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Список покупок") }
                )
            }

            when (state.selectedTab) {
                0 -> FavoritesTab(
                    favorites = state.favorites,
                    onDelete = viewModel::deleteFavorite
                )
                1 -> ShoppingTab(
                    items = state.shoppingItems,
                    onToggle = viewModel::togglePurchased,
                    onDelete = viewModel::deleteShoppingItem,
                    onMovePurchased = viewModel::movePurchasedToProducts
                )
            }
        }
    }
}

@Composable
private fun FavoritesTab(
    favorites: List<FavoriteRecipeEntity>,
    onDelete: (FavoriteRecipeEntity) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет избранных рецептов", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites, key = { it.id }) { recipe ->
                FavoriteItem(recipe = recipe) {
                    onDelete(recipe)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteItem(
    recipe: FavoriteRecipeEntity,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
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
        content = {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(recipe.imageUrl)
                            .crossfade(enable = true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
private fun ShoppingTab(
    items: List<ShoppingItemEntity>,
    onToggle: (ShoppingItemEntity) -> Unit,
    onDelete: (ShoppingItemEntity) -> Unit,
    onMovePurchased: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (items.any { it.isPurchased }) {
            Button(
                onClick = onMovePurchased,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Перенести купленное в продукты")
            }
        }

        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Список покупок пуст",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    ShoppingItemRow(
                        item = item,
                        onToggle = { onToggle(item) },
                        onDelete = { onDelete(item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingItemRow(
    item: ShoppingItemEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
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
        content = {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isPurchased,
                        onCheckedChange = { onToggle() }
                    )
                    Text(
                        text = item.ingredientName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "\u00D7${item.quantity}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}
