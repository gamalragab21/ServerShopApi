package com.example.data.tables

import org.ktorm.schema.*

object RateProductEntity:Table<Nothing>("RateProduct") {

    val rateId =  int("rateId").primaryKey()
    val userId =  int("userId")
    val productId =  int("productId")
    val countRate =  double("countRate")
    val messageRate =  varchar("messageRate")
    val createAt =  long("createAt")
}