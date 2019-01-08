package com.cleverapp

import android.app.Application
import androidx.room.Room
import com.cleverapp.repository.RepositoryFactory
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.database.AppDatabase.Companion.DATABASE_NAME
import com.cleverapp.repository.tagservice.TagServiceFactory

class App : Application() {

    val repository by lazy {
        RepositoryFactory.create(
                contentResolver,
                Room
                        .databaseBuilder(this, AppDatabase::class.java, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build(),
                TagServiceFactory.create())
    }
}