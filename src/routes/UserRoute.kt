package com.example.routes

import com.example.data.model.DeliveryAddress
import com.example.data.model.User
import com.example.repositories.UserRepository
import com.example.utils.MyResponse
import com.example.utils.TokenManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val CREATE_DELIVERY_ADDRESS_USERS = "$USERS/createDeliveryAddress"
const val DELIVERY_ADDRESS_USERS = "$USERS/DeliveryAddress"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"
const val ME_REQUEST = "$USERS/me"

fun Route.userRoute(userRepository: UserRepository, tokenManager: TokenManager) {


    //base_url/v1/users/register
    post(REGISTER_REQUEST) {
        // check body request if  missing some fields
        val registerRequest = try {
            call.receive<User>()
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

        // check if operation connected db successfully
        try {

            // check if email exist or note
            if (userRepository.findUserByEmail(registerRequest.email) == null) // means not found
            {
                val result = userRepository.register(registerRequest)
                // if result >0 it's success else is failed
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Registration Successfully",
                            data = tokenManager.generateJWTToken(registerRequest.email,true)
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Failed Registration",
                            data = null
                        )
                    )
                    return@post
                }
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "User already registration before.",
                        data = null
                    )
                )
                return@post
            }

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = false,
                    message = e.message ?: "Failed Registration",
                    data = null
                )
            )
            return@post
        }

    }

    post(LOGIN_REQUEST) {
        val userRequestLogin = try {
            call.receive<User>()
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
            val user = userRepository.findUserByEmail(userRequestLogin.email)
            if (user != null) {
// check password after hash pasword
                if (user.matchPassword(userRequestLogin.password)) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "You are logged in successfully",
                            data = tokenManager.generateJWTToken(user.email,true)
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Password Incorrect",
                            data = null
                        )
                    )
                    return@post
                }
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "Email is wrong",
                        data = null
                    )
                )
                return@post
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = false,
                    message = e.message ?: "Failed Logged in system",
                    data = null
                )
            )
            return@post
        }
    }

    authenticate("jwt") {
        get(ME_REQUEST) {
            // get user info from jwt
            val user =  try{
                call.principal<User>()!!
            }catch (e:Exception){
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

            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = true,
                    message = "Success",
                    data = user
                )
            )
            return@get


        }
        post(CREATE_DELIVERY_ADDRESS_USERS) {
            // get user info from jwt
            val user =  try{
                call.principal<User>()!!
            }catch (e:Exception){
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@post
            }

            val deliveryAddress=try {
                call.receive<DeliveryAddress>()!!
            }catch (e:Exception){
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing Some Failed",
                        data = null
                    )
                )
                return@post
            }


            try {
                val result=userRepository.createDeliveryAddress(deliveryAddress)
                if (result<0){
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed ",
                            data = null
                        )
                    )
                    return@post
                }else{
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Success",
                            data = "Add Address Sucess"
                        )
                    )
                    return@post
                }
            }catch (e:Exception){
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@post
            }



        }
        get(DELIVERY_ADDRESS_USERS) {
            // get user info from jwt
            val user =  try{
                call.principal<User>()!!
            }catch (e:Exception){
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }

            try {

                val deliveryAddress=userRepository.getDeliveryAddress(user.id!!)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = deliveryAddress
                    )
                )
                return@get

            }catch (e:Exception) {

                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message?:"Failed",
                        data = null
                    )
                )
                return@get

            }
        }
    }


}