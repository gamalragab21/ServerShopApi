package com.example.data.tables

import org.ktorm.schema.*

object RestaurantEntity:Table<Nothing>("Restaurant") {

    val restaurantId =  int("restaurantId").primaryKey()
    val restaurantName = varchar("restaurantName")
    val imageRestaurant = varchar("imageRestaurant")
    val email = varchar("email")
    val hashPassword = varchar("hashPassword")
    val contact = varchar("contact")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val createAt = int("createAt")
    val restaurantType = varchar("restaurantType")
    val freeDelivery = boolean("freeDelivery")
}