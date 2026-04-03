package com.project.deliciousdishes.model

import java.io.Serializable

class RecipeModel : Serializable {

    var strCategory: String? = null

    var idMeal: String? = null

    @JvmField
    var strMeal: String? = null

    @JvmField
    var strMealThumb: String? = null

}