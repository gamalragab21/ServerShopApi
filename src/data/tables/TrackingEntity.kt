package com.example.data.tables

import com.example.data.tables.UserEntity.primaryKey
import org.ktorm.schema.*

object TrackingEntity : Table<Nothing>("Tracking") {
    val trackingId = int("trackingId").primaryKey()
    val userId = int("userId")
    val status = int("status")
    val orderId = int("orderId")
    val restaurantId = int("restaurantId")
    val createAt = long("createAt")
}