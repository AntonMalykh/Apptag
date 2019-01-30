package com.cleverapp.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.ui.recyclerview.TagsAdapter
import com.cleverapp.ui.viewmodels.TagsViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.tags_fragment.*

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

    private val viewModel: TagsViewModel by getViewModel(TagsViewModel::class.java)

    private lateinit var tagsAdapter: TagsAdapter
    /**
     * tag that is currently being edited
     */
    private var currentEditedTag: ImageTag? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black)
        toolbar.setNavigationOnClickListener { navController.popBackStack() }
        toolbar.inflateMenu(R.menu.menu_fragment_tags)
        toolbar.setOnMenuItemClickListener {
            when {
                it.isEnabled -> {
                    viewModel.saveImageTags(tagsAdapter.getItems())
                    navController.popBackStack()
                    true
                }
                else -> false
            }
        }

        app_bar_layout.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { _, offset ->
                    if (app_bar_layout == null)
                        return@OnOffsetChangedListener
                    // If toolbar expanded, it has blurry dark background.
                    // If collapsed - white. To display icons correctly, you need to
                    // change the color of the icons to opposite accordingly.
                    val ratio = Math.abs(offset.toFloat() / app_bar_layout.totalScrollRange)
                    val color = ColorUtils.blendARGB(Color.BLACK, Color.WHITE, ratio)
                    toolbar.menu.findItem(R.id.done).icon.setTint(color)
                    toolbar.navigationIcon?.setTint(color)
                })

        tags.layoutManager = LinearLayoutManager(activity)
        tagsAdapter = TagsAdapter().also { adapter ->
            adapter.getIsEmptyLiveData().observeForever {
                toolbar.menu.findItem(R.id.done).isEnabled = it == false
            }
            adapter.setOnEditTagClickedCallback { imageTag -> startEditTag(imageTag) }
        }

        tags.adapter = tagsAdapter
        tagsAdapter.itemTouchHelper.attachToRecyclerView(tags)

        edit_input.apply {
            this.visibility = GONE
            this.setOnOkClickedListener { finishEditTag(true) }
            this.setOnEmptySpaceClickListener { finishEditTag(false) }
        }

        if (isNewImage(arguments!!))
            Glide.with(this)
                    .load(arguments!!.getParcelable(ARG_KEY_URI) as Uri)
                    .apply(RequestOptions.centerCropTransform())
                    .into(preview)

        ai_options.apply {
            language = viewModel.getTagLanguage()
            count = viewModel.getTagCount()
            setOnApplyClickListener {
                visibility = GONE
                viewModel.loadTags(language, count)
            }
        }

        add.setOnClickListener {
            ai_options.visibility = VISIBLE
            ai_options.bringToFront()
        }
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
            it.tag = edit_input.getInput()
            if (applyResult)
                tagsAdapter.updateTag(it)
            currentEditedTag = null
        }
        edit_input.visibility = GONE
        edit_input.setText("")
    }

    private fun startEditTag(imageTag: ImageTag) {
        currentEditedTag = imageTag
        edit_input.setText(imageTag.tag)
        edit_input.visibility = VISIBLE
        edit_input.bringToFront()
    }
}