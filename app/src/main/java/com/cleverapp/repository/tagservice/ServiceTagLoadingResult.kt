package com.cleverapp.repository.tagservice

import com.cleverapp.repository.TagLoadingResult
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.tagservice.response.GetImageTagResponse
import java.util.ArrayList

class ServiceTagLoadingResult(
        private val imageId: String,
        private val response: GetImageTagResponse)
    : TagLoadingResult {

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
        return strings.fold(ArrayList(strings.size)){ acc, tag ->
            acc.add(ImageTag(imageId, tag))
            acc
        }
    }
}