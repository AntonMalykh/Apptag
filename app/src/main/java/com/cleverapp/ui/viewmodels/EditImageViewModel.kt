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

    fun onSaveClicked() {
        repository.saveTaggedImage(
                TaggedImage(
                        imageTags.value!!.first().imageId,
                        imageBytes.value!!,
                        imageTags.value!!))
    }

    fun updateTagsOrdering(currentUiOrder: List<ImageTag>) {
        val changedIndices = ArrayList<ImageTag>(currentUiOrder.size)
        for (i in currentUiOrder.indices) {
            val tag = currentUiOrder[i]
            if (tag.ordinalNum != i)
                changedIndices.add(tag.apply { this.ordinalNum = i })
        }
        repository.updateImageTags(changedIndices)
    }
}
