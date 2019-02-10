package com.cleverapp.repository.database

import androidx.room.*
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

@Dao
interface TaggedImagesDao {

    // IMAGES

    @Query("""
        SELECT *
        FROM ${AppDatabase.TAGGED_IMAGES_TABLE_NAME}
        ORDER BY ${TaggedImage.COLUMN_NAME_ORDINAL_NUM} DESC""")
    fun getAllTaggedImages(): List<TaggedImage>

    @Query("""
        SELECT *
        FROM ${AppDatabase.TAGGED_IMAGES_TABLE_NAME}
        WHERE ${TaggedImage.COLUMN_NAME_ID} = :imageId""")
    fun getTaggedImage(imageId: String): TaggedImage

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaggedImage(taggedImage: TaggedImage)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTaggedImages(imagesToUpdate: Collection<TaggedImage>)

    @Delete
    fun deleteSavedImage(image: TaggedImage)

    // TAGS

    @Query("""
        SELECT *
        FROM ${AppDatabase.IMAGE_TAGS_TABLE_NAME}
        WHERE ${ImageTag.COLUMN_NAME_IMAGE_ID} = :imageId
        ORDER BY ${ImageTag.COLUMN_NAME_ORDINAL_NUM} ASC""")
    fun getTags(imageId: String): List<ImageTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImageTags(imageTags: List<ImageTag>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateImageTags(tagsToUpdate: List<ImageTag>)

    @Delete
    fun deleteImageTags(tags: List<ImageTag>)
}
