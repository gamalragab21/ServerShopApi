package com.example

import com.example.helpers.ConnectionDataBase
import com.example.repositories.CategoryAndProductRepository
import com.example.repositories.RestaurantRepository
import com.example.repositories.UserRepository
import com.example.routes.categoryAndProductRoute
import com.example.routes.restaurantRoute
import com.example.routes.userRoute
import com.example.utils.TokenManager
import com.google.gson.GsonBuilder
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val config = HoconApplicationConfig(ConfigFactory.load())

    val db = ConnectionDataBase.database
    val userRepository = UserRepository(db)
    val restaurantRepository = RestaurantRepository(db)
    val categoryAndProductRepository = CategoryAndProductRepository(db)
    val tokenManager = TokenManager(config)


    // after validate send user entity from db
    install(Authentication) {

        jwt("jwt") {
            verifier(tokenManager.verifyJWTToken())
            realm = config.property("realm").getString()
            validate { jwtCredential ->
                val payload = jwtCredential.payload
                val email = payload.getClaim("email").asString()
                val isUser = payload.getClaim("isUser").asBoolean()
                println("User Email: ${email}")

                if (email.isNotEmpty()) {
                    if (isUser) {
                        val user = userRepository.findUserByEmail(email)
                        user
                    } else {
                        val restaurant = restaurantRepository.findRestaurantByEmail(email)
                        restaurant
                    }
                } else {
                    null
                }
            }
        }
//        jwt("restaurantsJwt") {
//            verifier(tokenManager.verifyJWTToken())
//            realm = config.property("realm").getString()
//            validate { jwtCredential ->
//                val payload = jwtCredential.payload
//                val email = payload.getClaim("email").asString()
//                if (email.isNotEmpty()) {
//                    val restaurant = restaurantRepository.findRestaurantByEmail(email)
//                    restaurant
//                } else {
//                    null
//                }
//            }
//        }
    }

    install(ContentNegotiation) {
        gson {
            setLenient()
        }

    }

    routing {

        userRoute(userRepository, tokenManager)
        restaurantRoute(restaurantRepository, tokenManager)
        categoryAndProductRoute(categoryAndProductRepository)
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

    }

}


