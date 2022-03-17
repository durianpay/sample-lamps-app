package com.contentstack.contentstack_android_ecommerce_app.data.interfaces


import com.contentstack.contentstack_android_ecommerce_app.Constants
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrderResponse
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrdersRequest
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface OrdersApiService {

    @POST("orders")
    @Headers("Authorization: Basic XXXXXXXXXXXXXXXXXXX=")
    fun ordersResponse(@Body requestBody: OrdersRequest): Call<OrderResponse>


    companion object {
        val instance: OrdersApiService by lazy {

            val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            okHttpClientBuilder.addInterceptor(logging)
            okHttpClientBuilder.addInterceptor(requestInterceptor)


            val okHttpClient = okHttpClientBuilder.build()

            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setLenient()
                .create()
            Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.MERCHANT_BACKEND_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(
                    CoroutineCallAdapterFactory()
                )
                .build()
                .create(OrdersApiService::class.java)
        }

    }
}