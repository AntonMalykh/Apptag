package com.cleverapp.repository

import com.cleverapp.repository.data.ImageTag

interface TagsLoadingResult {
    fun getError(): String?
    fun getTags(): List<ImageTag>?
}
