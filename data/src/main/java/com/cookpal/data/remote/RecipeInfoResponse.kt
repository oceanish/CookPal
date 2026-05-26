package com.cookpal.data.remote

import com.squareup.moshi.Json

data class RecipeInfoResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "image") val image: String,
    @Json(name = "extendedIngredients") val extendedIngredients: List<Ingredient>,
    @Json(name = "instructions") val instructions: String?,
    @Json(name = "analyzedInstructions") val analyzedInstructions: List<AnalyzedInstruction>?,
)

data class Ingredient(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "unit") val unit: String?,
    @Json(name = "original") val original: String?,
)

data class AnalyzedInstruction(
    @Json(name = "steps") val steps: List<Step>
)

data class Step(
    @Json(name = "number") val number: Int,
    @Json(name = "step") val step: String
)
