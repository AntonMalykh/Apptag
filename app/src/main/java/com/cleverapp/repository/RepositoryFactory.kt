package com.cleverapp.repository

import android.content.Context
import com.cleverapp.repository.tagservice.TagService

class RepositoryFactory{

    companion object {
        fun create(app: Context, tagService: TagService): Repository = RepositoryImpl(app, tagService)
    }
}