package com.cleverapp.repository.tagservice

import androidx.lifecycle.Observer
import com.cleverapp.repository.Language

interface TagService {
    fun getImageTags(imageBytes: ByteArray, tagsLanguage: Language, tagsCount: Int, consumer: Observer<GetImageTagResponse>)
}
