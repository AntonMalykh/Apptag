package com.screw.mediumer.repository.webclient.retrofitwebclient

import com.screw.mediumer.repository.webclient.WebClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal class RetrofitWebClient : WebClient {

    val retrofit : Retrofit

    init {
        retrofit =
                Retrofit
                        .Builder()
                        .baseUrl(WebClient.ENDPOINT)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
    }
}
