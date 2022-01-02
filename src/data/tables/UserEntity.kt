package com.example.data.tables

import com.google.protobuf.Descriptors
import org.ktorm.schema.*
import java.math.BigInteger
import java.sql.Types
import java.sql.Types.BIGINT


object UserEntity:Table<Nothing>("User") {
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