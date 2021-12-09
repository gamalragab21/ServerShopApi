package com.example.data.model

import com.example.data.tables.CategoryEntity.primaryKey
import kotlinx.serialization.Serializable
import org.ktorm.schema.int

@Serializable
data class Category(
    val categoryId :Int?=-1,
    var restaurantId :Int?=null,
    val categoryName: String
)
