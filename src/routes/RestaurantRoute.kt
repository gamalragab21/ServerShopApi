package com.example.routes

import com.example.data.model.FavRestaurant
import com.example.data.model.RateRestaurant
import com.example.data.model.Restaurant
import com.example.data.model.User
import com.example.repositories.RestaurantRepository
import com.example.utils.MyResponse
import com.example.utils.TokenManager
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


const val RESTAURANTS = "$API_VERSION/restaurants"
const val FIND_RESTAURANT = "$RESTAURANTS/findById"
const val FILTER_RESTAURANTS = "$RESTAURANTS/filter"
const val RATE_RESTAURANTS = "$RESTAURANTS/rate"
const val UPDATE_RATE_RESTAURANTS = "$RESTAURANTS/updateRate"
const val My_RATING_RESTAURANTS = "$RESTAURANTS/rating"
const val NEARLY_LOCATION_RESTAURANTS = "$RESTAURANTS/nearlyLocation"
const val POPULAR_RESTAURANTS = "$RESTAURANTS/popular"
const val CREATE_FAV_RESTAURANT = "$RESTAURANTS/createFav"
const val My_FAVs_RESTAURANTTs = "$RESTAURANTS/favourites"
const val DELETE_My_FAV_RESTAURANTTs = "$RESTAURANTS/fav/delete"
const val CREATE_RESTAURANT_REQUEST = "$RESTAURANTS/createRestaurant"
const val LOGIN_RESTAURANT_REQUEST = "$RESTAURANTS/loginRestaurant"
const val MY_RESTAURANT_REQUEST = "$RESTAURANTS/myRestaurant"
const val Delete_RESTAURANT_REQUEST = "$RESTAURANTS/delete"

