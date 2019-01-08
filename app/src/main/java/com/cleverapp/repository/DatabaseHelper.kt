package com.cleverapp.repository

import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.database.TaggedImagesDao

class DatabaseHelper(
        val database: AppDatabase)
    : TaggedImagesDao {

    override fun getAllTaggedImages(): List<TaggedImage> {
        val images = database.taggedImagesDao().getAllTaggedImages()
        images.forEach {
            it.tags = getTags(it.id)
        }
        return images
    }

    override fun getTags(imageId: String): List<ImageTag> {
        return database.taggedImagesDao().getTags(imageId)
    }

    override fun insertTaggedImage(taggedImage: TaggedImage) {
        database.taggedImagesDao().insertTaggedImage(taggedImage)
        database.taggedImagesDao().insertImageTags(taggedImage.tags)
    }

    override fun insertImageTags(imageTags: List<ImageTag>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
