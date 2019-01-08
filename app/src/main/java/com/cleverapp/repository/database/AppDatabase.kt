package com.cleverapp.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

@Database(entities = [TaggedImage::class, ImageTag::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    companion object{
        const val DATABASE_NAME = "app-database"
        const val TAGGED_IMAGES_TABLE_NAME: String = "tagged_images"
        const val IMAGE_TAGS_TABLE_NAME: String = "image_tag"
    }

    abstract fun taggedImagesDao(): TaggedImagesDao
}