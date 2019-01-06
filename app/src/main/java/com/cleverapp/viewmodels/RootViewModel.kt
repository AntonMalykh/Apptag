package com.cleverapp.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.cleverapp.repository.Repository
import com.cleverapp.repository.data.ImageTagResult

class RootViewModel(private val repository: Repository): ViewModel() {

    private val imageTagResultObserver: Observer<ImageTagResult> =
            Observer {
                isFetchingTags.value = false
                imageTagResult.value = it
            }

    val imagePath: MutableLiveData<Uri> = MutableLiveData()
    val isFetchingTags: MutableLiveData<Boolean> = MutableLiveData()
    val imageTagResult: MutableLiveData<ImageTagResult> = MutableLiveData()

    init {
        repository.setImageTagResultObserver(imageTagResultObserver)
        isFetchingTags.postValue(false)
    }

    fun updateUri(fileUri: Uri) {
        imagePath.value = fileUri
    }

    fun updateTags(fileUri: Uri) {
        isFetchingTags.value = true
        repository.fetchImageTagsForImage(fileUri)
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeImageTagResultObserver()
    }
}