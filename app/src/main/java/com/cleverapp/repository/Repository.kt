package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.TaggedImage

interface Repository {

    fun observeTagLoaded(observer: Observer<TaggedImageLoadingResult>)

    fun removeTagLoadedObserver(observer: Observer<TaggedImageLoadingResult>)

    fun getTaggedImagesChangedLiveData(): LiveData<Boolean>

    fun loadNewTaggedImage(uri: Uri)

    fun getSavedTaggedImages(): List<TaggedImage>

    fun deleteSavedTaggedImage(image: TaggedImage)

    fun saveTaggedImage(taggedImage: TaggedImage)

    fun loadSavedTaggedImage(imageId: String)
}