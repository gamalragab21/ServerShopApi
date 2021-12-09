package com.example.data.model

import com.example.data.tables.DeliveryAddressEntity.primaryKey
import org.ktorm.schema.double
import org.ktorm.schema.int

data class DeliveryAddress (
    val deliveryAddressId:Int?=-1,
    val userId :Int,
    val latitude :Double,
    val longitude:Double,
        )