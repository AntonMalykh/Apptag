package com.cleverapp.ui.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavDirections
import com.cleverapp.R
import com.cleverapp.ui.BaseFragment
import com.cleverapp.ui.ImagePreviewFragment
import com.cleverapp.ui.ImagesFragment
import com.cleverapp.ui.TagsFragment
import com.cleverapp.ui.TagsFragment.Companion.getArgsForNewImage
import com.cleverapp.ui.TagsFragment.Companion.getArgsForSavedImage

class NavigationDirections{

    companion object {
        fun historyToEditSavedImage(imageId: String) = object: NavDirections {

            override fun getArguments() = getArgsForSavedImage(imageId)

            override fun getActionId() = R.id.navigate_images_to_tags
        }

        fun historyToEditNewImage(imageUri: Uri) = object: NavDirections {

            override fun getArguments() = getArgsForNewImage(imageUri)

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
