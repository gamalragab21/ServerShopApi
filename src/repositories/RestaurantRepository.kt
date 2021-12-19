package com.example.repositories

import com.example.data.model.FavRestaurant
import com.example.data.model.RateRestaurant
import com.example.data.model.Restaurant
import com.example.data.model.User
import com.example.data.tables.FavRestaurantEntity
import com.example.data.tables.RateRestaurantEntity
import com.example.data.tables.RestaurantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.*
import kotlin.math.absoluteValue


class RestaurantRepository(val db: Database) {

    suspend fun createRestaurant(restaurant: Restaurant) = withContext(Dispatchers.IO) {
        val result = db.insert(RestaurantEntity) {
            set(it.restaurantName, restaurant.restaurantName)
            set(it.email, restaurant.email)
            set(it.hashPassword, restaurant.hashedPassword())
            set(it.imageRestaurant, restaurant.imageRestaurant)
            set(it.latitude, restaurant.latitude)
            set(it.longitude, restaurant.longitude)
            set(it.contact, restaurant.contact)
            set(it.freeDelivery, restaurant.freeDelivery)
            set(it.createAt, restaurant.createAt)
            set(it.restaurantType, restaurant.restaurantType)
        }
        result
    }

    suspend fun findRestaurantByEmail(email: String) = withContext(Dispatchers.IO) {
        // this fun check if user email exist or not and if exists return user info
        println("My Restaurant ")
        val restaurant = db.from(RestaurantEntity)
            .select()
            .where {
                RestaurantEntity.email eq email
            }.map {
                rowToRestaurant(row = it,null)
            }.firstOrNull()
        println("My Restaurant is :${restaurant}")

        restaurant
    }

     suspend fun findRestaurantById(restaurantId: Int, user: User?) = withContext(Dispatchers.IO) {
        // this fun check if user email exist or not and if exists return user info
        val restaurant = db.from(RestaurantEntity)
            .select()
            .where {
                RestaurantEntity.restaurantId eq restaurantId
            }.map {
                rowToRestaurant(it,user)
            }.firstOrNull()

        restaurant
    }

    private suspend fun rowToRestaurant(row: QueryRowSet?, user: User?): Restaurant? {
        return if (row == null) {
            null
        } else {
            val id = row[RestaurantEntity.restaurantId] ?: -1
            val email = row[RestaurantEntity.email] ?: ""
            val restaurantName = row[RestaurantEntity.restaurantName] ?: ""
            val image = row[RestaurantEntity.imageRestaurant] ?: ""
            val haspassord = row[RestaurantEntity.hashPassword] ?: ""
            val latitude = row[RestaurantEntity.latitude] ?: 0.0
            val longitude = row[RestaurantEntity.longitude] ?: 0.0
            val contact = row[RestaurantEntity.contact] ?: ""
            val createAt = row[RestaurantEntity.createAt] ?: 0
            val restaurantType = row[RestaurantEntity.restaurantType] ?: ""
            val freeDelivery = row[RestaurantEntity.freeDelivery] ?: true
            val rateRestaurant = getRateRestaurant(id)
            val favRestaurant = checkRestaurantInFav(id, user)
            val isFav = favRestaurant != null

            var total = 0.0
            rateRestaurant.forEach {
                total += it.countRate
            }
            val ratingCount =if(rateRestaurant.isNotEmpty()){
                total / rateRestaurant.size
            }else{
                0.0
            }

            Restaurant(
                id,
                restaurantName,
                image,
                email,
                haspassord,
                contact,
                latitude,
                longitude,
                createAt,
                restaurantType,
                freeDelivery,
                rateRestaurant,
                isFav,
                ratingCount.absoluteValue,
                user
            )
        }
    }

    private suspend fun checkRestaurantInFav(id: Int, user: User?): FavRestaurant? = withContext(Dispatchers.IO) {


        val favRestaurant = if (user != null) {
            db.from(FavRestaurantEntity)
                .select()
                .where {
                    (FavRestaurantEntity.restaurantId eq id) and (FavRestaurantEntity.userId eq user?.id!!)
                }.map {
                    rowToFavRestaurant(it)
                }.firstOrNull()
        } else {
            null
        }
        favRestaurant
    }

    private fun rowToFavRestaurant(row: QueryRowSet): FavRestaurant? {
        return if (row == null) {
            null
        } else {
            val favRestaurantId = row[FavRestaurantEntity.favRestaurantId] ?: -1
            val userId = row[FavRestaurantEntity.userId] ?: -1
            val restaurantId = row[FavRestaurantEntity.restaurantId] ?: -1
            FavRestaurant(favRestaurantId, restaurantId, userId)

        }
    }

    private suspend fun getRateRestaurant(id: Int) = withContext(Dispatchers.IO) {
        val rateRestaurant = db.from(RateRestaurantEntity)
            .select()
            .where {
                RateRestaurantEntity.restaurantId eq id
            }
            .mapNotNull {
                rowToRateRestaurant(it)
            }

        rateRestaurant.sortedByDescending {
            it.createAt
        }

    }

