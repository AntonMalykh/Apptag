package com.cleverapp.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag
import com.cleverapp.ui.navigation.NavigationDirections
import com.cleverapp.ui.recyclerview.TagsAdapter
import com.cleverapp.ui.viewmodels.TagsViewModel
import com.cleverapp.utils.isVisibleAreaContains
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.tags_fragment.*
class TagsFragment: BaseFragment() {

    companion object {
        private const val ARG_KEY_URI = "ARG_KEY_URI"
        private const val ARG_KEY_IMAGE_ID = "ARG_KEY_IMAGE_ID"

        private const val NEW_TAG_OPTION_ENTER = 0
        private const val NEW_TAG_OPTION_AI = 1

        private const val IMAGE_EXPAND_DELAY = 300L

        private fun newBundle(uri: Uri? = null, imageId: String? = null): Bundle {
            return Bundle().also { bundle ->
                    when{
                        uri != null -> bundle.putParcelable(ARG_KEY_URI, uri)
                        imageId != null -> bundle.putString(ARG_KEY_IMAGE_ID, imageId)
                    }
                }
        }

        fun getArgsForNewImage(imageUri: Uri): Bundle {
            return newBundle(uri = imageUri)
        }

        fun getArgsForSavedImage(imageId: String): Bundle {
            return newBundle(imageId = imageId)
        }

        fun extractImageUri(args: Bundle): Uri? {
            return args.getParcelable(ARG_KEY_URI)
        }

        fun extractImageId(args: Bundle): String? {
            return args.getString(ARG_KEY_IMAGE_ID)
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

    private val onPreviewDoubleTapDetector =
            with (
                    object: GestureDetector.SimpleOnGestureListener(){
                        override fun onDoubleTap(e: MotionEvent?): Boolean {
                            return if (isNavigationAllowed() && viewModel.imageBytes.value != null) {
                                navController.navigate(
                                        NavigationDirections.toImagePreview(
                                                this@TagsFragment.javaClass,
                                                viewModel.imageBytes.value!!))
                                true
                            }
                            else
                                false
                        }
                    })
            {
                GestureDetector(activity, this).also { it.setOnDoubleTapListener(this) }
            }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black)
        toolbar.setNavigationOnClickListener {
            if (isNavigationAllowed())
                navController.popBackStack()
        }
        toolbar.inflateMenu(R.menu.menu_tags_fragment)
        toolbar.setOnMenuItemClickListener {
            when {
                it.itemId == R.id.done -> {
                    if (!isNavigationAllowed())
                        return@setOnMenuItemClickListener false
                    viewModel.saveImageTags(tagsAdapter.getItems())
                    navController.popBackStack()
                    true
                }
                else -> false
            }
        }

        app_bar_layout.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appbar, offset ->
                    if (this@TagsFragment.view == null)
                        return@OnOffsetChangedListener
                    // If toolbar expanded, it has blurry dark background.
                    // If collapsed - white. To display icons correctly, you need to
                    // change the color of the icons to opposite accordingly.
                    val ratio = Math.abs(offset.toFloat() / appbar.totalScrollRange)
                    val color = ColorUtils.blendARGB(Color.WHITE, Color.BLACK, ratio)
                    toolbar.menu.findItem(R.id.done).icon.setTint(color)
                    toolbar.navigationIcon?.setTint(color)

                    multi_fab.scaleX = 1-ratio
                    multi_fab.scaleY = 1-ratio
                    empty.translationY =
                            (appbar.totalScrollRange - toolbar.height) * (1 + ratio/4)
                })

        tagsAdapter = TagsAdapter(tags).also { adapter ->
            adapter.getIsEmptyLiveData().observeForever {
                empty.visibility = if (it) VISIBLE else GONE
            }
            adapter.setOnEditTagClickedCallback { imageTag -> startEditTag(imageTag) }
        }

        tags.adapter = tagsAdapter
        tagsAdapter.itemTouchHelper.attachToRecyclerView(tags)


        if (isNewImage(arguments!!)) {
            Glide.with(this)
                    .load(arguments!!.getParcelable(ARG_KEY_URI) as Uri)
                    .apply(RequestOptions.centerCropTransform())
                    .into(preview)
        }

        edit_input.apply {
            visibility = GONE
            setOnOkClickedListener { finishEditTag(true) }
            setOnEmptySpaceClickListener { finishEditTag(false) }
        }

        ai_options.apply {
            language = viewModel.getTagLanguage()
            count = viewModel.getTagCount()
            setOnApplyClickListener {
                visibility = GONE
                viewModel.loadTags(language, count)
            }
            setOnEmptySpaceClickListener { visibility = GONE }
        }

        multi_fab.apply {
            addOption(NEW_TAG_OPTION_ENTER, R.drawable.ic_add_white)
            addOption(NEW_TAG_OPTION_AI, R.drawable.ic_ai_recognition_white)
            setOnOptionsClickListener {
                when(it) {
                    NEW_TAG_OPTION_ENTER -> startEditTag(ImageTag())
                    NEW_TAG_OPTION_AI -> startAddingTagsWithAi()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isJustCreated())
            observeData()
    }

    override fun onBackPressed(): Boolean {
        if (edit_input.visibility == VISIBLE) {
            finishEditTag(false)
            return true
        }
        else if (ai_options.visibility == VISIBLE) {
            ai_options.visibility = GONE
            return true
        }
        return super.onBackPressed()
    }

    override fun onTouchEvent(event: MotionEvent?) {
        if (event != null && !multi_fab.isVisibleAreaContains(event.x.toInt(), event.y.toInt()))
            multi_fab.collapse()
        super.onTouchEvent(event)
    }

    private fun observeData() {
        viewModel.imageBytes.observe(
                this,
                Observer {
                    Glide.with(this)
                            .load(it)
                            .apply(RequestOptions.centerCropTransform())
                            .into(preview)
                    app_bar_layout.postDelayed(
                            { app_bar_layout?.setExpanded(true, true) },
                            IMAGE_EXPAND_DELAY)
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
        currentEditedTag?.let {
            it.tag = edit_input.getInput()
            it.isCustom = true
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

    private fun startAddingTagsWithAi() {
        ai_options.visibility = VISIBLE
        ai_options.bringToFront()
    }
}

