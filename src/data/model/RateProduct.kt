package com.example.data.model

import com.example.data.tables.RateRestaurantEntity.primaryKey
import kotlinx.serialization.Serializable
import org.ktorm.schema.int

@Serializable
data class RateProduct(
    var rateId :Int?=null,
    var userId :Int?=null,
    val productId :Int,
    val countRate:Double,
    val messageRate:String,
    val createAt:Int,
    var user:User?=null
)
