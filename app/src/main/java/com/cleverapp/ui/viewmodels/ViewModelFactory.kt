package com.cleverapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cleverapp.App

class ViewModelFactory(
        val app: Application)
    : ViewModelProvider.AndroidViewModelFactory(app)