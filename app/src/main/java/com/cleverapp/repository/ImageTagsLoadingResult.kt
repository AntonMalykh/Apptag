package com.cleverapp.repository

import com.cleverapp.repository.data.ImageTag

interface ImageTagsLoadingResult {
    fun getError(): String?
    fun getImageTags(): List<ImageTag>?
}
