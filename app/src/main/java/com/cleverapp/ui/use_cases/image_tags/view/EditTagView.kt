package com.cleverapp.ui.use_cases.image_tags.view

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.cleverapp.R
import com.cleverapp.utils.getColorByAttr
import kotlinx.android.synthetic.main.edit_tag_view.view.*

class EditTagView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val inputManager: InputMethodManager

    private val inputWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            save_tag.isEnabled = !s.isNullOrBlank()
        }

    }

    init{
        LayoutInflater.from(context).inflate(R.layout.edit_tag_view, this, true)
        inputManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val colorStateList = ColorStateList(
                arrayOf(
                        intArrayOf(android.R.attr.state_enabled),
                        intArrayOf(-android.R.attr.state_enabled)
                ),
                intArrayOf(
                        getColorByAttr(context, R.attr.colorAccent),
                        getColorByAttr(context, android.R.attr.textColorPrimaryDisableOnly)
                )
        )
        save_tag.setTextColor(colorStateList)
        (save_tag.background as GradientDrawable).setStroke(
                resources.getDimensionPixelSize(R.dimen.stroke_width_default),
                colorStateList)
        input.addTextChangedListener(inputWatcher)
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
        return input.text.toString().trim()
    }
}