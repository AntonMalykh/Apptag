package com.cleverapp.ui.recyclerview

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag

private const val ADD_STUB_IMAGE_ID = "ADD"
private val ADD_STUB = ImageTag(ADD_STUB_IMAGE_ID)

private const val VIEW_TYPE_ADD_STUB = 0
private const val VIEW_TYPE_TAG = 1

class TagsAdapter: BaseAdapter<ImageTag>() {

    override var items = super.items
        get() {
            return if (field.isEmpty()) field else field.subList(1, field.size)
        }
        set(value) {
            field = value
            items.add(0, ADD_STUB)
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD_STUB else VIEW_TYPE_TAG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ImageTag> {
        return if (viewType == VIEW_TYPE_ADD_STUB) AddStubViewHolder(parent)
            else TagViewHolder(parent)
    }

    class TagViewHolder(parent: ViewGroup):
            BaseViewHolder<ImageTag>(
                    parent,
                    R.layout.tag_view_holder) {

        private val tag: TextView = itemView.findViewById(R.id.tag)
        private val edit: View = itemView.findViewById(R.id.edit)
        private val move: View = itemView.findViewById(R.id.move)

        override fun bindItem(item: ImageTag) {
            tag.text = item.tag
            edit.setOnClickListener {
                // TODO implement
            }
            move.setOnTouchListener { v, event ->
                // TODO implement
                true
            }
        }
    }

    class AddStubViewHolder(parent: ViewGroup) :
            BaseViewHolder<ImageTag>(
                    parent,
                    R.layout.add_tag_holder) {

        init {
            itemView.setOnClickListener {
                // TODO implement
            }
        }
    }

}
