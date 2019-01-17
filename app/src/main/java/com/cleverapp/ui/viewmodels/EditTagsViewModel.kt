package com.cleverapp.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.TaggedImageLoadingResult
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

class EditTagsViewModel(app: Application): BaseViewModel(app) {

    private val imageTagResultObserver: Observer<TaggedImageLoadingResult> =
            Observer {
                isLoadingTags.value = false
                imageBytes.value = it.getPreview()
                if (TextUtils.isEmpty(it.getError()))
                    imageTags.value = it.getTaggedImages()
                else
                    error.value = it.getError()
            }

    val imageBytes = MutableLiveData<ByteArray>()
    val isLoadingTags = MutableLiveData<Boolean>()
    val imageTags = MutableLiveData<List<ImageTag>>()
    val error = MutableLiveData<String>()

    init {
        repository.observeTagLoaded(imageTagResultObserver)
    }

    fun loadTaggedImage(imageUri: Uri) {
        isLoadingTags.value = true
        repository.loadNewTaggedImage(imageUri)
    }

    fun loadTaggedImage(imageId: String) {
        isLoadingTags.value = true
        repository.loadSavedTaggedImage(imageId)
    }

    fun onSaveClicked() {
        repository.saveTaggedImage(
                TaggedImage(
                        imageTags.value!!.first().imageId,
                        imageBytes.value!!,
                        imageTags.value!!))
    }

    override fun onCleared() {
        super.onCleared()
        repository.observeTagLoaded(imageTagResultObserver)
    }
}
