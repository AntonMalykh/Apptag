package com.cleverapp.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.*
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.ui.navigation.NavigationDirections
import com.cleverapp.ui.recyclerview.ImagesAdapter
import com.cleverapp.ui.recyclerview.LayoutParamsProvider
import com.cleverapp.ui.recyclerview.OnImageMenuClickListener
import com.cleverapp.ui.viewmodels.HistoryViewMode
import com.cleverapp.ui.viewmodels.ImagesViewModel
import com.cleverapp.utils.INTENT_IMAGE_TYPE
import com.cleverapp.utils.isVisibleAreaContains
import com.cleverapp.utils.toPlainText
import kotlinx.android.synthetic.main.images_fragment.*
import java.text.SimpleDateFormat
import java.util.*


class ImagesFragment: BaseFragment() {

    private companion object {

        const val REQUEST_PICK_IMAGE = 0
        const val REQUEST_TAKE_PHOTO = 1
        const val NEW_IMAGE_OPTION_PHOTO = 0

        const val NEW_IMAGE_OPTION_GALLERY = 1
    }

    override val viewId: Int
        get() = R.layout.images_fragment

    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var itemDecoration: SpacesItemDecoration

    private val viewModel: ImagesViewModel by getViewModel(ImagesViewModel::class.java)

    private var capturePhotoUri: Uri? = null

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
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.grid -> {
                    viewModel.onGridMenuClicked()
                    (toolbar.menu.findItem(R.id.delete).icon as AnimatedVectorDrawable).reset()
                }
                R.id.delete -> {
                    (it.icon as AnimatedVectorDrawable).start()
                }
            }
            true
        }

        history.setPadding(0, resources.getInteger(R.integer.history_item_space), 0, 0)
        itemDecoration = SpacesItemDecoration(
                resources.getInteger(R.integer.history_item_space),
                HistoryViewMode.SingleColumn)
        history.addItemDecoration(itemDecoration)

        imagesAdapter = ImagesAdapter()
                .also { adapter ->
                    adapter.setOnMenuClickListener(onMenuClickListener)
                    adapter.setOnImageClickListener { taggedImage -> onImageClicked(taggedImage) }
                    adapter.getIsEmptyLiveData()
                            .observeForever { history.visibility = if (it) INVISIBLE else VISIBLE }
                }

        layoutManager = GridLayoutManager(activity, HistoryViewMode.SingleColumn.spanCount)

        imagesAdapter.itemTouchHelper.attachToRecyclerView(history)

        multi_fab.apply {
            addOption(NEW_IMAGE_OPTION_PHOTO, R.drawable.ic_add_a_photo_white)
            addOption(NEW_IMAGE_OPTION_GALLERY, R.drawable.ic_photo_library_white)
            setOnOptionsClickListener {
                when (it) {
                    NEW_IMAGE_OPTION_GALLERY -> startPickingImage()
                    NEW_IMAGE_OPTION_PHOTO -> startTakingPhoto()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateImageOrdering(imagesAdapter.getItems())
    }

    override fun onViewIsLaidOut() {
        super.onViewIsLaidOut()
        imagesAdapter.layoutParamsProvider =
                view?.width?.let { LayoutParamsProvider(it, viewModel.getViewModeLiveData().value!!) }
        history.adapter = imagesAdapter
        history.layoutManager = layoutManager
        observeViewModel()
        if (isJustCreated())
            viewModel.updateHistory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!isExpectedResult(requestCode) || resultCode != RESULT_OK)
            return

        val isMultipleImages = data?.clipData?.itemCount != null

        if (isMultipleImages) {
            data?.clipData?.let {
                if (it.itemCount == 0)
                    return@let
                val newUris = mutableListOf<Uri>()
                for (index in 0 until it.itemCount){
                    newUris.add(it.getItemAt(index).uri)
                }
                viewModel.onImagesAdded(newUris)
            }
        }
        else {
            val location: Uri? = if (requestCode == REQUEST_TAKE_PHOTO) capturePhotoUri else data?.data
            location?.let {
                if (isNavigationAllowed())
                    navController.navigate(NavigationDirections.historyToEditNewImage(it))
            }
        }
        capturePhotoUri = null
    }

    override fun onTouchEvent(event: MotionEvent?) {
        if (event != null && !multi_fab.isVisibleAreaContains(event.x.toInt(), event.y.toInt())) {
            multi_fab.collapse()
        }
        super.onTouchEvent(event)
    }

    override fun onPermissionGranted(permission: String) {
        super.onPermissionGranted(permission)
        when (permission) {
            WRITE_EXTERNAL_STORAGE -> startTakingPhoto()
        }
    }

    private fun isExpectedResult(requestCode: Int): Boolean {
        return requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_IMAGE
    }

    private fun onImageClicked(taggedImage: TaggedImage) {
        if (isNavigationAllowed())
            navController.navigate(NavigationDirections.historyToEditSavedImage(taggedImage.id))
    }

    private fun applyViewMode(mode: HistoryViewMode): Boolean {
        toolbar.menu.findItem(R.id.grid).icon =
                resources.getDrawable(
                        if (mode == HistoryViewMode.SingleColumn) R.drawable.ic_grid_on_black
                        else R.drawable.ic_grid_off_black,
                        null)
        imagesAdapter.layoutParamsProvider?.viewMode = mode
        layoutManager.spanCount = mode.spanCount
        itemDecoration.viewMode = mode
        history.adapter = imagesAdapter
        history.layoutManager = layoutManager
        return true
    }

    private fun observeViewModel() {
        viewModel.getImagesLiveData().observe(
                this,
                Observer { imagesAdapter.setItems(it) })

        viewModel.getViewModeLiveData().observe(this, Observer { applyViewMode(it) })
    }

    private fun startTakingPhoto() {
        if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
            val contentValues = ContentValues().also {
                val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                                .format(Date())
                it.put(MediaStore.Images.Media.TITLE, "Apptag_$timeStamp")
                it.put(MediaStore.Images.Media.DATE_ADDED, timeStamp)
            }
            capturePhotoUri =
                    activity?.contentResolver?.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues)
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturePhotoUri)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.resolveActivity(activity!!.packageManager)?.also {
                    activity?.grantUriPermission(
                            it.packageName,
                            capturePhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO)
                }
            }
        }
        else
            requestPermission(WRITE_EXTERNAL_STORAGE)
    }

    private fun startPickingImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = INTENT_IMAGE_TYPE
        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = INTENT_IMAGE_TYPE
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

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

