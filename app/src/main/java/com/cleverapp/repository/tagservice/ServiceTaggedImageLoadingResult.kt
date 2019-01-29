package com.cleverapp.repository.tagservice

import com.cleverapp.repository.TagsLoadingResult
import com.cleverapp.repository.data.ImageTag
import java.util.ArrayList

class ServiceTaggedImageLoadingResult(
        private val response: GetImageTagResponse)
    : TagsLoadingResult {

    private val tagsConverted = stringsToImageTags(response.tags)

    override fun getError(): String? {
        return response.error
    }

    override fun getTags(): List<ImageTag>? {
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
