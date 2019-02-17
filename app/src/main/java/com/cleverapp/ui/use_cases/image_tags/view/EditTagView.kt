package com.cleverapp.ui.use_cases.image_tags.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.cleverapp.R

class EditTagView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private var input: EditText
    private var ok: View

    init{
        val view = LayoutInflater.from(context).inflate(R.layout.edit_tag_view, this, true)
        input = view.findViewById(R.id.input)
        ok = view.findViewById(R.id.ok)
    }

    fun setOnOkClickedListener(listener: (View) -> Unit) {
        ok.setOnClickListener(listener)
    }

    fun setOnEmptySpaceClickListener(listener: (View) -> Unit) {
        setOnClickListener(listener)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return input.requestFocus()
    }

    fun setText(text: String) {
        input.setText(text)
    }

    fun getInput(): String {
        return input.text.toString()
    }
}