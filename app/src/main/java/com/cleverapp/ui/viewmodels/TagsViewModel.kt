package com.cleverapp.ui.viewmodels

import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.ui.TagsFragment

class TagsViewModel(app: Application,
                    tagsArguments: Bundle)
    : BaseViewModel(app) {

    private var imageId: String? = null

    private var requestedTagLanguage: String = ""
    private var requestedTagCount: Int = 0

    private val imageRequest = MutableLiveData<Boolean>()
    private val imageLoading = Transformations.switchMap(imageRequest){
        loading.value = true
        repository.getSavedTaggedImage(imageId!!)
    }
    private val tagsRequest = MutableLiveData<Boolean>()

    val loading = MediatorLiveData<Boolean>()
            .also { loading ->
                loading.addSource(tagsRequest){
                    loading.value =  true
                }
            }
    val imageBytes = MutableLiveData<ByteArray>()
    val imageTags = MediatorLiveData<List<ImageTag>>()
            .also { tags ->
                tags.addSource(
                        Transformations.switchMap(tagsRequest){
                            repository.getImageTags(imageBytes.value!!, requestedTagLanguage, requestedTagCount)
                        })
                {
                    loading.value = false
                    if (TextUtils.isEmpty(it.getError()))
                        tags.value = it.getImageTags()
                    else
                        error.value = it.getError()
                }
            }
    val error = MutableLiveData<String>()

    init {
        if (TagsFragment.isNewImage(tagsArguments))
            imageBytes.value = repository.getImageBytes(tagsArguments.getParcelable(TagsFragment.ARG_KEY_URI))
        else {
            imageId = tagsArguments.getString(TagsFragment.ARG_KEY_IMAGE_ID)
            imageLoading.observeForever {
                    imageBytes.value = it.previewBytes
                    imageTags.value = it.tags
                    loading.value = false
            }
            imageRequest.value = true
        }
    }

    fun loadTags(language: String, count: Int) {
        requestedTagLanguage = language
        requestedTagCount = count
        tagsRequest.value = true
    }

    fun saveImageTags(currentUiOrder: List<ImageTag>) {
         if (imageId == null)
            repository.saveTaggedImage(imageBytes.value!!, currentUiOrder)
        else
            imageId?.let {
                repository.updateTaggedImage(it, updateTagsOrdering(currentUiOrder))
            }
    }

    private fun updateTagsOrdering(currentUiOrder: List<ImageTag>): List<ImageTag> {
        for (i in currentUiOrder.indices)
            currentUiOrder[i].ordinalNum = i
        return currentUiOrder
    }
}
