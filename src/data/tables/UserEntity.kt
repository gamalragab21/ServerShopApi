package com.example.data.tables

import org.ktorm.schema.*

object UserEntity:Table<Nothing>("users") {
    val userId =  int("userId").primaryKey()
    val userName = varchar("userName")
    val imageProfile = varchar("imageProfile")
    val email = varchar("email")
    val hashPassword = varchar("hashPassword")
    val mobile = varchar("mobile")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val createAt = long("createAt")
}