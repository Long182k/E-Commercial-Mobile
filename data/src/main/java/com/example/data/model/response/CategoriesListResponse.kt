package com.example.data.model.response

import com.example.data.model.CategoryDataModel
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesListResponse(
    val `data`: List<CategoryDataModel>,
    val msg: String
) {
    fun toCategoriesList() = com.example.domain.model.CategoriesListModel(
        categories = `data`.map { it.toCategory() },
        msg = msg
    )
}