package com.cleverapp

import android.app.Application
import com.cleverapp.repository.tagservice.TagServiceFactory

class App : Application() {


    val tagService by lazy { TagServiceFactory.create() }
}