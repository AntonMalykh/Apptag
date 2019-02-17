package com.cleverapp.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavDirections
import com.cleverapp.R
import com.cleverapp.ui.BaseFragment
import com.cleverapp.ui.use_cases.image_preview.ImagePreviewFragment
import com.cleverapp.ui.use_cases.image_tags.TagsFragment
import com.cleverapp.ui.use_cases.image_tags.TagsFragment.Companion.getArgsForNewImage
import com.cleverapp.ui.use_cases.image_tags.TagsFragment.Companion.getArgsForSavedImage
import com.cleverapp.ui.use_cases.images_list.ImagesFragment

class NavigationDirections{

    companion object {
        fun historyToEditSavedImage(imageId: String) = object: NavDirections {

            override fun getArguments() = getArgsForSavedImage(imageId)

            override fun getActionId() = R.id.navigate_images_to_tags
        }

        fun historyToEditNewImage(imageUri: Uri, isNewPhoto: Boolean) = object: NavDirections {

            override fun getArguments() = getArgsForNewImage(imageUri, isNewPhoto)

            override fun getActionId() = R.id.navigate_images_to_tags
        }

        fun toImagePreview(cls: Class<in BaseFragment>, image: ByteArray) = object: NavDirections {

            override fun getArguments(): Bundle = ImagePreviewFragment.getArgs(image)

            override fun getActionId(): Int {
                return when (cls) {
                    ImagesFragment::class.java -> R.id.navigate_images_to_imagePreview
                    TagsFragment::class.java -> R.id.navigate_tags_to_imagePreview
                    else -> throw IllegalStateException(
                            String.format("Invalid navigation source %s", cls))
                }
            }
        }
    }
}
