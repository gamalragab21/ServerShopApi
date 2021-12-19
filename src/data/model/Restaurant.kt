package com.example.data.model

import com.example.data.tables.RestaurantEntity.primaryKey
import io.ktor.auth.*
import kotlinx.serialization.Serializable
import org.ktorm.schema.double
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar
import org.mindrot.jbcrypt.BCrypt
import java.text.DecimalFormat

@Serializable
data class Restaurant (
    val restaurantId :Int?=-1,
    val restaurantName:String,
    val imageRestaurant :String,
    val email:String,
    val password :String,
    val contact :String,
    val latitude :Double,
    val longitude :Double,
    val createAt :Long,
    val restaurantType:String,
    val freeDelivery:Boolean,
    val rateRestaurant:List<RateRestaurant>?=null,
    var inFav:Boolean?=false,
    var rateCount:Double?=0.0,
    var user:User?
): Principal {
    fun hashedPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun matchPassword(hashPassword: String): Boolean {
        val doesPasswordMatch = BCrypt.checkpw(hashPassword, password)
        return doesPasswordMatch
    }

    fun calculationByDistance(lat1:Double,lon1:Double, lat2:Double,lon2:Double): Int {
        val Radius = 6371 // radius of earth in Km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))

        //return Radius * c
        return  kmInDec
    }

}