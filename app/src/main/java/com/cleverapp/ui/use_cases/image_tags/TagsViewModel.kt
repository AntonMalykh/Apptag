package com.cleverapp.ui.use_cases.image_tags

import android.app.Application
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.cleverapp.repository.Language
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.ui.viewmodels.BaseViewModel
import java.io.File
import java.io.FileOutputStream


private const val PREFERENCE_KEY_TAG_LANGUAGE = "PREFERENCE_KEY_TAG_LANGUAGE"
private const val PREFERENCE_KEY_TAG_COUNT = "PREFERENCE_KEY_TAG_COUNT"

class TagsViewModel(app: Application,
                    tagsArguments: Bundle)
    : BaseViewModel(app) {

    val loading = MediatorLiveData<Boolean>()
    val imageTags = MediatorLiveData<List<ImageTag>>()
    val imageBytes = MutableLiveData<ByteArray>()
    val error = MutableLiveData<String>()

    private val imageRequest = MutableLiveData<Boolean>()
    private val imageLoading = Transformations.switchMap(imageRequest){
        loading.value = true
        repository.getImage(imageId!!)
    }
    private val tagsRequest = MutableLiveData<Boolean>()
    private var imageId: String? = null

    private var tagLanguage: Language = Language.valueOf(preferences.getString(PREFERENCE_KEY_TAG_LANGUAGE, Language.English.name)!!)
        set(value) {
            field = value
            preferences.edit().putString(PREFERENCE_KEY_TAG_LANGUAGE, value.name).apply()
        }

    private var tagCount: Int = preferences.getInt(PREFERENCE_KEY_TAG_COUNT, 5)
        set(value) {
            field = value
            preferences.edit().putInt(PREFERENCE_KEY_TAG_COUNT, value).apply()
        }

    init {
        loading.also { loading ->
            loading.addSource(tagsRequest){
                loading.value =  true
            }
        }
        imageTags.addSource(
                Transformations.switchMap(tagsRequest){
                    repository.getImageTags(imageBytes.value!!, tagLanguage, tagCount)
                })
        {
            loading.value = false
            if (TextUtils.isEmpty(it.getError()))
                imageTags.value = it.getTags()
            else
                error.value = it.getError()
        }

        when {
            TagsFragment.isNewImage(tagsArguments) ->
                TagsFragment.extractImageUri(tagsArguments)?.let { imageUri ->
                    imageBytes.value = repository.makeImageBytes(imageUri)
                    if (TagsFragment.isNewPhoto(tagsArguments))
                        repository.removeFromStorage(imageUri)
                }
            else ->
                TagsFragment.extractImageId(tagsArguments)?.let { imageId ->
                    this.imageId = imageId
                    imageLoading.observeForever {
                        imageBytes.value = it.previewBytes
                        imageTags.value = it.tags;
                        loading.value = false
                    }
                    imageRequest.value = true
                }
        }
    }

    fun loadTags(language: Language, count: Int) {
        tagLanguage = language
        tagCount = count
        tagsRequest.value = true
    }

    fun saveImageTags(currentUiOrder: List<ImageTag>) {
        if (imageId == null)
            repository.saveImage(imageBytes.value!!, currentUiOrder)
        else
            imageId?.let {
                repository.updateImage(it, updateTagsOrdering(currentUiOrder))
            }
    }

    fun removeImage(){
        imageId?.let { repository.removeImage(it) }
    }

    fun saveImageToStorage(): Boolean {
        var isSaved = false
        val picturesFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!picturesFile.exists())
            picturesFile.mkdir()
        File(picturesFile, "/Apptag").also {folder ->
            if (!folder.exists())
                folder.mkdir()
            File(folder, "Apptag + ${System.currentTimeMillis()}.jpeg").also {file ->
                if (file.exists())
                    file.delete()
                try {
                    FileOutputStream(file).use {
                        it.write(imageBytes.value)
                        isSaved = true
                    }
                }
                catch (ex: Exception) {

                }
                if (isSaved) {
                    MediaScannerConnection.scanFile(
                            getApplication(),
                            arrayOf(file.absolutePath),
                            null,
                            null)
                }
            }
        }
        return isSaved
    }

    fun getTagLanguage() = tagLanguage

    fun getTagCount() = tagCount


    private fun updateTagsOrdering(currentUiOrder: List<ImageTag>): List<ImageTag> {
        for (i in currentUiOrder.indices)
            currentUiOrder[i].ordinalNum = i
        return currentUiOrder
    }
}
