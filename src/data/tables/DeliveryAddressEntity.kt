package com.example.data.tables

import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int

object DeliveryAddressEntity : Table<Nothing>("DeliveryAddress") {

    val deliveryAddressId =  int("deliveryAddressId").primaryKey()
    val userId =  int("userId")
    val latitude =  double("latitude")
    val longitude = double("longitude")
}