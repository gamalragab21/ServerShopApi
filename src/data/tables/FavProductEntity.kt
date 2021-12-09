package com.example.data.tables

import org.ktorm.schema.*

object FavProductEntity:Table<Nothing>("FavProduct") {

    val favProductId =  int("favProductId").primaryKey()
    val productId =  int("productId")
    val userId = int("userId")
}