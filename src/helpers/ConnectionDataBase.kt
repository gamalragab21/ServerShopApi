package com.example.helpers

import org.ktorm.database.Database

object ConnectionDataBase {

    val database = Database.connect(
        url = "jdbc:mysql://127.0.0.1:3306/foodDoor",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "Gamal@@2172001"
    )

}