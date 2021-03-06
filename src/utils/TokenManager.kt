package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.User
import io.ktor.application.*
import io.ktor.config.*
import java.util.*

class TokenManager(val config: HoconApplicationConfig) {
    val audience = config.property("audience").getString()
    val secret = config.property("secret").getString()
    val issuer = config.property("issuer").getString()
    val expirationDate = System.currentTimeMillis() + 600000;

    fun generateJWTToken(email:String,isUser:Boolean): String {

        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("isUser", isUser)
            .withClaim("email", email)
           // .withExpiresAt(Date(expirationDate))
            .sign(Algorithm.HMAC256(secret))
        return token
    }




    fun verifyJWTToken(): JWTVerifier {
        return JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}