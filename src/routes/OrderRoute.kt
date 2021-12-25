package com.example.routes

import com.example.data.model.Order
import com.example.data.model.User
import com.example.repositories.OrdersRepository
import com.example.utils.MyResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val ORDERS = "$API_VERSION/orders"
const val CREATE_ORDER = "$ORDERS/createOrder"
const val COMING_ORDERS = "$ORDERS/comingOrders"
const val PER_ORDERS = "$ORDERS/preOrders"
const val HISTORY_ORDERS = "$ORDERS/historyOrders"
const val DELETE_ORDERS = "$ORDERS/deleteOrder"
const val UPDATE_STATUS_ORDERS = "$ORDERS/updateOrder"

fun Route.orderRoute(ordersRepository: OrdersRepository) {
    authenticate("jwt") {

        post(CREATE_ORDER) {
            // get user info from jwt
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@post
            }

            val order = try {
                call.receive<Order>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }

            try {
                val result = ordersRepository.createOrder(order, user)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Creation Order Successfully",
                            data = result
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed Creation Order",
                            data = null
                        )
                    )
                    return@post
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed Creation Order",
                        data = null
                    )
                )
                return@post
            }

        }

        get(COMING_ORDERS) {
            // get user info from jwt
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }


            try {
                val result = ordersRepository.getOnComingOrdersOfUser(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = " Successfully",
                        data = result
                    )
                )
                return@get

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@get
            }

        }

        get(PER_ORDERS) {
            // get user info from jwt
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }


            try {
                val result = ordersRepository.getPreOrdersOfUser(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = " Successfully",
                        data = result
                    )
                )
                return@get

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@get
            }

        }

        get(HISTORY_ORDERS) {
            // get user info from jwt
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }


            try {
                val result = ordersRepository.getHistoryOrdersOfUser(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = " Successfully",
                        data = result
                    )
                )
                return@get

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@get
            }

        }

        delete(DELETE_ORDERS) {
            // get user info from jwt
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@delete
            }

            val orderId = try {
                call.request.queryParameters["orderId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message =  "Missing Some Fields ",
                        data = null
                    )
                )
                return@delete
            }

            try {
                val result = ordersRepository.deleteOrderOfUser(user,orderId)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = " Successfully",
                            data = result
                        )
                    )
                    return@delete
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = " Failed Deletion",
                            data = null
                        )
                    )
                    return@delete
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@delete
            }

        }

        put(UPDATE_STATUS_ORDERS) {
            // get user info from jwt
            val user = try {
                call.principal<User>()!!
            }
            catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@put
            }

            val orderId = try {
                call.request.queryParameters["orderId"]!!.toInt()
            }
            catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message =  "Missing Some Fields ",
                        data = null
                    )
                )
                return@put
            }

            val orderType = try {
                call.request.queryParameters["orderType"]!!.toInt()
            }
            catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message =  "Missing Some Fields ",
                        data = null
                    )
                )
                return@put
            }

            try {
                val result = ordersRepository.updateOrderStatus(orderType,orderId,user)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Successfully",
                            data = result
                        )
                    )
                    return@put
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = " Failed Update",
                            data = null
                        )
                    )
                    return@put
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed",
                        data = null
                    )
                )
                return@put
            }

        }


    }

}