package com.cleverapp.repository

import android.content.ContentResolver
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.repository.database.AppDatabase
import com.cleverapp.repository.tagservice.TagService
import java.util.*

class RepositoryImpl(
        private val contentResolver: ContentResolver,
        database: AppDatabase,
        private val tagService: TagService)
    : Repository {

    private val databaseHelper = DatabaseHelper(database)

    private val tagFetchingResult = MutableLiveData<TagFetchingResult>()
    private val taggedImagesUpdated = MutableLiveData<Boolean>()

    override fun getTagFetchingResultLiveData(): LiveData<TagFetchingResult> {
        return tagFetchingResult
    }

    override fun getTaggedImagesChangedLiveData(): LiveData<Boolean> {
        return taggedImagesUpdated
    }

    override fun fetchTagsForImage(uri: Uri) {
        tagService.getImageTags(
                uriToFileBytes(uri),
                Observer {
                    if (!TextUtils.isEmpty(it.error))
                        tagFetchingResult.value = object: TagFetchingResult{
                            override fun getError(): String? {
                                return it.error
                            }
                        }
                    else {
                        tagFetchingResult.value = object: TagFetchingResult {
                            override fun getTaggedImages(): List<ImageTag>? {
                                return stringsToImageTags(
                                        UUID.randomUUID().toString(),
                                        it.tags)
                            }
                        }
                    }
                })
    }

    override fun saveTaggedImage(taggedImage: TaggedImage) {
        databaseHelper.insertTaggedImage(taggedImage)
        taggedImagesUpdated.value = true
    }

    override fun getSavedImages(): List<TaggedImage> {
        return databaseHelper.getAllTaggedImages()
    }

    override fun deleteSavedImage(image: TaggedImage) {
        databaseHelper.deleteSavedImage(image)
        taggedImagesUpdated.value = true
    }

    private fun stringsToImageTags(imageId: String, strings: Collection<String>?): List<ImageTag> {
        if (strings == null)
            return emptyList()
        return strings.fold(ArrayList(strings.size)){ acc, tag ->
            acc.add(ImageTag(imageId, tag))
            acc
        }
    }

    private fun uriToFileBytes(uri: Uri): ByteArray {
        return contentResolver.openInputStream(uri).readBytes()
    }
}