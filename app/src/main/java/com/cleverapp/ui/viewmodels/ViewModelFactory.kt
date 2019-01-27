package com.cleverapp.ui.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
        private val app: Application,
        private val arguments: Bundle?)
    : ViewModelProvider.AndroidViewModelFactory(app){

    override fun <T: ViewModel?> create(modelClass: Class<T>): T {
        return when {
            (modelClass.isAssignableFrom(ImagesViewModel::class.java)) ->
                ImagesViewModel(app) as T
            (modelClass.isAssignableFrom(TagsViewModel::class.java)) ->
                TagsViewModel(app, arguments!!) as T
            else -> throw IllegalStateException("ViewModel was not found")
        }
    }
}