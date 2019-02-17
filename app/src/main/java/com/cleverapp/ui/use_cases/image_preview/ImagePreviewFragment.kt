package com.cleverapp.ui.use_cases.image_preview

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cleverapp.R
import com.cleverapp.ui.BaseFragment
import kotlinx.android.synthetic.main.image_preview_fragment.*

class ImagePreviewFragment : BaseFragment() {

    companion object {
        private const val ARG_KEY_BYTES = "ARG_KEY_BYTES"

        fun getArgs(imageBytes: ByteArray): Bundle {
            return Bundle().also { it.putByteArray(ARG_KEY_BYTES, imageBytes) }
        }
    }

    override val viewId: Int
        get() = R.layout.image_preview_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this)
                .load(getImageBytes())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(preview)
        close.setOnClickListener{
            if (isNavigationAllowed())
                navController.popBackStack()
        }
    }

    private fun getImageBytes(): ByteArray {
        return arguments!!.getByteArray(ARG_KEY_BYTES)
    }
}