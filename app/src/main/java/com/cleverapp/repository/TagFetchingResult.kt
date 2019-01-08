package com.cleverapp.repository

import com.cleverapp.repository.data.ImageTag

interface TagFetchingResult {
    fun getError(): String? = null
    fun getTaggedImages(): List<ImageTag>? = null
}
