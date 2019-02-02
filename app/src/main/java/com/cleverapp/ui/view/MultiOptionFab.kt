package com.cleverapp.ui.view

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.LinearLayoutCompat
import com.cleverapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MultiOptionFab@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
    : LinearLayoutCompat(context, attrs, defStyleAttr),
        View.OnClickListener {

    private val fab: FloatingActionButton = FloatingActionButton(context)
    private val optionButtons = mutableListOf<FloatingActionButton>()
    private val optionIds = mutableListOf<Int>()
    private var isExpanded = false
    private var onOptionsClickListener: ((optionId: Int) -> Unit)? = null

    init{
        fab.setImageResource(R.drawable.ic_add_white)
        fab.setOnClickListener(this)
        addView(fab)
        layoutTransition = LayoutTransition()
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (!hasFocus() && isExpanded)
            collapse()
    }

    override fun onClick(v: View?) {
        when (v) {
            fab -> expand()
            else -> {
                collapse()
                onOptionsClickListener?.invoke(optionIds[optionButtons.indexOf(v)])
            }
        }
    }

    fun setOnOptionsClickListener(listener: (optionId: Int) -> Unit) {
        onOptionsClickListener = listener
    }

    fun expand() {
        requestFocus()
        removeView(fab)
        optionButtons.forEach { addView(it) }
        isExpanded = true
    }

    fun collapse() {
        optionButtons.forEach{ removeView(it) }
        if (!fab.isAttachedToWindow)
            addView(fab)
        isExpanded = false
    }

    fun addOption(optionId: Int, @DrawableRes iconResId: Int) {
        val wasExpanded = isExpanded
        if (wasExpanded)
            collapse()
        val fab = FloatingActionButton(context)
        fab.setImageResource(iconResId)
        fab.setOnClickListener(this)
        val index = optionIds.indexOf(optionId)
        when (index) {
            -1 -> {
                optionIds.add(optionId)
                optionButtons.add(fab)
            }
            else -> optionButtons[index] = fab
        }
        if (wasExpanded)
            expand()
    }
}