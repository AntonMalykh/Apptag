package com.cleverapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.ui.recyclerview.TagsAdapter
import com.cleverapp.ui.viewmodels.EditTagsViewModel
import com.squareup.picasso.Picasso

class EditTagsFragment: BaseFragment() {

    companion object {
        private const val ARG_KEY_URI = "ARG_KEY_URI"
        private const val ARG_KEY_IMAGE_ID = "ARG_KEY_IMAGE_ID"

        private fun newBundle(uri: Uri?, imageId: String?): Bundle {
            return Bundle().also { bundle ->
                    when{
                        uri != null -> bundle.putParcelable(ARG_KEY_URI, uri)
                        imageId != null -> bundle.putString(ARG_KEY_IMAGE_ID, imageId)
                    }
                }
        }

        fun getArgsForNewImage(imageUri: Uri): Bundle {
            return newBundle(imageUri, null)
        }

        fun getArgsForExistingImage(imageId: String): Bundle {
            return newBundle(null, imageId)
        }
    }

    private lateinit var preview: ImageView
    private lateinit var tags: RecyclerView
    private lateinit var cancel: Button
    private lateinit var save: Button

    private lateinit var tagsAdapter: TagsAdapter

    private val viewModel: EditTagsViewModel by getViewModel(EditTagsViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.edit_tags_fragment, container, false)

        preview = view.findViewById(R.id.preview)
        tags = view.findViewById(R.id.tags)
        cancel = view.findViewById(R.id.button_cancel)
        save = view.findViewById(R.id.button_save)

        tagsAdapter = TagsAdapter().apply {
            getIsEmptyLiveData().observeForever {
                save.isEnabled = it == false
            }
        }
        tags.adapter = tagsAdapter
        tags.layoutManager = LinearLayoutManager(activity)

        cancel.setOnClickListener{ navController.popBackStack() }
        save.setOnClickListener {
            viewModel.onSaveClicked()
            navController.popBackStack()
        }

        observeData()
        if (savedInstanceState == null)
            viewModel.getImageTags(arguments!!.getParcelable(ARG_KEY_URI))
        return view
    }

    private fun observeData() {
        viewModel.imageBytes.observe(
                this,
                Observer {
                    Glide.with(this)
                            .load(it)
                            .apply(RequestOptions.centerCropTransform())
                            .into(preview)
                }
        )

        viewModel.isLoadingTags.observe(
                this,
                Observer {
                    tags.visibility = if (it == true) INVISIBLE else VISIBLE
                }
        )

        viewModel.imageTags.observe(
                this,
                Observer {
                    tagsAdapter.items = it
                }
        )
    }
}