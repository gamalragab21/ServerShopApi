package com.example.data.tables

import org.ktorm.schema.*

object ProductImageEntity:Table<Nothing>("ProductImage") {

    val productImageId =  int("productImageId").primaryKey()
    val productId =  int("productId")
    val imageProduct = varchar("imageProduct")
}