package com.cookpal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cookpal.data.local.dao.FavoriteRecipeDao
import com.cookpal.data.local.dao.ProductDao
import com.cookpal.data.local.dao.ShoppingItemDao
import com.cookpal.data.local.entity.FavoriteRecipeEntity
import com.cookpal.data.local.entity.ProductEntity
import com.cookpal.data.local.entity.ShoppingItemEntity

@Database(
    entities = [ProductEntity::class, FavoriteRecipeEntity::class, ShoppingItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CookPalDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun shoppingItemDao(): ShoppingItemDao

    companion object {
        @Volatile
        private var INSTANCE: CookPalDatabase? = null

        fun getDatabase(context: Context): CookPalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CookPalDatabase::class.java,
                    "cookpal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
