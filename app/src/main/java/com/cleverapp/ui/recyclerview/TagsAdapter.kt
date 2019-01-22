package com.cleverapp.ui.recyclerview

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag


class TagsAdapter: BaseAdapter<ImageTag>() {

    private var tagRemovedCallback: ((ImageTag) -> Unit)? = null
    private var editTagClickedCallback: ((ImageTag) -> Unit)? = null

    fun setOnTagRemovedCallback(callback:(ImageTag) -> Unit) {
        tagRemovedCallback = callback
    }

    fun setOnEditTagClickedCallback(callback: (ImageTag) -> Unit) {
        editTagClickedCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ImageTag> {
        return TagViewHolder(parent)
    }

    inner class TagViewHolder(parent: ViewGroup):
            BaseViewHolder<ImageTag>(
                    parent,
                    R.layout.tag_view_holder) {

        private val tag: TextView = itemView.findViewById(R.id.tag)
        private val edit: View = itemView.findViewById(R.id.edit)
        private val move: View = itemView.findViewById(R.id.move)

        override fun bindItem(item: ImageTag) {
            tag.text = item.tag
            edit.setOnClickListener {
                editTagClickedCallback?.invoke(item)
            }
            move.setOnTouchListener { v, event ->
                // TODO implement
                true
            }
        }
    }
}