    suspend fun getAllFavRestaurant(user: User) = withContext(Dispatchers.IO) {
        val favRestaurants = db.from(FavRestaurantEntity)
            .select()
            .where {
                FavRestaurantEntity.userId eq user.id!!
            }
            .mapNotNull {
//                 rowToFavRestaurant(it)
                val restaurantId = it[FavRestaurantEntity.restaurantId] ?: -1
                val restaurante = findRestaurantById(restaurantId,user)
             //   restaurante?.inFav = true
                restaurante
            }

        favRestaurants
    }

    suspend fun getAllRatingRestaurant(user: User)= withContext(Dispatchers.IO){
        val ratingRestaurants = db.from(RateRestaurantEntity)
            .select()
            .where {
                RateRestaurantEntity.userId eq user.id!!
            }.orderBy(RateRestaurantEntity.createAt.desc())

            .mapNotNull {
//                 rowToFavRestaurant(it)
                val restaurantId = it[RateRestaurantEntity.restaurantId] ?: -1
                val restaurante = findRestaurantById(restaurantId,user)
                restaurante
            }

        ratingRestaurants
    }

    private fun rowToRateRestaurant(row: QueryRowSet): RateRestaurant? {
        return if (row == null) {
            null
        } else {
            val rateId = row[RateRestaurantEntity.rateId] ?: -1
            val userId = row[RateRestaurantEntity.userId] ?: -1
            val restaurantId = row[RateRestaurantEntity.restaurantId] ?: -1
            val countRate = row[RateRestaurantEntity.countRate] ?: 0.0
            val createAt = row[RateRestaurantEntity.createAt] ?: 0
            RateRestaurant(rateId, userId, restaurantId, countRate, createAt)
        }
    }

    suspend fun getAllRestaurant(user: User) = withContext(Dispatchers.IO) {
        // this fun check if user email exist or not and if exists return user info
        val restaurants = db.from(RestaurantEntity)
            .select()
            .orderBy(RestaurantEntity.createAt.desc())
            .mapNotNull {
                rowToRestaurant(it, user)
            }

        restaurants
    }

   suspend fun getNearlyRestaurant(latitude: Double, longitude: Double, user: User) = withContext(Dispatchers.IO) {
        val restaurants = db.from(RestaurantEntity)
            .select()
            .orderBy(RestaurantEntity.createAt.desc())
            .mapNotNull {
                rowToRestaurant(it, user)
            }

       Collections.sort(restaurants, Comparator<Restaurant> { o1, o2 ->
           val dist1: Int = o1.calculationByDistance(o1.latitude,o1.longitude,latitude,longitude)
           val dist2: Int = o2.calculationByDistance(o2.latitude,o2.longitude,latitude,longitude)
           dist1.compareTo(dist2)
       })

       restaurants

    }
    suspend fun filterRestaurant(name: String, user: User)=withContext(Dispatchers.IO){
        val filterRestaurant = db.from(RestaurantEntity)
            .select()
            .where {
                ( RestaurantEntity.restaurantName like "%${name}%") or
                ( RestaurantEntity.restaurantType like "%${name}%")
            }.map {
                rowToRestaurant(row = it,user)
            }

        filterRestaurant
    }

    suspend fun getPopularRestaurant(user: User)= withContext(Dispatchers.IO) {
        // this fun check if user email exist or not and if exists return user info
        val popularRestaurants = db.from(RestaurantEntity)
            .select()
            .orderBy(RestaurantEntity.createAt.desc())
            .mapNotNull {
                rowToRestaurant(it, user)
            }


        popularRestaurants.sortedByDescending {
            it.rateCount
        }
    }
    suspend fun setInFavRestaurant(favRestaurant: FavRestaurant) = withContext(Dispatchers.IO) {
        val result = db.insert(FavRestaurantEntity) {
            set(it.restaurantId, favRestaurant.restaurantId)
            set(it.userId, favRestaurant.userId)
        }
        result
    }

    suspend fun rateRestaurant(rateRestaurant: RateRestaurant) = withContext(Dispatchers.IO) {
        val result = db.insertAndGenerateKey(RateRestaurantEntity) {
            set(it.restaurantId, rateRestaurant.restaurantId)
            set(it.userId, rateRestaurant.userId)
            set(it.countRate, rateRestaurant.countRate)
            set(it.createAt, rateRestaurant.createAt)
        }
        result as Int
    }

    suspend fun deleteFavouriteRestaurant(restaurantId: Int, userId: Int?) = withContext(Dispatchers.IO) {
        val result = db.delete(FavRestaurantEntity) {
            (it.userId eq userId!!) and (it.restaurantId eq restaurantId)
        }

        result

    }

    suspend fun deleteRestaurant(restaurant: Restaurant) = withContext(Dispatchers.IO) {

        val result = db.delete(RestaurantEntity) {
            it.restaurantId eq restaurant.restaurantId!!

        }

        result

    }

    suspend fun updateRateRestaurant(rateRestaurant: RateRestaurant)= withContext(Dispatchers.IO) {

        val result=db.update(RateRestaurantEntity){
            set(it.countRate, rateRestaurant.countRate)
            set(it.createAt, rateRestaurant.createAt)
            where {
                (it.rateId eq rateRestaurant.rateId!!) and (it.userId eq rateRestaurant.userId!!) and (it.restaurantId eq rateRestaurant.restaurantId)
            }
        }

        result
    }


}