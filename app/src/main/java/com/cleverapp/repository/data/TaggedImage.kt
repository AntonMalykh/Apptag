package com.cleverapp.repository.data

import androidx.room.*
import androidx.room.ColumnInfo.BLOB
import com.cleverapp.repository.database.AppDatabase.Companion.TAGGED_IMAGES_TABLE_NAME

@Entity(tableName = TAGGED_IMAGES_TABLE_NAME)
data class TaggedImage(
        @PrimaryKey
        @ColumnInfo(name = COLUMN_NAME_ID)
        var id: String,
        @ColumnInfo(typeAffinity = BLOB)
        var previewBytes: ByteArray,
        @ColumnInfo(name = COLUMN_NAME_ORDINAL_NUM)
        var ordinalNum: Int){

    constructor(id: String,
                previewBytes: ByteArray,
                tags: List<ImageTag>)
            : this(id, previewBytes, 0){
        this.tags = tags
    }

    companion object {
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_ORDINAL_NUM = "ordinal_num"
    }

    @Ignore
    var tags: List<ImageTag> = emptyList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaggedImage

        if (id != other.id) return false
        if (!previewBytes.contentEquals(other.previewBytes)) return false
        if (tags != other.tags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + previewBytes.contentHashCode()
        result = 31 * result + tags.hashCode()
        return result
    }
}
