package com.contentstack.contentstack_android_ecommerce_app.data.repositories

import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrderResponse
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrdersRequest
import com.contentstack.contentstack_android_ecommerce_app.data.interfaces.OrdersApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OrdersRepository {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun ordersRepo(
        order: OrdersRequest,
        onResult:  (isSuccess: Boolean, response: OrderResponse?) -> Unit
    ) {

        scope.launch {
            OrdersApiService.instance.ordersResponse(
                order
            ).enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                        if (response != null && response.isSuccessful)
                            onResult(true, response.body()!!)
                        else
                            onResult(false, null)
                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        onResult(false, null)
                    }

                })

        }

    }

}