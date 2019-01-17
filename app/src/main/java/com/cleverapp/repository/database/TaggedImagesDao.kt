package com.cleverapp.repository.database

import androidx.room.*
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

@Dao
interface TaggedImagesDao {

    @Query("SELECT * FROM ${AppDatabase.TAGGED_IMAGES_TABLE_NAME}")
    fun getAllTaggedImages(): List<TaggedImage>

    @Query("SELECT * FROM ${AppDatabase.IMAGE_TAGS_TABLE_NAME} " +
            "WHERE ${ImageTag.COLUMN_NAME_IMAGE_ID} = :imageId")
    fun getTags(imageId: String): List<ImageTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaggedImage(taggedImage: TaggedImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImageTags(imageTags: List<ImageTag>)

    @Delete
    fun deleteSavedImage(image: TaggedImage)

    @Delete
    fun deleteImageTags(tags: List<ImageTag>)

    @Query("SELECT * FROM ${AppDatabase.TAGGED_IMAGES_TABLE_NAME} " +
            "WHERE ${TaggedImage.COLUMN_NAME_ID} = :imageId")
    fun getTaggedImage(imageId: String): TaggedImage
}
