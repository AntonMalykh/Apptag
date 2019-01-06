package com.cleverapp.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.ImageTagResult
import com.cleverapp.repository.tagservice.TagService

class RepositoryImpl(val app: Context, val tagService: TagService): Repository {

    private var imageRequestObserver: Observer<ImageTagResult>? = null

    override fun setImageTagResultObserver(observer: Observer<ImageTagResult>) {
        imageRequestObserver = observer
    }

    override fun removeImageTagResultObserver() {
        imageRequestObserver = null
    }

    override fun fetchImageTagsForImage(uri: Uri) {
        tagService.getImageTags(
                uriToFileBytes(uri),
                Observer {
                    imageRequestObserver?.onChanged(it)
                })
    }

    private fun uriToFileBytes(uri: Uri): ByteArray {
        return app.contentResolver.openInputStream(uri).readBytes()
    }
}