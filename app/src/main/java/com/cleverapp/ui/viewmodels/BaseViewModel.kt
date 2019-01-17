package com.cleverapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cleverapp.App

abstract class BaseViewModel(app: Application): AndroidViewModel(app){

    protected val repository = (app as App).repository
}