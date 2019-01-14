package com.cleverapp.repository

import com.cleverapp.repository.data.ImageTag

interface TagLoadingResult {
    fun getError(): String?
    fun getTaggedImages(): List<ImageTag>?
    fun getPreview(): ByteArray?
}
