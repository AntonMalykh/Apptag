package com.cleverapp.repository.tagservice

import com.cleverapp.repository.TaggedImageLoadingResult
import com.cleverapp.repository.data.ImageTag
import java.util.ArrayList

class ServiceTaggedImageLoadingResult(
        private val imageId: String,
        private val response: GetImageTagResponse)
    : TaggedImageLoadingResult {

    private val tagsConverted = stringsToImageTags(response.tags)

    override fun getError(): String? {
        return response.error
    }

    override fun getTaggedImages(): List<ImageTag>? {
        return tagsConverted
    }

    override fun getPreview(): ByteArray? {
        return response.requestImageBytes
    }

    private fun stringsToImageTags(strings: Collection<String>?): List<ImageTag> {
        if (strings == null)
            return emptyList()
        var ordinalNum = 0
        return strings.fold(ArrayList(strings.size)){ acc, tag ->
            acc.add(ImageTag(imageId, tag, ordinalNum = ordinalNum++))
            acc
        }
    }
}