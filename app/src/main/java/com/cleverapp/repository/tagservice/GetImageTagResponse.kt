package com.cleverapp.repository.tagservice

class GetImageTagResponse private constructor(
        val requestImageBytes: ByteArray,
        val tags: List<String>? = null,
        val error: String? = null) {

    companion object {
        fun error(requestImageBytes: ByteArray, error: String): GetImageTagResponse =
                GetImageTagResponse(requestImageBytes = requestImageBytes, error = error)
        fun success(requestImageBytes: ByteArray, tags: List<String>): GetImageTagResponse =
                GetImageTagResponse(requestImageBytes = requestImageBytes, tags = tags)
    }
}
