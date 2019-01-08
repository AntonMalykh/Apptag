package com.cleverapp.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

@Dao
interface TaggedImagesDao {

    @Query("SELECT * FROM ${AppDatabase.TAGGED_IMAGES_TABLE_NAME}")
    fun getAllTaggedImages(): List<TaggedImage>

    @Query("SELECT * FROM ${AppDatabase.IMAGE_TAGS_TABLE_NAME} " +
            "where ${ImageTag.COLUMN_NAME_IMAGE_ID} = :imageId")
    fun getTags(imageId: String): List<ImageTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaggedImage(taggedImage: TaggedImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImageTags(imageTags: List<ImageTag>)
}