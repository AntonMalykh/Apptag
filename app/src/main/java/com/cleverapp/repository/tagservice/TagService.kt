package com.cleverapp.repository.tagservice

import androidx.lifecycle.Observer
import com.cleverapp.repository.tagservice.response.GetImageTagResponse

interface TagService {
    fun getImageTags(imageBytes: ByteArray, consumer: Observer<GetImageTagResponse>)
}
