package com.cleverapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleverapp.repository.Repository

class ViewModelFactory(val repository: Repository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RootViewModel::class.java))
            return RootViewModel(repository) as T
        else
            throw IllegalArgumentException("View model not found")
    }

}