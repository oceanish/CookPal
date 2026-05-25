package com.cookpal.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 20
    ): RecipeSearchResponse

    @GET("/recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Long,
        @Query("apiKey") apiKey: String
    ): RecipeInfoResponse
}
