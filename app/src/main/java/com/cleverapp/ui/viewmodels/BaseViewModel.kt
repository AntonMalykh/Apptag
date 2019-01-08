package com.cleverapp.ui.viewmodels

import androidx.lifecycle.AndroidViewModel
import com.cleverapp.App

abstract class BaseViewModel(
        app: App)
    : AndroidViewModel(app){

    protected val repository = app.repository
}