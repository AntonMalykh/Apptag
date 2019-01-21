package com.cleverapp.ui.recyclerview

import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.utils.toPlainText

class HistoryAdapter: BaseAdapter<TaggedImage>() {

    private var onImageClickListener: OnImageClickListener? = null
    private var onMenuClickListener: OnImageMenuClickListener? = null

    var layoutParamsProvider: LayoutParamsProvider? = null

    fun setOnMenuClickListener(onMenuClickListener: OnImageMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener
    }

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TaggedImage> {
        val holder = HistoryViewHolder(parent)
        layoutParamsProvider?.let {
            holder.itemView.layoutParams = it.getLayoutParams()
        }
        return holder
    }

    inner class HistoryViewHolder(parent: ViewGroup):
            BaseViewHolder<TaggedImage>(
                    parent, R.layout.history_view_holder) {

        private val preview: ImageView = itemView.findViewById(R.id.preview)
        private val tags: TextView = itemView.findViewById(R.id.tags)
        private val menu: ImageButton = itemView.findViewById(R.id.menu)

        override fun bindItem(item: TaggedImage) {
            super.bindItem(item)
            Glide.with(preview)
                    .load(item.previewBytes)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(preview)
            itemView.setOnClickListener{ onImageClickListener?.onImageClicked(item) }
            tags.text = item.tags.toPlainText()
            menu.setOnClickListener {
                val menu = PopupMenu(menu.context, menu)
                menu.inflate(R.menu.image_item_menu)
                menu.setOnMenuItemClickListener { menuItem ->
                    return@setOnMenuItemClickListener when {
                        menuItem.itemId == R.id.remove -> {
                            onMenuClickListener?.onRemoveClicked(item)
                            true
                        }
                        menuItem.itemId == R.id.copy -> {
                            onMenuClickListener?.onCopyClicked(item)
                            true
                        }
                        else ->
                            false
                    }
                }
                menu.show()
            }
        }
    }
}

interface OnImageMenuClickListener {
    fun onRemoveClicked(image: TaggedImage)
    fun onCopyClicked(image: TaggedImage)
}

interface OnImageClickListener {
    fun onImageClicked(image: TaggedImage)
}