package com.cleverapp.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.ui.recyclerview.TagsAdapter
import com.cleverapp.ui.view.EditTagView
import com.cleverapp.ui.viewmodels.TagsViewModel

class TagsFragment: BaseFragment() {

    companion object {
        const val ARG_KEY_URI = "ARG_KEY_URI"
        const val ARG_KEY_IMAGE_ID = "ARG_KEY_IMAGE_ID"

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

        fun getArgsForSavedImage(imageId: String): Bundle {
            return newBundle(null, imageId)
        }

        fun isNewImage(args: Bundle): Boolean {
            return args.containsKey(ARG_KEY_URI)
        }
    }

    override val viewId: Int
        get() = R.layout.tags_fragment

    private lateinit var preview: ImageView
    private lateinit var tags: RecyclerView
    private lateinit var cancel: Button
    private lateinit var save: Button
    private lateinit var editInput: EditTagView

    private lateinit var tagsAdapter: TagsAdapter

    private val viewModel: TagsViewModel by getViewModel(TagsViewModel::class.java)

    /**
     * tag that is currently being edited
     */
    private var currentEditedTag: ImageTag? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        preview = view.findViewById(R.id.preview)
        tags = view.findViewById(R.id.tags)
        cancel = view.findViewById(R.id.button_cancel)
        save = view.findViewById(R.id.button_save)
        editInput = view.findViewById(R.id.edit_input)

        tags.layoutManager = LinearLayoutManager(activity)
        tagsAdapter = TagsAdapter().also { adapter ->
            adapter.getIsEmptyLiveData().observeForever {
                save.isEnabled = it == false
            }
            adapter.setOnEditTagClickedCallback { imageTag -> startEditTag(imageTag) }
        }

        tags.adapter = tagsAdapter
        tagsAdapter.itemTouchHelper.attachToRecyclerView(tags)

        cancel.setOnClickListener{ navController.popBackStack() }
        save.setOnClickListener {
            viewModel.saveImageTags(tagsAdapter.getItems())
            navController.popBackStack()
        }

        editInput.apply {
            this.visibility = GONE
            this.setOnOkClickedListener { finishEditTag(true) }
            this.setOnEmptySpaceClickListner { finishEditTag(false) }
        }

        if (isNewImage(arguments!!))
            Glide.with(this)
                    .load(arguments!!.getParcelable(ARG_KEY_URI) as Uri)
                    .apply(RequestOptions.centerCropTransform())
                    .into(preview)

        view.findViewById<View>(R.id.add).setOnClickListener { viewModel.loadTags("", 1) }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (isJustCreated())
            observeData()
    }

    override fun onBackPressed(): Boolean {
        if (currentEditedTag != null) {
            finishEditTag(false)
            return true
        }
        return super.onBackPressed()
    }

    private fun observeData() {
        if (!isNewImage(arguments!!))
            viewModel.imageBytes.observe(
                    this,
                    Observer {
                        Glide.with(this)
                                .load(it)
                                .apply(RequestOptions.centerCropTransform())
                                .into(preview)
                    }
            )

        viewModel.loading.observe(
                this,
                Observer { loading ->
                    loading?.let {
                        tagsAdapter.setProgressEnabled(it)
                        tags.scrollToPosition(tagsAdapter.itemCount - 1)
                    }
                }
        )

        viewModel.imageTags.observe(
                this,
                Observer {
                    tagsAdapter.appendItems(it)
                }
        )
    }

    private fun finishEditTag(applyResult: Boolean) {
        if (currentEditedTag == null)
            return
        currentEditedTag?.let {
            it.tag = editInput.getInput()
            if (applyResult)
                tagsAdapter.updateTag(it)
            currentEditedTag = null
        }
        editInput.visibility = GONE
        editInput.setText("")
    }

    private fun startEditTag(imageTag: ImageTag) {
        currentEditedTag = imageTag
        editInput.setText(imageTag.tag)
        editInput.visibility = VISIBLE
        editInput.bringToFront()
    }
}