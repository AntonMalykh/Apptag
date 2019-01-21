package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

interface Repository {

    fun getTaggedImagesChangedLiveData(): LiveData<Boolean>

    fun loadNewTaggedImage(uri: Uri): LiveData<TaggedImageLoadingResult>

    fun getSavedTaggedImages(): LiveData<List<TaggedImage>>

    fun deleteSavedTaggedImage(image: TaggedImage)

    fun saveTaggedImage(taggedImage: TaggedImage)

    fun getSavedTaggedImage(imageId: String): LiveData<TaggedImageLoadingResult>

    fun updateTaggedImages(imagesToUpdate: List<TaggedImage>)

    fun updateImageTags(tagsToUpdate: List<ImageTag>)
}