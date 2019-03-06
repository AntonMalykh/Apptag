package com.cleverapp.ui.use_cases.image_tags.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.PopupMenu
import com.cleverapp.R
import com.cleverapp.repository.Language
import kotlinx.android.synthetic.main.ai_options.view.*

private const val MAX_TAGS_AMOUNT = 20

class AiOptionsView @JvmOverloads constructor(
                    context: Context,
                    attrs: AttributeSet? = null,
                    defStyleAttr: Int = 0):
        FrameLayout(context, attrs, defStyleAttr) {

    var language: Language = Language.English
        set(value) {
            field = value
            option_language.text = value.localizedName
        }

    var amount: Int = 5
        set(value) {
            field = value
            option_count.text = value.toString()
        }

    private val languageMenu: PopupMenu
    private val countMenu: PopupMenu

    private var onApplyClickListener: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.ai_options, this, true)

        languageMenu = PopupMenu(context, option_language, Gravity.CENTER_VERTICAL)
        for (index in 0..Language.values().lastIndex) {
            languageMenu.menu.add(0, 0, index, Language.values()[index].localizedName)
        }
        languageMenu.setOnMenuItemClickListener { item ->
            language = Language.values()[item.order]
            true
        }

        countMenu = PopupMenu(context, option_count, Gravity.CENTER_VERTICAL)
        for (order in 1..MAX_TAGS_AMOUNT) {
            countMenu.menu.add(0, 0, order, order.toString())
        }
        countMenu.setOnMenuItemClickListener { item ->
            amount = item.order
            true
        }

        option_language.setOnClickListener { languageMenu.show() }
        option_count.setOnClickListener { countMenu.show() }
        find_tags.setOnClickListener { onApplyClickListener?.invoke() }
    }

    fun setOnApplyClickListener(listener: () -> Unit){
        onApplyClickListener = listener
    }
}