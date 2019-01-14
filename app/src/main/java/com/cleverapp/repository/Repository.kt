package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cleverapp.repository.data.TaggedImage

interface Repository {

    fun getTagLoadingResultLiveData(): LiveData<TagLoadingResult>

    fun getTaggedImagesChangedLiveData(): LiveData<Boolean>

    fun loadTagsForImage(uri: Uri)

    fun getSavedTaggedImages(): List<TaggedImage>

    fun deleteSavedTaggedImage(image: TaggedImage)

    fun saveTaggedImage(taggedImage: TaggedImage)
}