package com.cleverapp.ui

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.ui.navigation.NavigationDirections
import com.cleverapp.ui.recyclerview.*
import com.cleverapp.ui.viewmodels.HistoryViewMode
import com.cleverapp.ui.viewmodels.ImagesViewModel
import com.cleverapp.utils.INTENT_IMAGE_TYPE
import com.cleverapp.utils.isHitAreaBelow
import com.cleverapp.utils.toPlainText
import kotlinx.android.synthetic.main.images_fragment.*


class ImagesFragment: BaseFragment() {

    override val viewId: Int
        get() = R.layout.images_fragment

    private companion object {
        const val REQUEST_PICK_IMAGE = 0
        const val REQUEST_TAKE_IMAGE = 1

        const val NEW_IMAGE_OPTION_PHOTO = 0
        const val NEW_IMAGE_OPTION_GALLERY = 1
    }

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var itemDecoration: SpacesItemDecoration

    private val viewModel: ImagesViewModel by getViewModel(ImagesViewModel::class.java)

    private var onMenuClickListener = object: OnImageMenuClickListener{
        override fun onRemoveClicked(image: TaggedImage) {
            viewModel.onRemoveClicked(image)
        }

        override fun onCopyClicked(image: TaggedImage) {
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .primaryClip =
                    ClipData.newPlainText("Image tags", image.tags.toPlainText())
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "#" + resources.getString(activity?.applicationInfo?.labelRes!!)
        toolbar.inflateMenu(R.menu.history_fragment_menu)
        toolbar.setOnMenuItemClickListener { viewModel.onGridMenuClicked(); true }

        history.setPadding(0, resources.getInteger(R.integer.history_item_space), 0, 0)
        itemDecoration = SpacesItemDecoration(
                resources.getInteger(R.integer.history_item_space),
                HistoryViewMode.SingleColumn)
        history.addItemDecoration(itemDecoration)

        historyAdapter = HistoryAdapter()
                .also { adapter ->
                    adapter.setOnMenuClickListener(onMenuClickListener)
                    adapter.setOnImageClickListener { taggedImage -> onImageClicked(taggedImage) }
                    adapter.getIsEmptyLiveData()
                            .observeForever { history.visibility = if (it) GONE else VISIBLE }
                }

        layoutManager = GridLayoutManager(activity, HistoryViewMode.SingleColumn.spanCount)

        historyAdapter.itemTouchHelper.attachToRecyclerView(history)

        multi_fab.apply {
            addOption(NEW_IMAGE_OPTION_PHOTO, R.drawable.ic_add_a_photo_white)
            addOption(NEW_IMAGE_OPTION_GALLERY, R.drawable.ic_photo_library_white)
            setOnOptionsClickListener {
                when (it) {
                    NEW_IMAGE_OPTION_GALLERY -> startPickingImage()
                    NEW_IMAGE_OPTION_PHOTO -> startTakingImage()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateImageOrdering(historyAdapter.getItems())
    }

    override fun onViewIsLaidOut() {
        super.onViewIsLaidOut()
        historyAdapter.layoutParamsProvider =
                view?.width?.let { LayoutParamsProvider(it, viewModel.getViewModeLiveData().value!!) }
        history.adapter = historyAdapter
        history.layoutManager = layoutManager
        observeViewModel()
        if (isJustCreated())
            viewModel.updateHistory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!isExpectedResult(requestCode) || resultCode != RESULT_OK || data == null)
            return

        val location : Uri? = data.data
        location?.let { navController.navigate(NavigationDirections.historyToEditNewImage(it)) }
    }

    override fun onTouchEvent(event: MotionEvent?) {
        if (event != null && !multi_fab.isHitAreaBelow(event.x.toInt(), event.y.toInt())) {
            multi_fab.collapse()
        }
        super.onTouchEvent(event)
    }

    private fun isExpectedResult(requestCode: Int): Boolean {
        return requestCode == REQUEST_TAKE_IMAGE || requestCode == REQUEST_PICK_IMAGE
    }

    private fun onImageClicked(taggedImage: TaggedImage) {
        navController.navigate(NavigationDirections.historyToEditSavedImage(taggedImage.id))
    }

    private fun applyViewMode(mode: HistoryViewMode): Boolean {
        toolbar.menu.findItem(R.id.grid).icon =
                resources.getDrawable(
                        if (mode == HistoryViewMode.SingleColumn) R.drawable.ic_grid_on_black
                        else R.drawable.ic_grid_off_black,
                        null)
        historyAdapter.layoutParamsProvider?.viewMode = mode
        layoutManager.spanCount = mode.spanCount
        itemDecoration.viewMode = mode
        history.adapter = historyAdapter
        history.layoutManager = layoutManager
        return true
    }

    private fun observeViewModel() {
        viewModel.getImagesLiveData().observe(
                this,
                Observer { historyAdapter.setItems(it) })

        viewModel.getViewModeLiveData().observe(this, Observer { applyViewMode(it) })
    }

    private fun startTakingImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {intent ->
            intent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(intent, REQUEST_TAKE_IMAGE)
            }
        }
    }

    private fun startPickingImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = INTENT_IMAGE_TYPE

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = INTENT_IMAGE_TYPE

        val chooserIntent = Intent.createChooser(getIntent, getString(R.string.image_chooser_title))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE)
    }
}

private class SpacesItemDecoration(
        private val space: Int,
        var viewMode: HistoryViewMode)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State) {

        outRect.bottom = space
        if (viewMode == HistoryViewMode.SingleColumn)
            return
        val position = parent.getChildAdapterPosition(view)
        val positionCenter = position - 1
        if (positionCenter == 0 || positionCenter % viewMode.spanCount == 0){
            outRect.left = space / 2
            outRect.right = space / 2
        }
        else if (position == 0 || position % viewMode.spanCount == 0){
            outRect.right = space / 2
        }
        else {
            outRect.left = space
            outRect.right = -space
        }
    }
}

