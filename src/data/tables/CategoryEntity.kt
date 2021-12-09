package com.example.data.tables

import org.ktorm.schema.*

object CategoryEntity:Table<Nothing>("Category") {

    val categoryId =  int("categoryId").primaryKey()
    val restaurantId =  int("restaurantId")
    val categoryName = varchar("categoryName")
}