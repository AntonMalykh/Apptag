package com.cleverapp.repository.database

import androidx.room.*
import com.cleverapp.repository.data.Image

@Dao
interface ImagesDao {

    @Query("""
        SELECT *
        FROM ${AppDatabase.IMAGES_TABLE_NAME}
        ORDER BY ${Image.COLUMN_NAME_ORDINAL_NUM} DESC""")
    fun getAllImages(): List<Image>

    @Query("""
        SELECT *
        FROM ${AppDatabase.IMAGES_TABLE_NAME}
        WHERE ${Image.COLUMN_NAME_ID} = :imageId""")
    fun getImage(imageId: String): Image

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(image: Image)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateImages(imagesToUpdate: Collection<Image>)

    @Delete
    fun deleteImage(image: Image)
}
