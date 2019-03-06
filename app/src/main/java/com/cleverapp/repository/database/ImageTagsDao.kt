package com.cleverapp.repository.database

import androidx.room.*
import com.cleverapp.repository.data.ImageTag

@Dao
interface ImageTagsDao {

    @Query("""
        SELECT *
        FROM ${AppDatabase.IMAGE_TAGS_TABLE_NAME}
        WHERE ${ImageTag.COLUMN_NAME_IMAGE_ID} = :imageId
        ORDER BY ${ImageTag.COLUMN_NAME_ORDINAL_NUM} ASC""")
    fun getTags(imageId: String): List<ImageTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTags(tags: List<ImageTag>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTags(tagsToUpdate: List<ImageTag>)

    @Delete
    fun deleteTags(tags: List<ImageTag>)

}
