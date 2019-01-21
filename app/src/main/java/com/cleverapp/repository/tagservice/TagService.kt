package com.cleverapp.repository.tagservice

import androidx.lifecycle.Observer

interface TagService {
    fun getImageTags(imageBytes: ByteArray, consumer: Observer<GetImageTagResponse>)
}
