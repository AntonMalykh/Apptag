package com.cleverapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.TaggedImage

class HistoryViewModel(app: Application): BaseViewModel(app) {

    var imagesChangedObserver: Observer<Boolean> = Observer {
        if (it == true)
            updateHistory()
    }

    val images by lazy { MutableLiveData<List<TaggedImage>>() }

    init {
        repository.getTaggedImagesChangedLiveData().observeForever(imagesChangedObserver)
    }

    fun updateHistory(){
        images.value = repository.getSavedTaggedImages()
    }

    fun onRemoveClicked(image: TaggedImage) {
        repository.deleteSavedTaggedImage(image)
        updateHistory()
    }

    override fun onCleared() {
        super.onCleared()
        repository.getTaggedImagesChangedLiveData().removeObserver(imagesChangedObserver)
    }
}