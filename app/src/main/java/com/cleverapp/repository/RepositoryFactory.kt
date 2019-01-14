package com.cleverapp.repository

import android.content.ContentResolver
import android.content.Context
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.tagservice.TagService

class RepositoryFactory{

    companion object {
        fun create(
                contentResolver: ContentResolver,
                database: AppDatabase,
                tagService: TagService)
                : Repository =
                    RepositoryImpl(contentResolver, database, tagService)
    }
}