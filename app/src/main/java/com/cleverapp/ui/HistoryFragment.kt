package com.cleverapp.ui

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.ui.navigation.NavigationDirections
import com.cleverapp.ui.recyclerview.*
import com.cleverapp.ui.viewmodels.HistoryViewMode
import com.cleverapp.ui.viewmodels.HistoryViewModel
import com.cleverapp.utils.INTENT_IMAGE_TYPE
import com.cleverapp.utils.toPlainText


class HistoryFragment: BaseFragment() {

    override val viewId: Int
        get() = R.layout.history_fragment

    private companion object {
        const val PICK_IMAGE_REQUEST = 0
    }

    private lateinit var toolbar: Toolbar
    private lateinit var fab: View
    private lateinit var history: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var itemDecoration: SpacesItemDecoration

    private val viewModel: HistoryViewModel by getViewModel(HistoryViewModel::class.java)

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

    private val onImageClickListener: OnImageClickListener = object : OnImageClickListener {
        override fun onImageClicked(image: TaggedImage) {
            navController.navigate(NavigationDirections.historyToEditSavedImage(image.id))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        toolbar = view.findViewById(R.id.toolbar)
        fab = view.findViewById(R.id.fab)
        history = view.findViewById(R.id.history)

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
                    adapter.setOnImageClickListener(onImageClickListener)
                    adapter.getIsEmptyLiveData()
                            .observeForever { history.visibility = if (it) GONE else VISIBLE }
                }

        layoutManager = GridLayoutManager(activity, HistoryViewMode.SingleColumn.spanCount)

        AppItemTouchHelper(
                historyAdapter,
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN
                        or ItemTouchHelper.UP
                        or ItemTouchHelper.START
                        or ItemTouchHelper.END)
                .attachToRecyclerView(history)

        fab.setOnClickListener { openFileChooser() }

        return view
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateImageOrdering(historyAdapter.items)
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

    private fun observeViewModel() {
        viewModel.getImagesLiveData().observe(this, Observer { historyAdapter.items = it })

        viewModel.getViewModeLiveData().observe(this, Observer { applyViewMode(it) })
    }

    private fun openFileChooser() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = INTENT_IMAGE_TYPE

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = INTENT_IMAGE_TYPE

        val chooserIntent = Intent.createChooser(getIntent, getString(R.string.image_chooser_title))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICK_IMAGE_REQUEST || resultCode != RESULT_OK || data == null)
            return

        val location : Uri? = data.data
        location?.let { navController.navigate(NavigationDirections.historyToEditNewImage(it)) }
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

