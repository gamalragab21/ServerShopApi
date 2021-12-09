package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FavProduct(
    val favProductId: Int,
    val productId: Int,
    var userId: Int? = null
)