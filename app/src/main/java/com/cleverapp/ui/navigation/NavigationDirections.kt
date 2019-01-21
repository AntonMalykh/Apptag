package com.cleverapp.ui.navigation

import android.net.Uri
import androidx.navigation.NavDirections
import com.cleverapp.R
import com.cleverapp.ui.EditImageFragment.Companion.getArgsForNewImage
import com.cleverapp.ui.EditImageFragment.Companion.getArgsForSavedImage

class NavigationDirections{

    companion object {
        fun historyToEditSavedImage(imageId: String) = object: NavDirections {

            override fun getArguments() = getArgsForSavedImage(imageId)

            override fun getActionId() = R.id.navigate_history_to_editTags
        }

        fun historyToEditNewImage(imageUri: Uri) = object: NavDirections {

            override fun getArguments() = getArgsForNewImage(imageUri)

            override fun getActionId() = R.id.navigate_history_to_editTags
        }
    }
}
