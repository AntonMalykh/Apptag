package com.cleverapp.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import com.cleverapp.App
import com.cleverapp.repository.data.TaggedImage

class HistoryViewModel(app: App): BaseViewModel(app) {

    val images by lazy { MutableLiveData<List<TaggedImage>>() }

    fun updateHistory(){
        images.value = repository.getSavedImages()
    }
}