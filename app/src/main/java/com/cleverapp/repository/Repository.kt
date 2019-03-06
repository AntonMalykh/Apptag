package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.Image

interface Repository {

    fun getImagesChangedLiveData(): LiveData<Boolean>

    fun getImage(imageId: String): LiveData<Image>

    fun getImages(): LiveData<List<Image>>

    fun saveImage(previewBytes: ByteArray, tags: List<ImageTag>)

    fun saveImages(imageUriList: List<Uri>)

    fun removeImage(image: Image)

    fun removeImage(imageId: String)

    fun removeImages(images: Collection<Image>)

    fun updateImage(imageId: String, newTags: List<ImageTag>)

    fun updateImages(imagesToUpdate: Collection<Image>)

    fun getImageTags(imageBytes: ByteArray,
                     tagsLanguage: Language,
                     tagsCount: Int)
            : LiveData<TagsLoadingResult>

    fun makeImageBytes(uri: Uri): ByteArray

    fun removeFromStorage(imageUri: Uri)
}