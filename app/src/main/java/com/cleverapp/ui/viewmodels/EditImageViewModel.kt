package com.cleverapp.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.TaggedImageLoadingResult
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

class EditImageViewModel(app: Application): BaseViewModel(app) {

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

    private var tagLoading: LiveData<TaggedImageLoadingResult>? = null

    fun loadTaggedImage(imageUri: Uri) {
        isLoadingTags.value = true
        tagLoading = repository.loadNewTaggedImage(imageUri)
        tagLoading?.observeForever(imageTagResultObserver)

    }

    fun loadTaggedImage(imageId: String) {
        isLoadingTags.value = true
        tagLoading = repository.getSavedTaggedImage(imageId)
        tagLoading?.observeForever(imageTagResultObserver)
    }

    fun saveImageTags(isNewImage: Boolean, currentUiOrder: List<ImageTag>) {
        if (isNewImage)
            repository.saveTaggedImage(
                    TaggedImage(
                            imageTags.value!!.first().imageId,
                            imageBytes.value!!,
                            imageTags.value!!))
        else
            updateTagsOrdering(currentUiOrder)
    }

    private fun updateTagsOrdering(currentUiOrder: List<ImageTag>) {
        for (i in currentUiOrder.indices)
            currentUiOrder[i].ordinalNum = i
        repository.updateImageTags(currentUiOrder)
    }
}
