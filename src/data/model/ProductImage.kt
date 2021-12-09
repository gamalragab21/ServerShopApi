package com.example.data.model

import com.example.data.tables.ProductImageEntity.primaryKey
import kotlinx.serialization.Serializable
import org.ktorm.schema.int

@Serializable
data class ProductImage (
    val productImageId :Int?=-1,
    val productId :Int?=-1,
    val imageProduct:String
        )