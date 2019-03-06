package com.cleverapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.Image

@Database(entities = [Image::class, ImageTag::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    companion object{
        const val DATABASE_NAME = "app-database"
        const val IMAGES_TABLE_NAME: String = "images"
        const val IMAGE_TAGS_TABLE_NAME: String = "image_tags"
    }

    abstract fun imagesDao(): ImagesDao
    abstract fun imageTagsDao(): ImageTagsDao
}