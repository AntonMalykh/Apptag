package com.cleverapp.repository.database

import com.cleverapp.repository.TagLoadingResult
import com.cleverapp.repository.data.ImageTag

class DatabaseTagLoadingResult(
        private val previewBytes: ByteArray,
        private val tags: List<ImageTag>)
    : TagLoadingResult {



    override fun getError(): String? {
        return null
    }

    override fun getTaggedImages(): List<ImageTag>? {
        return tags
    }

    override fun getPreview(): ByteArray? {
        return previewBytes
    }
}