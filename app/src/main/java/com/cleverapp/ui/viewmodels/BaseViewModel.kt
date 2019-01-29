package com.cleverapp.ui.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.cleverapp.App

abstract class BaseViewModel(app: Application): AndroidViewModel(app){

    protected val repository = (app as App).repository
    protected val preferences: SharedPreferences =
            app.getSharedPreferences("App shared preferences", MODE_PRIVATE)
}