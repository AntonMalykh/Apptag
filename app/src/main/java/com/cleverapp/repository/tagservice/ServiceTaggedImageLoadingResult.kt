package com.cleverapp.repository.tagservice

import com.cleverapp.repository.ImageTagsLoadingResult
import com.cleverapp.repository.data.ImageTag
import java.util.ArrayList

class ServiceTaggedImageLoadingResult(
        private val response: GetImageTagResponse)
    : ImageTagsLoadingResult {

    private val tagsConverted = stringsToImageTags(response.tags)

    override fun getError(): String? {
        return response.error
    }

    override fun getImageTags(): List<ImageTag>? {
        return tagsConverted
    }

    private fun stringsToImageTags(strings: Collection<String>?): List<ImageTag> {
        if (strings == null)
            return emptyList()
        var ordinalNum = 0
        return strings.fold(ArrayList(strings.size)){ acc, tag ->
            acc.add(ImageTag("", tag, ordinalNum = ordinalNum++))
            acc
        }
    }
}
