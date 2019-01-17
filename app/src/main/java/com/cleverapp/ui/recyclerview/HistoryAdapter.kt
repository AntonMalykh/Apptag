package com.cleverapp.ui.recyclerview

import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.utils.toPlainText

class HistoryAdapter: BaseAdapter<TaggedImage, HistoryAdapter.HistoryViewHolder>() {

    private var onImageClickListener: OnImageClickListener? = null
    private var onMenuClickListener: OnImageMenuClickListener? = null

    var layoutParamsProvider: LayoutParamsProvider? = null

    fun setOnMenuClickListener(onMenuClickListener: OnImageMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener
    }

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val holder = HistoryViewHolder(parent)
        layoutParamsProvider?.let {
            holder.itemView.layoutParams = it.getLayoutParams()
        }
        return holder
    }

    override fun bindViewHolder(holder: HistoryViewHolder, item: TaggedImage){
        Glide.with(holder.preview)
                .load(item.previewBytes)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.preview)
        holder.itemView.setOnClickListener{ onImageClickListener?.onImageClicked(item)}
        holder.itemView.setOnLongClickListener { holder.menu.callOnClick() }
        holder.tags.text = item.tags.toPlainText()
        holder.menu.setOnClickListener {
            val menu = PopupMenu(holder.menu.context, holder.menu)
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

    class HistoryViewHolder(parent: ViewGroup):
            BaseViewHolder(
                    parent, R.layout.history_view_holder) {

        val preview: ImageView = itemView.findViewById(R.id.preview)
        val tags: TextView = itemView.findViewById(R.id.tags)
        val menu: ImageButton = itemView.findViewById(R.id.menu)
    }
}

interface OnImageMenuClickListener {
    fun onRemoveClicked(image: TaggedImage)
    fun onCopyClicked(image: TaggedImage)
}

interface OnImageClickListener {
    fun onImageClicked(image: TaggedImage)
}