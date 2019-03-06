package com.cleverapp.ui.use_cases.images_list

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.cleverapp.R
import com.cleverapp.repository.data.Image
import com.cleverapp.ui.BaseFragment
import com.cleverapp.ui.navigation.NavigationDirections
import com.cleverapp.ui.use_cases.images_list.Mode.Normal
import com.cleverapp.ui.use_cases.images_list.Mode.Remove
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
        const val NEW_IMAGE_OPTION_FILES = 2
    }

    override val viewId: Int
        get() = R.layout.images_fragment

    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var itemDecoration: SpacesItemDecoration

    private val viewModel: ImagesViewModel by getViewModel(ImagesViewModel::class.java)

    private var capturePhotoUri: Uri? = null
    private var mode = Normal

    private var onMenuClickListener = object: OnImageMenuClickListener {
        override fun onRemoveClicked(image: Image) {
            viewModel.removeImage(image)
        }

        override fun onCopyClicked(image: Image) {
            (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .primaryClip =
                    ClipData.newPlainText("Image tags", image.tags.toPlainText())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "#" + resources.getString(activity?.applicationInfo?.labelRes!!)
        toolbar.inflateMenu(R.menu.menu_images_fragment)
        toolbar.setOnMenuItemClickListener { onMenuItemClicked(it) }

        images.setPadding(0, resources.getInteger(R.integer.history_item_space), 0, 0)
        itemDecoration = SpacesItemDecoration(
                resources.getInteger(R.integer.history_item_space),
                HistoryViewMode.SingleColumn)
        images.addItemDecoration(itemDecoration)

        imagesAdapter = ImagesAdapter()
                .also { adapter ->
                    adapter.setOnMenuClickListener(onMenuClickListener)
                    adapter.setOnImageClickListener(::onImageClicked)
                    adapter.setOnImageDoubleClickListener(::onImageDoubleClicked)
                    adapter.getIsEmptyLiveData()
                            .observeForever { images.visibility = if (it) INVISIBLE else VISIBLE }
                }

        layoutManager = GridLayoutManager(activity, HistoryViewMode.SingleColumn.spanCount)

        imagesAdapter.itemTouchHelper.attachToRecyclerView(images)

        multi_fab.apply {
            addOption(NEW_IMAGE_OPTION_FILES, R.drawable.ic_outline_folder_open_white)
            addOption(NEW_IMAGE_OPTION_PHOTO, R.drawable.ic_outline_add_a_photo_white)
            addOption(NEW_IMAGE_OPTION_GALLERY, R.drawable.ic_outline_photo_library_white)
            setOnOptionsClickListener {
                when (it) {
                    NEW_IMAGE_OPTION_GALLERY -> startPickingImageFromGallery()
                    NEW_IMAGE_OPTION_FILES -> startPickingImageFromFiles()
                    NEW_IMAGE_OPTION_PHOTO -> startTakingPhoto()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeViewModel()
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateImageOrdering(imagesAdapter.getItems())
    }

    override fun onViewIsLaidOut() {
        super.onViewIsLaidOut()
        imagesAdapter.layoutParamsProvider =
                view?.width?.let { LayoutParamsProvider(it, viewModel.getViewModeLiveData().value!!) }
        images.adapter = imagesAdapter
        images.layoutManager = layoutManager
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!isExpectedResult(requestCode) || resultCode != RESULT_OK) {
            capturePhotoUri = null
            return
        }

        val isMultipleImages = data?.clipData?.let { it.itemCount > 1 } ?: false

        if (isMultipleImages) {
            data?.clipData?.let {
                if (it.itemCount == 0)
                    return@let
                val newUris = mutableListOf<Uri>()
                for (index in 0 until it.itemCount){
                    newUris.add(it.getItemAt(index).uri)
                }
                viewModel.addImage(newUris)
            }
        }
        else {
            val isPhoto = requestCode == REQUEST_TAKE_PHOTO
            val location: Uri? = if (isPhoto) capturePhotoUri else data?.data
            location?.let {
                if (isNavigationAllowed())
                    navController.navigate(NavigationDirections.historyToEditNewImage(it, isPhoto))
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

    override fun onBackPressed(): Boolean {
        return when{
            mode != Normal -> {
                setMode(Normal)
                true
            }
            multi_fab.isExpanded() -> {
                multi_fab.collapse()
                true
            }
            else -> super.onBackPressed()
        }
    }

    override fun onPermissionGranted(permission: String) {
        super.onPermissionGranted(permission)
        when (permission) {
            WRITE_EXTERNAL_STORAGE -> startTakingPhoto()
        }
    }

    private fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.grid -> viewModel.changeGrid()
            R.id.delete -> {
                if (mode == Remove)
                    viewModel.removeImages(imagesAdapter.getSelectedImages())
                setMode(if (mode == Normal) Remove else Normal)
            }
        }
        return true
    }

    private fun setMode(mode: Mode) {
        this.mode = mode
        val removeMenuItem = toolbar.menu.findItem(R.id.delete)
        imagesAdapter.setMode(mode)
        for (index in 0 until images.childCount) {
            with (images.getChildViewHolder(images.getChildAt(index))) {
                when (this) {
                    is ImagesAdapter.ImageViewHolder -> this.setMode(mode)
                }
            }
        }
        when (mode) {
            Normal -> {
                removeMenuItem.icon = ActivityCompat.getDrawable(activity!!, R.drawable.ic_select_multiple)
                multi_fab.show()
            }
            Remove -> {
                with(AnimatedVectorDrawableCompat.create(
                        activity!!,
                        R.drawable.ic_delete_animated)) {
                    removeMenuItem.icon = this
                    this?.start()
                }
                multi_fab.hide()
            }
        }
    }

    private fun isExpectedResult(requestCode: Int): Boolean {
        return requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_PICK_IMAGE
    }

    private fun onImageClicked(image: Image) {
        if (isNavigationAllowed())
            navController.navigate(NavigationDirections.historyToEditSavedImage(image.id))
    }

    private fun onImageDoubleClicked(image: Image) {
        if (isNavigationAllowed())
            navController.navigate(NavigationDirections.toImagePreview(this.javaClass, image.previewBytes))
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
        images.adapter = imagesAdapter
        images.layoutManager = layoutManager
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
                            EXTERNAL_CONTENT_URI,
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

    private fun startPickingImageFromGallery() {
        Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI).also{
            it.type = INTENT_IMAGE_TYPE
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(it, REQUEST_PICK_IMAGE)
        }
    }

    private fun startPickingImageFromFiles() {
        Intent(Intent.ACTION_GET_CONTENT).also{
            it.type = INTENT_IMAGE_TYPE
            it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(it, REQUEST_PICK_IMAGE)
        }
    }
}

enum class Mode {
    Normal,
    Remove
}