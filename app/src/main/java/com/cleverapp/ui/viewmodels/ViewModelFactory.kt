package com.cleverapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleverapp.App

class ViewModelFactory(
        val app: Application)
    : ViewModelProvider.AndroidViewModelFactory(app) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(app as App) as T

            modelClass.isAssignableFrom(EditTagsViewModel::class.java) ->
                EditTagsViewModel(app as App) as T

            else ->
                throw IllegalArgumentException("View model not found")
        }
    }

}