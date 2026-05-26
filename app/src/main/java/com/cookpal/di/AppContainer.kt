package com.cookpal.di

import android.content.Context
import com.cookpal.BuildConfig
import com.cookpal.data.local.CookPalDatabase
import com.cookpal.data.remote.SpoonacularApi
import com.cookpal.data.repository.RecipeRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {
    private val database = CookPalDatabase.getDatabase(context)

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor: Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val spoonacularApi = retrofit.create(SpoonacularApi::class.java)

    val recipeRepository = RecipeRepository(
        api = spoonacularApi,
        productDao = database.productDao(),
        favoriteDao = database.favoriteRecipeDao(),
        shoppingDao = database.shoppingItemDao(),
        apiKey = BuildConfig.SPOONACULAR_API_KEY,
    )
}
