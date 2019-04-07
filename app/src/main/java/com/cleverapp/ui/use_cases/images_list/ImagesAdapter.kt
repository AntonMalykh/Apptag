package com.cleverapp.ui.use_cases.images_list

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.Image
import com.cleverapp.ui.view.recyclerview.BaseAdapter
import com.cleverapp.ui.view.recyclerview.BaseViewHolder
import com.cleverapp.utils.toPlainText
import java.util.*

class ImagesAdapter: BaseAdapter<Image>() {

    private companion object {
        const val ITEM_SELECTED_SCALE = 0.8f
        const val ITEM_NORMAL_SCALE = 1f
    }

    var layoutParamsProvider: LayoutParamsProvider? = null

    private var onImageClickListener: ((Image) -> Unit)? = null
    private var onImageDoubleClickListener: ((Image) -> Unit)? = null
    private var onMenuClickListener: OnImageMenuClickListener? = null
    private var mode = Mode.Normal
    private val selectedImages = mutableListOf<Image>()

    override val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.Callback(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // not supported
                }

                override fun getMovementFlags(recyclerView: RecyclerView,
                                              viewHolder: RecyclerView.ViewHolder): Int {
                    return makeFlag(
                            ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN
                                    or ItemTouchHelper.UP
                                    or ItemTouchHelper.START
                                    or ItemTouchHelper.END)
                }

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    Collections.swap(getItems(), viewHolder.adapterPosition, target.adapterPosition)
                    notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Image> {
        val holder = ImageViewHolder(parent)
        layoutParamsProvider?.let {
            holder.itemView.layoutParams = it.getLayoutParams()
        }
        return holder
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<Image>) {
        super.onViewAttachedToWindow(holder)
        if (holder is ImageViewHolder && holder.mod != mode) {
            holder.setMode(mode)
        }
    }

    fun setOnMenuClickListener(onMenuClickListener: OnImageMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener
    }

    fun setOnImageClickListener(listener: (Image) -> Unit) {
        this.onImageClickListener = listener
    }

    fun setOnImageDoubleClickListener(listener: (Image) -> Unit) {
        this.onImageDoubleClickListener = listener
    }

    fun setMode(mode: Mode) {
        this.mode = mode
        if (mode == Mode.Normal)
            selectedImages.clear()
    }

    fun getSelectedImages(): List<Image> {
        return selectedImages
    }

    inner class ImageViewHolder(parent: ViewGroup):
            BaseViewHolder<Image>(
                    parent, R.layout.images_view_holder) {

        internal var mod: Mode = Mode.Normal

        private val preview: ImageView = itemView.findViewById(R.id.preview)
        private val tags: TextView = itemView.findViewById(R.id.tags)
        private val menu: ImageButton = itemView.findViewById(R.id.menu)
        private val check: View = itemView.findViewById(R.id.check)

        private var onSingleClick: () -> Unit = { onImageClickListener?.invoke(getItem()) }
        private val tapListener = object: GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                onSingleClick.invoke()
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                return onImageDoubleClickListener?.let {
                    it.invoke(getItem())
                    true
                } ?: false
            }
        }
        private val detector = GestureDetector(itemView.context, tapListener).apply{
            setOnDoubleTapListener(tapListener)
        }

        override fun bindItem(item: Image) {
            super.bindItem(item)
            Glide.with(preview)
                    .load(item.previewBytes)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(preview)
            tags.text = item.tags.toPlainText()
            menu.setOnClickListener { onMenuClicked() }
            setMode(mode, false)
            itemView.setOnTouchListener{ _, event -> detector.onTouchEvent(event) }
        }

        fun setMode(mode: Mode) = setMode(mode, true)

        private fun setMode(mode: Mode, animateChange: Boolean) {
            this.mod = mode
            when (mode) {
                Mode.Normal -> {
                     onSingleClick = { onImageClickListener?.invoke(getItem()) }
                    check.visibility = GONE
                }
                Mode.Remove -> {
                    onSingleClick = {
                        if (selectedImages.contains(getItem())){
                            selectedImages.remove(getItem())
                            check.isEnabled = false
                            setScale(false)
                        }
                        else {
                            selectedImages.add(getItem())
                            check.isEnabled = true
                            setScale(true)
                        }
                    }
                    check.visibility = VISIBLE
                    check.isEnabled = selectedImages.contains(getItem())
                }
            }
            setScale(selectedImages.contains(getItem()), animateChange)
        }

        private fun setScale(scaleInside: Boolean, animate: Boolean = true) {
            val scale = if (scaleInside) ITEM_SELECTED_SCALE else ITEM_NORMAL_SCALE
            if (animate) {
                itemView.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .start()
            }
            else
                itemView.apply { scaleX = scale; scaleY = scale }
        }

        private fun onMenuClicked() {
            val menu = PopupMenu(menu.context, menu)
            menu.inflate(R.menu.image_item_menu)
            menu.menu.findItem(R.id.copy).isVisible = !getItem().tags.isEmpty()
            menu.setOnMenuItemClickListener { menuItem ->
                return@setOnMenuItemClickListener when {
                    menuItem.itemId == R.id.remove -> {
                        onMenuClickListener?.onRemoveClicked(getItem())
                        true
                    }
                    menuItem.itemId == R.id.copy -> {
                        onMenuClickListener?.onCopyClicked(getItem())
                        true
                    }
                    else -> false
                }
            }
            menu.show()
        }
    }
}

interface OnImageMenuClickListener {
    fun onRemoveClicked(image: Image)
    fun onCopyClicked(image: Image)
}