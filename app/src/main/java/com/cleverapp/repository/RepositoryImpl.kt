package com.cleverapp.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.database.DatabaseHelper
import com.cleverapp.repository.tagservice.ServiceTaggedImageLoadingResult
import com.cleverapp.repository.tagservice.TagService
import com.cleverapp.utils.MAX_THUMBNAIL_IMAGE_FILE_SIZE
import com.cleverapp.utils.compressImage
import java.util.*

class RepositoryImpl(
        private val contentResolver: ContentResolver,
        database: AppDatabase,
        private val tagService: TagService)
    : Repository {

    private val databaseHelper = DatabaseHelper(database)

    private val taggedImagesUpdated = MutableLiveData<Boolean>()

    override fun getTaggedImagesChangedLiveData(): LiveData<Boolean> {
        return taggedImagesUpdated
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

    override fun getSavedTaggedImage(imageId: String): LiveData<TaggedImage> {
        val data = MutableLiveData<TaggedImage>()
        data.value = databaseHelper.getTaggedImage(imageId)
        return data
    }

    override fun saveTaggedImage(previewBytes: ByteArray, tags: List<ImageTag>) {
        val id = UUID.randomUUID().toString()
        tags.forEach { it.imageId = id }
        val newImage = TaggedImage(id, previewBytes, tags)
        newImage.ordinalNum = databaseHelper.getAllTaggedImages().size
        databaseHelper.insertTaggedImage(newImage)
        taggedImagesUpdated.value = true
    }

    override fun getSavedTaggedImages(): LiveData<List<TaggedImage>> {
        return MutableLiveData<List<TaggedImage>>()
                .also { it.value = databaseHelper.getAllTaggedImages() }
    }

    override fun deleteSavedTaggedImage(image: TaggedImage) {
        databaseHelper.deleteSavedImage(image)
        taggedImagesUpdated.value = true
    }

    override fun updateTaggedImages(imagesToUpdate: List<TaggedImage>) {
        databaseHelper.updateTaggedImages(imagesToUpdate)
    }


    override fun updateTaggedImage(imageId: String, newTags: List<ImageTag>) {
        newTags.forEach { it.imageId = imageId }
        databaseHelper.updateImageTags(imageId, newTags)
        taggedImagesUpdated.value = true
    }

    override fun getImageBytes(uri: Uri): ByteArray {
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.let {
            var size = 0
            if (cursor.moveToFirst()) {
                size = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
            }
            cursor.close()
            return if (size > MAX_THUMBNAIL_IMAGE_FILE_SIZE)
                compressImage(contentResolver.openInputStream(uri), MAX_THUMBNAIL_IMAGE_FILE_SIZE)
            else
                contentResolver.openInputStream(uri).readBytes()
        }
        return ByteArray(0)
    }
}