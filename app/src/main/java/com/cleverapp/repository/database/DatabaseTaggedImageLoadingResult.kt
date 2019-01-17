package com.cleverapp.repository.database

import com.cleverapp.repository.TaggedImageLoadingResult
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

class DatabaseTaggedImageLoadingResult(private val image: TaggedImage): TaggedImageLoadingResult {

    override fun getError(): String? {
        return null
    }

    override fun getTaggedImages(): List<ImageTag>? {
        return image.tags
    }

    override fun getPreview(): ByteArray? {
        return image.previewBytes
    }
}