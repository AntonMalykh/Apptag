package com.screw.mediumer.repository.webclient

import com.screw.mediumer.repository.webclient.retrofitwebclient.RetrofitWebClient

class WebClientFactory {
    companion object {
        fun getWebClient() : WebClient = RetrofitWebClient()

    }
}