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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.ui.recyclerview.AppItemTouchHelper
import com.cleverapp.ui.recyclerview.TagsAdapter
import com.cleverapp.ui.viewmodels.EditImageViewModel

class EditImageFragment: BaseFragment() {

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

        fun getArgsForSavedImage(imageId: String): Bundle {
            return newBundle(null, imageId)
        }

        private fun isNewImage(args: Bundle): Boolean {
            return args.containsKey(ARG_KEY_URI)
        }
    }

    override val viewId: Int
        get() = R.layout.edit_image_fragment

    private lateinit var preview: ImageView
    private lateinit var tags: RecyclerView
    private lateinit var cancel: Button
    private lateinit var save: Button

    private lateinit var tagsAdapter: TagsAdapter

    private val viewModel: EditImageViewModel by getViewModel(EditImageViewModel::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        preview = view.findViewById(R.id.preview)
        tags = view.findViewById(R.id.tags)
        cancel = view.findViewById(R.id.button_cancel)
        save = view.findViewById(R.id.button_save)

        tags.layoutManager = LinearLayoutManager(activity)
        tagsAdapter = TagsAdapter().also { adapter ->
            adapter.getIsEmptyLiveData().observeForever {
                save.isEnabled = it == false
            }
            adapter.setOnEditTagClickedCallback { imageTag -> onEditTagClicked(imageTag) }
        }

        tags.adapter = tagsAdapter
        AppItemTouchHelper(
                tagsAdapter,
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN or ItemTouchHelper.UP,
                true)
                .attachToRecyclerView(tags)

        cancel.setOnClickListener{ navController.popBackStack() }
        save.setOnClickListener {
            viewModel.onSaveClicked()
            navController.popBackStack()
        }

        return view
    }

    private fun onEditTagClicked(imageTag: ImageTag) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResume() {
        super.onResume()
        observeData()
        if (isJustCreated()) {
            when{
                isNewImage(arguments!!) ->
                    viewModel.loadTaggedImage(arguments!!.getParcelable(ARG_KEY_URI) as Uri)
                else ->
                    viewModel.loadTaggedImage(arguments!!.getString(ARG_KEY_IMAGE_ID)!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateTagsOrdering(tagsAdapter.getItems())
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
                    tagsAdapter.setItems(it)
                }
        )
    }
}