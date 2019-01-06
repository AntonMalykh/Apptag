package com.cleverapp.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cleverapp.repository.tagservice.TagService

class RootViewModel(val tagService: TagService): ViewModel() {

    val imagePath: MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    val isFetchingTags: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    fun updateUri(fileUri: Uri) {
        imagePath.value = fileUri
    }

    fun updateTags(fileUri: Uri) {
        isFetchingTags.value = true
        val tags = tagService.getTags(fileUri).get()
        isFetchingTags.value = false
    }
}