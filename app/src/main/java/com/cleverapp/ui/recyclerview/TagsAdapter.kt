package com.cleverapp.ui.recyclerview

import android.view.ViewGroup
import android.widget.TextView
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag

class TagsAdapter: BaseAdapter<ImageTag, TagsAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(parent)
    }

    override fun bindViewHolder(holder: TagViewHolder, item: ImageTag) {
        holder.tag.text = item.tag
    }

    class TagViewHolder(parent: ViewGroup):
            BaseViewHolder(
                    parent,
                    R.layout.tag_view_holder) {

        val tag: TextView = itemView.findViewById(R.id.tag)
    }

}
