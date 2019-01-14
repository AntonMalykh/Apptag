package com.cleverapp.ui.viewmodels

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.App
import com.cleverapp.repository.TagLoadingResult
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

class EditTagsViewModel(app: App): BaseViewModel(app) {

    private val imageTagResultObserver: Observer<TagLoadingResult> =
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
        repository.getTagLoadingResultLiveData().observeForever(imageTagResultObserver)
    }

    fun getImageTags(imageUri: Uri) {
        isLoadingTags.value = true
        repository.loadTagsForImage(imageUri)
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
        repository.getTagLoadingResultLiveData().removeObserver(imageTagResultObserver)
    }
}
