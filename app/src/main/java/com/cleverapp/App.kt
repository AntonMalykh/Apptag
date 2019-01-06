package com.cleverapp

import android.app.Application
import com.cleverapp.repository.RepositoryFactory
import com.cleverapp.repository.tagservice.TagServiceFactory

class App : Application() {


    val repository by lazy { RepositoryFactory.create(this, TagServiceFactory.create()) }
}