package com.project.deliciousdishes.model

import java.io.Serializable

class CategoryModel : Serializable {

    @JvmField
    var strCategory: String? = null

    @JvmField
    var strCategoryThumb: String? = null

    var strCategoryDescription: String? = null

}