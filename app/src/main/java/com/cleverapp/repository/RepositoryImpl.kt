package com.cleverapp.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.database.DatabaseHelper
import com.cleverapp.repository.database.DatabaseTaggedImageLoadingResult
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

    private var tagLoadedObserver: Observer<TaggedImageLoadingResult>? = null
    private val taggedImagesUpdated = MutableLiveData<Boolean>()

    override fun observeTagLoaded(observer: Observer<TaggedImageLoadingResult>) {
        tagLoadedObserver = observer
    }

    override fun removeTagLoadedObserver(observer: Observer<TaggedImageLoadingResult>) {
        tagLoadedObserver = null
    }

    override fun getTaggedImagesChangedLiveData(): LiveData<Boolean> {
        return taggedImagesUpdated
    }

    override fun loadNewTaggedImage(uri: Uri) {
        tagService.getImageTags(
                getImageBytes(uri),
                // worker thread
                Observer { getImageTagResponse ->
                    tagLoadedObserver?.onChanged(
                            ServiceTaggedImageLoadingResult(
                                    UUID.randomUUID().toString(),
                                    getImageTagResponse))
                })
    }

    override fun loadSavedTaggedImage(imageId: String) {
        tagLoadedObserver?.onChanged(
                DatabaseTaggedImageLoadingResult(databaseHelper.getTaggedImage(imageId)))
    }

    override fun saveTaggedImage(taggedImage: TaggedImage) {
        databaseHelper.insertTaggedImage(taggedImage)
        taggedImagesUpdated.value = true
    }

    override fun getSavedTaggedImages(): List<TaggedImage> {
        return databaseHelper.getAllTaggedImages()
    }

    override fun deleteSavedTaggedImage(image: TaggedImage) {
        databaseHelper.deleteSavedImage(image)
        taggedImagesUpdated.value = true
    }

    private fun getImageBytes(uri: Uri): ByteArray {
        val c = contentResolver.query(uri, null, null, null, null)

        c?.let {
            var size = 0
            if (c.moveToFirst()) {
                size = c.getInt(c.getColumnIndex(MediaStore.MediaColumns.SIZE))
            }
            c.close()
            return if (size > MAX_THUMBNAIL_IMAGE_FILE_SIZE)
                compressImage(contentResolver.openInputStream(uri), MAX_THUMBNAIL_IMAGE_FILE_SIZE)
            else
                contentResolver.openInputStream(uri).readBytes()
        }
        return ByteArray(0)
    }
}