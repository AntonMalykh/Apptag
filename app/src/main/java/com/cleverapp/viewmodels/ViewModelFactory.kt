package com.cleverapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleverapp.repository.tagservice.TagService
import java.lang.IllegalArgumentException

class ViewModelFactory(val tagService: TagService) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RootViewModel::class.java))
            return RootViewModel(tagService) as T
        else
            throw IllegalArgumentException("View model not found")
    }

}