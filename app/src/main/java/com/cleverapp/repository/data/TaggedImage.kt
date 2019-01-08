package com.cleverapp.repository.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.cleverapp.repository.database.AppDatabase.Companion.TAGGED_IMAGES_TABLE_NAME

@Entity(tableName = TAGGED_IMAGES_TABLE_NAME)
data class TaggedImage(
        @PrimaryKey
        @ColumnInfo(name = COLUMN_NAME_ID)
        var id: String,
        var imageUri: String){

    constructor(id: String,
                imageUri: String,
                tags: List<ImageTag>) : this(id, imageUri){
        this.tags = tags
    }

    companion object {
        const val COLUMN_NAME_ID = "id"
    }

    @Ignore
    var tags: List<ImageTag> = emptyList()
}
