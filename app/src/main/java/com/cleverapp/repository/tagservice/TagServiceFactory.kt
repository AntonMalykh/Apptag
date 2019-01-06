package com.cleverapp.repository.tagservice

import com.cleverapp.repository.tagservice.clarifaitagservice.ClarifaiTagService

class TagServiceFactory {
    companion object {
        fun create() : TagService = ClarifaiTagService()
    }
}