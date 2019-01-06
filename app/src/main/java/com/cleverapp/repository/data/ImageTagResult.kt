package com.cleverapp.repository.data

data class ImageTagResult (
        val tags: List<String>? = null,
        val error: String? = null) {

    companion object {
        fun error(error: String): ImageTagResult = ImageTagResult(error = error)
        fun success(tags: List<String>): ImageTagResult = ImageTagResult(tags = tags)
    }
}
