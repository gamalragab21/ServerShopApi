package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FavRestaurant(
    val favRestaurantId: Int? = null,
    val restaurantId: Int,
    var userId: Int? = null
)