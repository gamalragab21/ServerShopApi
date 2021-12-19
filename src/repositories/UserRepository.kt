package com.example.repositories

import com.example.data.model.DeliveryAddress
import com.example.data.model.User
import com.example.data.tables.DeliveryAddressEntity
import com.example.data.tables.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.math.BigInteger


class UserRepository(val db:Database) {

    suspend fun register(user:User) = withContext(Dispatchers.IO){
        val result=db.insert(UserEntity) {
            set(it.userName, user.username)
            set(it.email, user.email)
            set(it.hashPassword, user.hashedPassword())
            set(it.imageProfile, user.image)
            set(it.latitude, user.latitude)
            set(it.longitude, user.longitude)
            set(it.mobile, user.mobile)
            set(it.createAt, user.createAt)
        }

            // val deliveryAddressResult=createDeliveryAddress(DeliveryAddress(userId = result as Int , latitude = user.latitude, longitude = user.longitude))

        result
    }

    suspend fun getDeliveryAddress(userId:Int)= withContext(Dispatchers.IO){
        val deliveryAddress = db.from(DeliveryAddressEntity)
            .select()
            .where {
                DeliveryAddressEntity.userId eq userId
            }.map {
                rowToDeliveryAddress(row = it)
            }

        deliveryAddress
    }

    private fun rowToDeliveryAddress(row: QueryRowSet?) :DeliveryAddress?{

        return if (row == null) {
            null
        } else {
            val id = row[DeliveryAddressEntity.deliveryAddressId] ?: -1
            val latitude = row[DeliveryAddressEntity.latitude] ?: 0.0
            val longitude = row[DeliveryAddressEntity.longitude] ?: 0.0
            val userId = row[DeliveryAddressEntity.userId] ?: -1
            DeliveryAddress(id,userId,latitude, longitude)
        }

    }



    suspend fun createDeliveryAddress(deliveryAddress: DeliveryAddress) = withContext(Dispatchers.IO) {
        val result = db.insert(DeliveryAddressEntity) {
            set(it.userId, deliveryAddress.userId)
            set(it.latitude, deliveryAddress.latitude)
            set(it.longitude, deliveryAddress.longitude)
        }
        result
    }


    suspend fun findUserByEmail(email:String)=withContext(Dispatchers.IO){
        // this fun check if user email exist or not and if exists return user info
        val user = db.from(UserEntity)
            .select()
            .where {
                UserEntity.email eq email
            }.map {
                rowToUser(it)
            }.firstOrNull()

        user
    }

    suspend fun findUserById(userId:Int)=withContext(Dispatchers.IO){
        // this fun check if user email exist or not and if exists return user info
        val user = db.from(UserEntity)
            .select()
            .where {
                UserEntity.userId eq userId
            }.map {
                rowToUser(it)
            }.firstOrNull()

        user
    }

    private fun rowToUser(row:QueryRowSet?):User?{
        return if (row==null){
             null
        }else{
            val id = row[UserEntity.userId]?:-1
            val email = row[UserEntity.email]?:""
            val username = row[UserEntity.userName] ?:""
            val image = row[UserEntity.imageProfile] ?:""
            val haspassord = row[UserEntity.hashPassword] ?:""
            val latitude = row[UserEntity.latitude] ?:0.0
            val longitude = row[UserEntity.longitude] ?:0.0
            val mobile = row[UserEntity.mobile] ?:""
            val createAt = row[UserEntity.createAt] ?:0
            User(id, username, email, image,haspassord,latitude,longitude,mobile,createAt)
        }
    }
}