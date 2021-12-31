package com.example.data.tables

import com.example.data.tables.ProductEntity.primaryKey
import org.ktorm.schema.*

object OrderEntity : Table<Nothing>("Orders") {


    val orderId = int("orderId").primaryKey()
    val userId = int("userId")
    val restaurantId = int("restaurantId")
    val foodId = int("foodId")
    val orderType = int("orderType")
    val productName = varchar("productName")
    val productPrice = double("productPrice")
    val productDistCount = double("productDistCount")
    val productQuantity = int("productQuantity")
    val freeDelivery = boolean("freeDelivery")
    val createAt = long("createAt")
    val coinType = varchar("coinType")
    val foodImage = varchar("foodImage")
}