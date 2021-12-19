package com.example.data.tables

import org.ktorm.schema.*

object ProductEntity:Table<Nothing>("Product") {

    val productId =  int("productId").primaryKey()
    val categoryId =  int("categoryId")
    val restaurantId =  int("restaurantId")
    val productName = varchar("productName")
    val productPrice = double("productPrice")
    val freeDelivery = boolean("freeDelivery")
    val createAt = long("createAt")
    val productDescription = varchar("productDescription")
}