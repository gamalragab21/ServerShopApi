package com.example.data.tables

import org.ktorm.schema.*

object FavRestaurantEntity:Table<Nothing>("FavRestaurant") {

    val favRestaurantId =  int("favRestaurantId").primaryKey()
    val restaurantId =  int("restaurantId")
    val userId = int("userId")
}