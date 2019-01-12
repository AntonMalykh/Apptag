package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cleverapp.repository.data.TaggedImage

interface Repository {

    fun fetchTagsForImage(uri: Uri)

    fun getTagFetchingResultLiveData(): LiveData<TagFetchingResult>

    fun getTaggedImagesChangedLiveData(): LiveData<Boolean>

    fun getSavedImages(): List<TaggedImage>

    fun deleteSavedImage(image: TaggedImage)

    fun saveTaggedImage(taggedImage: TaggedImage)
}