package com.contentstack.contentstack_android_ecommerce_app.data.dataclasses

import java.util.*

data class OrderResponse(
    val data: OrderData
)

data class OrderData(
    val access_token: String,
    val id: String
)