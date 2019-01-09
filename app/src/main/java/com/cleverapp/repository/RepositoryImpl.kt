package com.cleverapp.repository

import android.content.ContentResolver
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    private val tagFetchingResult = MediatorLiveData<TagFetchingResult>()

    override fun getTagFetchingResultLiveData(): LiveData<TagFetchingResult> {
        return tagFetchingResult
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
                        val newId = UUID.randomUUID().toString()
                        databaseHelper.insertTaggedImage(
                                TaggedImage(
                                        newId,
                                        uri.toString(),
                                        stringsToImageTags(newId, it.tags)))
                        tagFetchingResult.value = object: TagFetchingResult {
                            override fun getTaggedImages(): List<ImageTag>? {
                                return databaseHelper.getTags(newId)
                            }
                        }
                    }
                })
    }

    override fun getSavedImages(): List<TaggedImage> {
        return databaseHelper.getAllTaggedImages()
    }

    override fun deleteSavedImage(image: TaggedImage) {
        return databaseHelper.deleteSavedImage(image)
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