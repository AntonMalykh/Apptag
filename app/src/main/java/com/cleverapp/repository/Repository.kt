package com.cleverapp.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.repository.data.TaggedImage

interface Repository {

    fun getImagesChangedLiveData(): LiveData<Boolean>

    fun getImage(imageId: String): LiveData<TaggedImage>

    fun getImages(): LiveData<List<TaggedImage>>

    fun saveImage(previewBytes: ByteArray, tags: List<ImageTag>)

    fun saveImages(imageUriList: List<Uri>)

    fun removeImage(image: TaggedImage)

    fun removeImages(images: Collection<TaggedImage>)

    fun updateImage(imageId: String, newTags: List<ImageTag>)

    fun updateImages(imagesToUpdate: Collection<TaggedImage>)

    fun getImageTags(imageBytes: ByteArray,
                     tagsLanguage: Language,
                     tagsCount: Int)
            : LiveData<TagsLoadingResult>

    fun makeImageBytes(uri: Uri): ByteArray
}