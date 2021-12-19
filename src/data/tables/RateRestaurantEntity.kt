package com.example.data.tables

import org.ktorm.schema.*

object RateRestaurantEntity:Table<Nothing>("RateRestaurant") {

    val rateId =  int("rateId").primaryKey()
    val userId =  int("userId")
    val restaurantId =  int("restaurantId")
    val countRate =  double("countRate")
    val createAt =  int("createAt")
}