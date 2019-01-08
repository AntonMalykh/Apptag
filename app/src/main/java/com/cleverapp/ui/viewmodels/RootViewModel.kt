package com.cleverapp.ui.viewmodels

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.cleverapp.App
import com.cleverapp.repository.Repository
import com.cleverapp.repository.TagFetchingResult
import com.cleverapp.repository.data.ImageTag

class RootViewModel(app: App): BaseViewModel(app) {

    private val imageTagResultObserver: Observer<TagFetchingResult> =
            Observer {
                isFetchingTags.value = false
                if (TextUtils.isEmpty(it.getError()))
                    imageTags.value = it.getTaggedImages()
                else
                    error.value = it.getError()
            }

    val imagePath = MutableLiveData<Uri>()
    val isFetchingTags = MutableLiveData<Boolean>()
    val imageTags = MutableLiveData<List<ImageTag>>()
    val error = MutableLiveData<String>()

    init {
        repository.getTagFetchingResultLiveData().observeForever(imageTagResultObserver)
        isFetchingTags.postValue(false)
    }

    fun updateUri(fileUri: Uri) {
        imagePath.value = fileUri
    }

    fun updateTags(fileUri: Uri) {
        isFetchingTags.value = true
        repository.fetchTagsForImage(fileUri)
    }

    override fun onCleared() {
        super.onCleared()
        repository.getTagFetchingResultLiveData().removeObserver(imageTagResultObserver)
    }
}