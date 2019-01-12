package com.cleverapp.ui.viewmodels

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.App
import com.cleverapp.repository.TagFetchingResult
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

class EditTagsViewModel(app: App): BaseViewModel(app) {

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
    }

    fun getImageTags(imageUri: Uri) {
        imagePath.value = imageUri
        isFetchingTags.value = true
        repository.fetchTagsForImage(imageUri)
    }

    fun onSaveClicked() {
        repository.saveTaggedImage(
                TaggedImage(
                        imageTags.value!!.first().imageId,
                        imagePath.value.toString(),
                        imageTags.value!!))
    }

    override fun onCleared() {
        super.onCleared()
        repository.getTagFetchingResultLiveData().removeObserver(imageTagResultObserver)
    }
}
