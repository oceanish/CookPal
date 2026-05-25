package com.cookpal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val apiRecipeId: Long,
    val title: String,
    val imageUrl: String,
    val ingredientsJson: String,
    val instructionsJson: String
)
