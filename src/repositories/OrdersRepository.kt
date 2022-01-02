package com.example.repositories

import com.example.data.model.*
import com.example.data.tables.*
import com.example.utils.Constants.getTimeStamp
import com.mysql.cj.x.protobuf.MysqlxCrud
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class OrdersRepository(
    val db: Database, val userRepository: UserRepository, val restaurantRepository: RestaurantRepository
) {


    suspend fun createOrder(order: Order, user: User) = withContext(Dispatchers.IO) {
        println("createOrder ${order.toString()}")
        val result = db.insert(OrderEntity) {
            set(it.createAt, order.createAt)
            set(it.userId, user.id)
            set(it.productDistCount, order.productDistCount)
            set(it.productQuantity, order.productQuantity)
            set(it.productPrice, order.productPrice)
            set(it.productName, order.productName)
            set(it.coinType, order.coinType)
            set(it.foodImage, order.foodImage)
            set(it.restaurantId, order.restaurantId)
            set(it.foodId, order.foodId)
            set(it.freeDelivery, order.freeDelivery)
        }
        result
    }

    suspend fun getOrders(user: User) = withContext(Dispatchers.IO) {
        val products = db.from(OrderEntity)
            .select()
            .orderBy(OrderEntity.createAt.desc())
            .mapNotNull {
                rowToOrders(it, user)
            }

        products
    }

    suspend fun findOrderById(user: User, orderId: Int) = withContext(Dispatchers.IO) {
        val order = db.from(OrderEntity)
            .select()
            .orderBy(OrderEntity.createAt.desc())
            .where {
                (OrderEntity.userId eq user.id!!) and (OrderEntity.orderId eq orderId)
            }
            .mapNotNull {
                rowToOrders(it, user)
            }.firstOrNull()

        order
    }

    suspend fun getOnComingOrdersOfUser(user: User) = withContext(Dispatchers.IO) {
        val products = db.from(OrderEntity)
            .select()
            .orderBy(OrderEntity.createAt.desc())
            .where {
                (OrderEntity.userId eq user.id!!) and (OrderEntity.orderType eq 1)
            }
            .mapNotNull {
                rowToOrders(it, user)
            }

        products
    }

    suspend fun getPreOrdersOfUser(user: User) = withContext(Dispatchers.IO) {
        val products = db.from(OrderEntity)
            .select()
            .orderBy(OrderEntity.createAt.desc())
            .where {
                (OrderEntity.userId eq user.id!!) and (OrderEntity.orderType eq -1)
            }
            .mapNotNull {
                rowToOrders(it, user)
            }

        products
    }

    suspend fun getHistoryOrdersOfUser(user: User) = withContext(Dispatchers.IO) {
        val products = db.from(OrderEntity)
            .select()
            .orderBy(OrderEntity.createAt.desc())
            .where {
                (OrderEntity.userId eq user.id!!) and (OrderEntity.orderType eq 0)
            }
            .mapNotNull {
                rowToOrders(it, user)
            }

        products
    }

    suspend fun deleteOrderOfUser(user: User, orderId: Int) = withContext(Dispatchers.IO) {
        val result = db.delete(OrderEntity) {
            (it.userId eq user.id!!) and
                    (it.orderId eq orderId) and
                    (it.orderType eq 0) or
                    (it.orderType eq -1)
        }
        result
    }

    suspend fun updateOrderStatus(orderStatus: Int, orderId: Int, userId: Int, restaurant: Restaurant) =
        withContext(Dispatchers.IO) {
   print("${orderStatus } , ${userId},${restaurant}")
            if (orderStatus == 1) {
                createTrackingOrder(
                    Tracking(
                        0,
                        0,
                        userId,
                        getTimeStamp(),
                        orderId, restaurant.restaurantId!!,
                        null
                    )
                )

            }
            val result = db.update(OrderEntity) {
                set(it.orderType, orderStatus)
                where {
                    (it.orderId eq orderId) and
                            (it.restaurantId eq restaurant.restaurantId!!) and
                            (it.userId eq userId)
                }
            }

            result
        }

    private suspend fun rowToOrders(row: QueryRowSet, user: User): Order? {
        return if (row == null) {
            null
        } else {
            val orderId = row[OrderEntity.orderId] ?: -1
            val restaurantId = row[OrderEntity.restaurantId] ?: -1
            val userId = row[OrderEntity.userId] ?: -1
            val foodId = row[OrderEntity.foodId] ?: -1
            val foodImage = row[OrderEntity.foodImage] ?: ""
            val productName = row[OrderEntity.productName] ?: ""
            val freeDelivery = row[OrderEntity.freeDelivery] ?: true
            val productPrice = row[OrderEntity.productPrice] ?: 0.0
            val productDistCount = row[OrderEntity.productDistCount] ?: 0.0
            val productQuantity = row[OrderEntity.productQuantity] ?: 0
            val coinType = row[OrderEntity.coinType] ?: "$"
            val orderType = row[OrderEntity.orderType] ?: 0
            val createAt = row[OrderEntity.createAt] ?: 0
            val user = userRepository.findUserById(userId)
            val restaurant = restaurantRepository.findRestaurantById(restaurantId, user)

            Order(
                orderId = orderId,
                userId = userId,
                restaurantId = restaurantId,
                foodId = foodId,
                productName = productName,
                productPrice = productPrice,
                productDistCount = productDistCount,
                productQuantity = productQuantity,
                freeDelivery = freeDelivery,
                createAt = createAt,
                coinType = coinType,
                foodImage = foodImage,
                orderType = orderType,
                user = user,
                restaurant = restaurant
            )
        }
    }

    suspend fun createTrackingOrder(tracking: Tracking) = withContext(Dispatchers.IO) {
        val result = db.insert(TrackingEntity) {
            set(it.createAt, tracking.createAt)
            set(it.orderId, tracking.orderId)
            set(it.userId, tracking.userId)
            set(it.restaurantId, tracking.restaurantId)
            set(it.status, tracking.status)
        }

        print("createTrackingOrder ${result}")
        result
    }

    suspend fun getTrackingOrder(user: User, orderId: Int) = withContext(Dispatchers.IO) {
        val products = db.from(TrackingEntity)
            .select()
            .where {
                (TrackingEntity.userId eq user.id!!) and (TrackingEntity.orderId eq orderId)
            }
            .mapNotNull {
                rowToTrackingOrder(it, user)
            }

        products
    }

    private suspend fun rowToTrackingOrder(row: QueryRowSet, user: User): Tracking? {
        return if (row == null) {

            null
        } else {
            val trackingId = row[TrackingEntity.orderId] ?: -1
            val orderId = row[TrackingEntity.orderId] ?: -1
            val status = row[TrackingEntity.status] ?: -1
            val userId = row[TrackingEntity.userId] ?: -1
            val restaurantId = row[TrackingEntity.restaurantId] ?: -1
            val createAt = row[TrackingEntity.createAt] ?: -1
            val order = findOrderById(user, orderId)

            Tracking(
                trackingId = trackingId,
                status = status,
                userId = userId,
                createAt = createAt.toLong(),
                orderId = orderId,
                restaurantId = restaurantId,
                order = order
            )

        }
    }
}
