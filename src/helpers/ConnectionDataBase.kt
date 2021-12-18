package com.example.helpers

import org.ktorm.database.Database

object ConnectionDataBase {

    val database = Database.connect(
        url = "jdbc:mysql://MYSQL5037.site4now.net/db_a7ce11_noteapp", //server/database
        driver = "com.mysql.cj.jdbc.Driver",
        user = "a7ce11_noteapp", // uid
        password = "Gamal2172001"


//        url = "jdbc:mysql://127.0.0.1:3306/foodDoor",
//        driver = "com.mysql.cj.jdbc.Driver",
//        user = "root",
//        password = "Gamal@@2172001"
//    )
    )

}