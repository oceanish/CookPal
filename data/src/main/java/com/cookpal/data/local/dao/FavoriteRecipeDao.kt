package com.cookpal.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cookpal.data.local.entity.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface FavoriteRecipeDao {
    @Query("SELECT * FROM favorite_recipes ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(recipe: FavoriteRecipeEntity): Long

    @Delete
    suspend fun deleteFavorite(recipe: FavoriteRecipeEntity)

    @Query("SELECT * FROM favorite_recipes WHERE apiRecipeId = :apiRecipeId LIMIT 1")
    suspend fun getFavoriteByApiId(apiRecipeId: Long): FavoriteRecipeEntity?
}
