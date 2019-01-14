package com.cleverapp.ui

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.ui.recyclerview.HistoryAdapter
import com.cleverapp.ui.recyclerview.OnImageClickListener
import com.cleverapp.ui.recyclerview.OnImageMenuClickListener
import com.cleverapp.ui.viewmodels.HistoryViewModel
import com.cleverapp.utils.INTENT_IMAGE_TYPE
import com.cleverapp.utils.toPlainText

class HistoryFragment: BaseFragment() {

    private companion object {
        const val PICK_IMAGE_REQUEST = 0
    }

    private lateinit var fab: View
    private lateinit var history: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

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
//            navController.navigate(
//                    R.id.navigate_history_to_editTags,
//                    EditTagsFragment.getArgsForExistingImage(image.id))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.history_fragment, container, false)

        fab = view.findViewById(R.id.fab)
        history = view.findViewById(R.id.history)

        fab.setOnClickListener { openFileChooser() }

        historyAdapter = HistoryAdapter()
                .also {
                    it.setOnMenuClickListener(onMenuClickListener)
                    it.setOnImageClickListener(onImageClickListener)
                }
        history.layoutManager = GridLayoutManager(activity, 2)
        history.adapter = historyAdapter

        observeData()
        viewModel.updateHistory()
        return view
    }

    private fun observeData() {
        viewModel.images.observe(
                this,
                Observer {
                    historyAdapter.items = it
                    history.visibility = if (historyAdapter.itemCount > 0) VISIBLE else INVISIBLE
                }
        )
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
        location?.let {
            navController.navigate(
                    R.id.navigate_history_to_editTags,
                    EditTagsFragment.getArgsForNewImage(location))
        }
    }
}
