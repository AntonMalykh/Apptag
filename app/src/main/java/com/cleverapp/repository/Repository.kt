package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

interface Repository {

    fun getTaggedImagesChangedLiveData(): LiveData<Boolean>

    fun getSavedTaggedImages(): LiveData<List<TaggedImage>>

    fun deleteSavedTaggedImage(image: TaggedImage)

    fun saveTaggedImage(previewBytes: ByteArray, tags: List<ImageTag>)

    fun getSavedTaggedImage(imageId: String): LiveData<TaggedImage>

    fun updateTaggedImages(imagesToUpdate: List<TaggedImage>)

    fun updateTaggedImage(imageId: String, newTags: List<ImageTag>)

    fun getImageBytes(uri: Uri): ByteArray

    fun getImageTags(imageBytes: ByteArray,
                     requestedTagLanguage: String,
                     requestedTagCount: Int)
            : LiveData<ImageTagsLoadingResult>
}