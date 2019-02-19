package com.cleverapp.ui.use_cases.image_tags.view

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.cleverapp.R
import kotlinx.android.synthetic.main.edit_tag_view.view.*

class EditTagView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val inputManager: InputMethodManager

    init{
        LayoutInflater.from(context).inflate(R.layout.edit_tag_view, this, true)
        inputManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (isShown) {
            input.requestFocus()
            inputManager.showSoftInput(input, 0)
        }
        else
            inputManager.hideSoftInputFromWindow(input.windowToken, 0)
    }

    fun setOnSaveClickedListener(listener: (View) -> Unit) {
        save_tag.setOnClickListener(listener)
    }

    fun setText(text: String) {
        input.setText(text)
    }

    fun getInput(): String {
        return input.text.toString()
    }
}