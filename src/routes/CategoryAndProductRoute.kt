package com.example.routes

import com.example.data.model.*
import com.example.repositories.CategoryAndProductRepository
import com.example.utils.MyResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val CATEGORY_REQUEST = "$API_VERSION/category"
const val PRODUCT_REQUEST = "$API_VERSION/product"
const val FIND_PRODUCT_REQUEST = "$PRODUCT_REQUEST/findByID"
const val PRODUCT_REQUEST_FORYOU = "$API_VERSION/product/for you"
const val MY_FAVOURITES_PRODUCTTs = "$PRODUCT_REQUEST/favorites"
const val DELETE_My_FAV_PRODUCT = "$PRODUCT_REQUEST/fav/delete"
const val SET_FAV_PRODUCT_REQUEST = "$PRODUCT_REQUEST/setFav"
const val RATE_PRODUCT = "$PRODUCT_REQUEST/rate"
const val UPDATE_RATE_PRODUCT = "$PRODUCT_REQUEST/updateRate"
const val POPULAR_PRODUCT = "$PRODUCT_REQUEST/popular"
const val CREATE_CATEGORY_REQUEST = "$CATEGORY_REQUEST/createCategory"
const val CREATE_PRODUCT_CATEGORY_REQUEST = "$PRODUCT_REQUEST/createProduct"
const val FILTER_PRODUCT = "$PRODUCT_REQUEST/filterProduct"

fun Route.categoryAndProductRoute(categoryAndProductRepository: CategoryAndProductRepository) {


    authenticate("jwt") {
        post(CREATE_CATEGORY_REQUEST) {
            val categoryRequest = try {
                call.receive<Category>()
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
            val restaurant = try {
                call.principal<Restaurant>()!!
            } catch (e: Exception) {
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

            try {

                categoryRequest.restaurantId = restaurant.restaurantId!!
                val result = categoryAndProductRepository.createCategory(categoryRequest)

                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Success Create your category",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed Create Category",
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
                        message = e.message ?: "Failed Create Category",
                        data = null
                    )
                )
                return@post
            }


        }

        get(CATEGORY_REQUEST) {

            val restaurantId = try {
                call.request.queryParameters["restaurantId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing restaurantId Field",
                        data = null
                    )
                )
                return@get
            }
            try {

                val categories = categoryAndProductRepository.getCategoryOfRestaurant(restaurantId)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = categories
                    )
                )
                return@get

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed get Category",
                        data = null
                    )
                )
                return@get
            }
        }

        post(CREATE_PRODUCT_CATEGORY_REQUEST) {
            val productRequest = try {
                call.receive<Product>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing Some Fields",
                        data = null
                    )
                )
                return@post
            }

            val restaurant = try {
                call.principal<Restaurant>()!!
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

            try {

                productRequest.restaurantId = restaurant.restaurantId!!
                val result = categoryAndProductRepository.createProduct(productRequest)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "Success Create Your Product",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed Create Product",
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
                        message = e.message ?: "Failed get Products",
                        data = null
                    )
                )
                return@post
            }

        }

        get(PRODUCT_REQUEST) {
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
            val restaurantId = try {
                call.request.queryParameters["restaurantId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing restaurantId Field",
                        data = null
                    )
                )
                return@get
            }

            val categoryId = try {
                call.request.queryParameters["categoryId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing CategoryId Field",
                        data = null
                    )
                )
                return@get
            }
            try {


                val products = categoryAndProductRepository.getProductOfCategory(categoryId, restaurantId, user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = products
                    )
                )
                return@get


            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed get Category",
                        data = null
                    )
                )
                return@get
            }
        }

        get(FIND_PRODUCT_REQUEST) {
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
            val productId = try {
                call.request.queryParameters["productId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing restaurantId Field",
                        data = null
                    )
                )
                return@get
            }

            try {


                val product = categoryAndProductRepository.findProductById(productId, user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = product
                    )
                )
                return@get


            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed get Category",
                        data = null
                    )
                )
                return@get
            }
        }

        get(PRODUCT_REQUEST_FORYOU) {
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
            val restaurantId = try {
                call.request.queryParameters["restaurantId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = "Missing restaurantId Field",
                        data = null
                    )
                )
                return@get
            }

            try {
                val products = categoryAndProductRepository.getProductForYou(restaurantId, user)
                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = products
                    )
                )
                return@get


            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed get Category",
                        data = null
                    )
                )
                return@get
            }
        }

        get(MY_FAVOURITES_PRODUCTTs) {
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
                println("UserId :${user.toString()}")
                val favRestaurants = categoryAndProductRepository.getAllFavProduct(user)
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

        post(RATE_PRODUCT) {
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
                return@post
            }
            val rateProduct = try {
                call.receive<RateProduct>()
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
                rateProduct.userId = user.id
                val result = categoryAndProductRepository.rateProduct(rateProduct)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "\uD83D\uDE0D Thanks For Your Rate",
                            data = result
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

        put(UPDATE_RATE_PRODUCT) {
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
            val rateProduct = try {
                call.receive<RateProduct>()
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
                rateProduct.userId = user.id
                val result = categoryAndProductRepository.updateRateProduct(rateProduct)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = "\uD83D\uDE0D Thanks For Your Rate",
                            data = result
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

        post(SET_FAV_PRODUCT_REQUEST) {
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
                return@post
            }

            val favProduct = try {
                call.receive<FavProduct>()
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
                favProduct.userId = user.id
                val result = categoryAndProductRepository.setInFavProduct(favProduct)
                if (result > 0) {
                    call.respond(
                        HttpStatusCode.OK,
                        MyResponse(
                            success = true,
                            message = " \uD83D\uDE0D Success Add To Your Favourite Product",
                            data = null
                        )
                    )
                    return@post
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        MyResponse(
                            success = false,
                            message = "Failed To set this Product in favourites",
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
                        message = e.message ?: "Failed To set this Product in favourites",
                        data = null
                    )
                )
                return@post
            }

        }

        delete(DELETE_My_FAV_PRODUCT) {
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

            val restaurantId = try {
                call.request.queryParameters["productId"]!!.toInt()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MyResponse(
                        success = false,
                        message = "Messing Fields",
                        data = null
                    )
                )
                return@delete
            }

            try {
                val result = categoryAndProductRepository.deleteFavouriteProduct(restaurantId, user.id)

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
                            message = "Failed Delete your favorite Product",
                            data = null
                        )
                    )
                }

            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    MyResponse(
                        success = false,
                        message = "Failed Delete your favorite Product",
                        data = null
                    )
                )


            }


        }

        get(POPULAR_PRODUCT) {
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

                val favRestaurants = categoryAndProductRepository.getPopularProduct(user)
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

        post(FILTER_PRODUCT) {
            val user = try {
                call.principal<User>()!!
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Failed ",
                        data = null
                    )
                )
                return@post
            }

            val filterName = try {
                call.request.queryParameters["filterName"]!!.toString()
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

                val result = categoryAndProductRepository.filterProduct(filterName, 5, user)

                call.respond(
                    HttpStatusCode.OK,
                    MyResponse(
                        success = true,
                        message = "Success",
                        data = result
                    )
                )
                return@post
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    MyResponse(
                        success = false,
                        message = e.message ?: "Error Some Fields",
                        data = null
                    )
                )
                return@post
            }

        }


    }
}