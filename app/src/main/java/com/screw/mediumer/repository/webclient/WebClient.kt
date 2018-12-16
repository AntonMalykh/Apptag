package com.screw.mediumer.repository.webclient

import com.screw.mediumer.repository.Repository

interface WebClient : Repository {


    companion object Constants{
        val ENDPOINT = "https://api.medium.com/v1"
    }
}
