package com.cleverapp.ui

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.ui.recyclerview.HistoryAdapter
import com.cleverapp.ui.recyclerview.OnImageMenuClickListener
import com.cleverapp.ui.viewmodels.HistoryViewModel
import com.cleverapp.ui.viewmodels.ViewModelFactory
import com.cleverapp.utils.INTENT_IMAGE_TYPE
import com.cleverapp.utils.toPlainText
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistoryFragment : BaseFragment() {

    private companion object {
        const val PICK_IMAGE_REQUEST = 0
    }

    private lateinit var fab : FloatingActionButton
    private lateinit var history : RecyclerView
    private lateinit var historyAdapter : HistoryAdapter

    private lateinit var viewModel : HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(activity!!.application))
                    .get(HistoryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.root_fragment, container, false)

        fab = view.findViewById(R.id.fab)
        history = view.findViewById(R.id.history)

        fab.setOnClickListener { openFileChooser() }

        historyAdapter = HistoryAdapter(Glide.with(this))
        historyAdapter.setOnMenuClickListener(
                object : OnImageMenuClickListener {
                    override fun onRemoveClicked(image: TaggedImage) {

                    }

                    override fun onCopyClicked(image: TaggedImage) {
                        (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                                .primaryClip =
                                    ClipData.newPlainText("Image tags", image.tags.toPlainText())
                    }
        })
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

        }
    }
}