fun Route.restaurantRoute(restaurantRepository: RestaurantRepository, tokenManager: TokenManager) {


    //base_url/v1/restaurant/createRestaurant
    post(CREATE_RESTAURANT_REQUEST) {
        // check body request if  missing some fields
        val restaurantRequest = try {
            call.receive<Restaurant>()
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
            if (restaurantRepository.findRestaurantByEmail(restaurantRequest.email) == null) // means not found
            {
                val result = restaurantRepository.createRestaurant(restaurantRequest)
                // if result >0 it's success else is failed
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Create Restaurant  Successfully",
                            data = tokenManager.generateJWTToken(restaurantRequest.email, false)
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Failed add your restaurant",
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
                        message = "Restaurant already creation before.",
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
                    message = e.message ?: "Failed Create Restaurant",
                    data = null
                )
            )
            return@post
        }

    }

    post(LOGIN_RESTAURANT_REQUEST) {
        val restaurantRequestLogin = try {
            call.receive<Restaurant>()
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
            val restaurant = restaurantRepository.findRestaurantByEmail(restaurantRequestLogin.email)
            if (restaurant != null) {
                // check password after hash password
                if (restaurant.matchPassword(restaurantRequestLogin.password)) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Restaurant is logged in successfully",
                            data = tokenManager.generateJWTToken(restaurant.email, false)
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

        get(RESTAURANTS) {

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
                return@get
            }

            try {

                val resultRestaurants = restaurantRepository.getAllRestaurant(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = resultRestaurants
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }

        get(FIND_RESTAURANT) {

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
                return@get
            }
            val restaurantId = try {
                call.request.queryParameters["restaurantId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }

            try {

                val resultRestaurant = restaurantRepository.findRestaurantById(restaurantId,user)
                if (resultRestaurant==null){
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Not Found Restaurant With this Id",
                            data = resultRestaurant
                        )
                    )
                    return@get
                }else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Success",
                            data = resultRestaurant
                        )
                    )
                    return@get
                }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }

        get(FILTER_RESTAURANTS) {

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
                return@get
            }
            val restaurantName = try {
                call.request.queryParameters["restaurantName"]!!.toString()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = "Missing Some Failed ",
                        data = null
                    )
                )
                return@get
            }
            try {

                val resultRestaurants = restaurantRepository.filterRestaurant(restaurantName,user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = resultRestaurants
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }
        get(My_FAVs_RESTAURANTTs) {

            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
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

                val favRestaurants = restaurantRepository.getAllFavRestaurant(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = favRestaurants
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }

        post(RATE_RESTAURANTS) {
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
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
            val rateRestaurant = try {
                call.receive<RateRestaurant>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }


            try {
                rateRestaurant.userId = user.id
                val result = restaurantRepository.rateRestaurant(rateRestaurant)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "\uD83D\uDE0D Thanks For Your Rate",
                            data = rateRestaurant
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed",
                            data = null
                        )
                    )
                    return@post
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed Rated",
                        data = null
                    )
                )
                return@post
            }
        }

        put(UPDATE_RATE_RESTAURANTS) {
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@put
            }
            val rateRestaurant = try {
                call.receive<RateRestaurant>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = "Missing Some Fields",
                        data = null
                    )
                )
                return@put
            }


            try {
                rateRestaurant.userId = user.id
                val result = restaurantRepository.updateRateRestaurant(rateRestaurant)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "\uD83D\uDE0D Thanks For Your Rate ",
                            data = rateRestaurant
                        )
                    )
                    return@put
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed",
                            data = null
                        )
                    )
                    return@put
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed Rated",
                        data = null
                    )
                )
                return@put
            }
        }

        get(My_RATING_RESTAURANTS) {
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
                return@get
            }
            try {
                val restaurantRating=restaurantRepository.getAllRatingRestaurant(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success" ,
                        data = restaurantRating
                    )
                )
                return@get

            }catch (e:Exception){
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }

        get(POPULAR_RESTAURANTS){
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
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

                val favRestaurants = restaurantRepository.getPopularRestaurant(user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = favRestaurants
                    )
                )

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@get
            }
        }

        post(NEARLY_LOCATION_RESTAURANTS) {
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
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
            val latitude:Double
            val longitude:Double
            try {
                 latitude = call.request.queryParameters["latitude"]!!.toDouble()
                 longitude = call.request.queryParameters["longitude"]!!.toDouble()
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

                val restaurants=restaurantRepository.getNearlyRestaurant(latitude,longitude,user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "success",
                        data = restaurants
                    )
                )
                return@post

            }catch (e:Exception){
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message?:"Failed",
                        data = null
                    )
                )
                return@post
            }
        }

        post(CREATE_FAV_RESTAURANT) {
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
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

            val favRestaurant = try {
                call.receive<FavRestaurant>()
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
                favRestaurant.userId = user.id
                val result = restaurantRepository.setInFavRestaurant(favRestaurant)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "\uD83D\uDE0D Marked  Add To Your Favourites Restaurant",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed To set this restaurant in favourites",
                            data = null
                        )
                    )
                    return@post
                }


            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed To set this restaurant in favourites",
                        data = null
                    )
                )
                return@post
            }

        }

        delete(DELETE_My_FAV_RESTAURANTTs) {
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@delete
            }

            val restaurantId = try {
                call.request.queryParameters["restaurantId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = "Messing Fields",
                        data = null
                    )
                )
                return@delete
            }

            try {
                val result = restaurantRepository.deleteFavouriteRestaurant(restaurantId, user?.id)

                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "\uD83D\uDE0D Un Marked Successes",
                            data = null
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed Delete your favorite restaurant",
                            data = null
                        )
                    )
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = "Failed Delete your favorite restaurant",
                        data = null
                    )
                )


            }


        }

        get(MY_RESTAURANT_REQUEST) {
            // get user info from jwt
            val restaurant = try {
                call.principal<Restaurant>()!!
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

            call.respond(
                HttpStatusCode.OK,
                MyResponse(
                    success = true,
                    message = "Success",
                    data = restaurant
                )
            )
            return@get


        }

        delete(Delete_RESTAURANT_REQUEST) {
            // get user info from jwt
            val restaurant = try {
                call.principal<Restaurant>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@delete
            }
            try {
                val result = restaurantRepository.deleteRestaurant(restaurant)

                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Success",
                            data = restaurant
                        )
                    )
                    return@delete
                } else {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = false,
                            message = "Failed Delete Your Restaurant",
                            data = null
                        )
                    )
                    return@delete
                }
            } catch (e: Exception) {

                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = e.message ?: "Failed Deletion",
                        data = null
                    )
                )
                return@delete

            }
        }
    }


}