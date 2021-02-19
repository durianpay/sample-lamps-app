package com.contentstack.contentstack_android_ecommerce_app.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrderResponse
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrdersRequest
import com.contentstack.contentstack_android_ecommerce_app.data.repositories.OrdersRepository
import com.contentstack.contentstack_android_ecommerce_app.utils.ResourceStatus

class OrdersViewModel: ViewModel() {
    var apiCallStatus = MutableLiveData<ResourceStatus>() // livedata for observing login API call status
    var apiResponseData = MutableLiveData<OrderResponse>() // live data for getting response

    fun callOrdersData(
        context: Context,
        order: OrdersRequest
    ) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo: NetworkInfo? = connectivityManager.getActiveNetworkInfo()
        val connected =
            nInfo != null && nInfo.isAvailable && nInfo.isConnected

        if (connected) { //using the above connectivity manager to check user has an active internet
            apiCallStatus.value = ResourceStatus.loading()

            OrdersRepository.ordersRepo(order) { isSuccess, response ->
                if (isSuccess) {
                    apiCallStatus.value =
                        ResourceStatus.success("")
                    apiResponseData.postValue(response)

                } else {
                    apiCallStatus.value =
                        ResourceStatus.sessionexpired()
                }

            }


        } else {
            apiCallStatus.value = ResourceStatus.nonetwork()
        }

    }
}