package com.contentstack.contentstack_android_ecommerce_app.data.dataclasses

data class OrdersRequest(
    val access_token: String?,
    val amount: Long?,
    val currency: String,
    val customer: Customer,
    val description: String?,
    val items: List<Item>?,
    val order_ref_id: String
)