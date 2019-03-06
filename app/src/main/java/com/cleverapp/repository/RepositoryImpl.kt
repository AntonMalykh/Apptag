package com.cleverapp.repository

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.Image
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.database.DatabaseHelper
import com.cleverapp.repository.tagservice.ServiceTaggedImageLoadingResult
import com.cleverapp.repository.tagservice.TagService
import com.cleverapp.utils.MAX_THUMBNAIL_IMAGE_FILE_SIZE
import com.cleverapp.utils.compressImage
import com.cleverapp.utils.getImageRotation
import java.util.*

class RepositoryImpl(
        private val contentResolver: ContentResolver,
        database: AppDatabase,
        private val tagService: TagService)
    : Repository {

    private val databaseHelper = DatabaseHelper(database)
    private val taggedImagesUpdated = MutableLiveData<Boolean>()

    override fun getImagesChangedLiveData(): LiveData<Boolean> {
        return taggedImagesUpdated
    }

    override fun getImage(imageId: String): LiveData<Image> {
        val data = MutableLiveData<Image>()
        data.value = databaseHelper.getImageWithTags(imageId)
        return data
    }

    override fun getImages(): LiveData<List<Image>> {
        return MutableLiveData<List<Image>>()
                .also { it.value = databaseHelper.getImagesWithTags() }
    }

    override fun saveImage(previewBytes: ByteArray, tags: List<ImageTag>) {
        val id = UUID.randomUUID().toString()
        tags.forEach { it.imageId = id }
        val newImage = Image(id, previewBytes, tags)
        newImage.ordinalNum = databaseHelper.getImagesWithTags().size
        databaseHelper.insertImageWithTags(newImage)
        taggedImagesUpdated.value = true
    }

    override fun saveImages(imageUriList: List<Uri>) {
        var order = databaseHelper.getImagesWithTags().size
        imageUriList.forEach{
            val id = UUID.randomUUID().toString()
            val newImage = Image(id, makeImageBytes(it)).apply {
                ordinalNum = order++
            }
            databaseHelper.insertImageWithTags(newImage)
        }
        taggedImagesUpdated.value = true
    }

    override fun removeImage(image: Image) {
        databaseHelper.deleteImage(image)
        taggedImagesUpdated.value = true
    }

    override fun removeImage(imageId: String) {
        databaseHelper.deleteImage(imageId)
        taggedImagesUpdated.value = true
    }

    override fun removeImages(images: Collection<Image>) {
        var any = false
        images.forEach{
            databaseHelper.deleteImage(it)
            any = true
        }
        if (any)
            taggedImagesUpdated.value = true
    }

    override fun updateImage(imageId: String, newTags: List<ImageTag>) {
        newTags.forEach { it.imageId = imageId }
        databaseHelper.updateTagsForImage(imageId, newTags)
        taggedImagesUpdated.value = true
    }

    override fun updateImages(imagesToUpdate: Collection<Image>) {
        databaseHelper.updateImages(imagesToUpdate)
    }

    override fun getImageTags(imageBytes: ByteArray,
                              tagsLanguage: Language,
                              tagsCount: Int)
            : LiveData<TagsLoadingResult> {

        val data = MutableLiveData<TagsLoadingResult>()
        tagService.getImageTags(
                imageBytes,
                tagsLanguage,
                tagsCount,
                // worker thread
                Observer { data.postValue(ServiceTaggedImageLoadingResult(it)) }
        )
        return data
    }

    override fun makeImageBytes(uri: Uri): ByteArray {
        val cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null)

        cursor?.let {
            if (it.moveToFirst()) {
                return compressImage(
                        contentResolver.openInputStream(uri),
                        MAX_THUMBNAIL_IMAGE_FILE_SIZE,
                        getImageRotation(contentResolver.openInputStream(uri)))
            }
            it.close()
        }
        return ByteArray(0)
    }

    override fun removeFromStorage(imageUri: Uri) {
        contentResolver.delete(imageUri, null, null)
    }
}