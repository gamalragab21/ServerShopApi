package com.example.data.model

import kotlinx.serialization.Serializable


@Serializable
data class Tracking(
    var trackingId: Int? = -1,
    var status: Int = 0,
    val userId: Int,
    val createAt: Long,
    val orderId: Int,
    var restaurantId: Int,
    val order: Order?
)