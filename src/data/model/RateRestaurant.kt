package com.example.data.model

import com.example.data.tables.RateRestaurantEntity.primaryKey
import kotlinx.serialization.Serializable
import org.ktorm.schema.int

@Serializable
data class RateRestaurant(
    var rateId :Int?=null,
    var userId :Int?=null,
    val restaurantId :Int,
    val countRate:Double,
    val createAt:Long
)
