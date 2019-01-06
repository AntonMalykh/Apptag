package com.cleverapp.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.cleverapp.R
import com.cleverapp.utils.INTENT_IMAGE_TYPE
import com.cleverapp.viewmodels.RootViewModel
import com.cleverapp.viewmodels.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RootFragment : BaseFragment() {

    private companion object {
        const val PICK_IMAGE_REQUEST = 0
    }

    private lateinit var fab : FloatingActionButton
    private lateinit var preview : AppCompatImageView
    private lateinit var progress : ProgressBar
    private lateinit var tags : AppCompatTextView

    private lateinit var viewModel : RootViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(repository))
                    .get(RootViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.root_fragment, container, false)

        preview = view.findViewById(R.id.preview)
        progress = view.findViewById(R.id.progress)
        tags = view.findViewById(R.id.tags)
        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener { openFileChooser() }

        progress.bringToFront()
        tags.bringToFront()

        observeData()
        return view
    }

    private fun observeData() {
        viewModel.imagePath.observe(
                this,
                Observer { Glide.with(this).load(it).into(preview) })
        viewModel.isFetchingTags.observe(
                this,
                Observer {
                    progress.visibility = if (it == true) VISIBLE else GONE
                    tags.visibility = if (it == true) GONE else VISIBLE
                })
        viewModel.imageTagResult.observe(
                this,
                Observer {
                    if(it.error != null)
                        Toast.makeText(activity, it.error, Toast.LENGTH_SHORT).show()
                    else
                        tags.text = toUiString(it.tags)
                })
    }

    private fun toUiString(tags: List<String>?): CharSequence? {
        return tags?.fold(StringBuilder()){
            acc, s ->
                acc.append('#').append(s)
        }
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
            viewModel.updateUri(it)
            viewModel.updateTags(it)
        }
    }
}
