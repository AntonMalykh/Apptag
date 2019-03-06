package com.cleverapp.repository.database

import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.Image

//TODO use worker thread
class DatabaseHelper(val database: AppDatabase){

    fun getImageWithTags(imageId: String): Image {
        return database.imagesDao().getImage(imageId)
                .also {it.tags = getTagsForImage(it.id)}
    }

    fun getImagesWithTags(): List<Image> {
        val images = database.imagesDao().getAllImages()
        images.forEach {
            it.tags = getTagsForImage(it.id)
        }
        return images
    }

    fun insertImageWithTags(image: Image) {
        database.imagesDao().insertImage(image)
        insertImageTags(image.tags)
    }

    fun updateImages(imagesToUpdate: Collection<Image>) {
        database.imagesDao().updateImages(imagesToUpdate)
    }

    fun deleteImage(image: Image) {
        database.imageTagsDao().deleteTags(image.tags)
        database.imagesDao().deleteImage(image)
    }

    fun deleteImage(imageId: String) {
        database.imageTagsDao().deleteTags(getTagsForImage(imageId))
        database.imagesDao().deleteImage(getImageWithTags(imageId))
    }

    private fun getTagsForImage(imageId: String): List<ImageTag> {
        return database.imageTagsDao().getTags(imageId)
    }

    private fun insertImageTags(tags: List<ImageTag>) {
        database.imageTagsDao().insertTags(tags)
    }

    fun updateTagsForImage(imageId: String, tags: List<ImageTag>) {
        database.imageTagsDao().deleteTags(
                database.imageTagsDao().getTags(imageId))
        insertImageTags(tags)
    }
}
