package com.cookpal.data.remote

import com.squareup.moshi.Json

data class RecipeSearchResponse(
    @Json(name = "results") val results: List<RecipeSummary>,
    @Json(name = "totalResults") val totalResults: Int,
)

data class RecipeSummary(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
)
