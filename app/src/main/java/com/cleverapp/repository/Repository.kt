package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.ImageTagResult

interface Repository {

    fun setImageTagResultObserver(observer: Observer<ImageTagResult>)

    fun removeImageTagResultObserver()

    fun fetchImageTagsForImage(uri: Uri)
}