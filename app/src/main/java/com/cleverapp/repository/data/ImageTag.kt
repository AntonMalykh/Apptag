package com.cleverapp.repository.data

import androidx.room.*
import com.cleverapp.repository.database.AppDatabase

@Entity(
        tableName = AppDatabase.IMAGE_TAGS_TABLE_NAME,
        foreignKeys = [
            ForeignKey(
                    entity = TaggedImage::class,
                    parentColumns = [TaggedImage.COLUMN_NAME_ID],
                    childColumns = [ImageTag.COLUMN_NAME_IMAGE_ID])
        ],
        indices = [Index(TaggedImage.COLUMN_NAME_ID)])
data class ImageTag(
        @ColumnInfo(name = COLUMN_NAME_IMAGE_ID)
        var imageId: String,
        var tag: String = "",
        var isCustom: Boolean = false,
        @ColumnInfo(name = COLUMN_NAME_ORDINAL_NUM)
        var ordinalNum: Int = 0){

    companion object {
        const val COLUMN_NAME_IMAGE_ID = "image_id"
        const val COLUMN_NAME_ORDINAL_NUM = "ordinal_num"
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}