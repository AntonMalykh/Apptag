package com.cleverapp.repository.tagservice

import androidx.lifecycle.Observer
import com.cleverapp.repository.data.ImageTagResult

interface TagService {
    fun getImageTags(imageBytes: ByteArray, resultHandler: Observer<ImageTagResult>)
}
