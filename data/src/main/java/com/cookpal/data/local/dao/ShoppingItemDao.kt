package com.cookpal.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cookpal.data.local.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items ORDER BY isPurchased ASC, ingredientName ASC")
    fun getAllItems(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShoppingItemEntity>)

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)

    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE isPurchased = 1")
    suspend fun deletePurchasedItems()
}
