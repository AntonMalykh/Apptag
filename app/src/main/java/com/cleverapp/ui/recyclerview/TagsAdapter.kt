package com.cleverapp.ui.recyclerview

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MOVE
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag
import java.util.*

private const val VIEW_TYPE_TAG = 0
private const val VIEW_TYPE_LOADING = 1
private val ITEM_LOADING = ImageTag("", "", false, 0)

class TagsAdapter(val recyclerView: RecyclerView): BaseAdapter<ImageTag>() {

    private var onEditTagClickedCallback: ((ImageTag) -> Unit)? = null

    override val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(TagsTouchCallback())

    fun setOnEditTagClickedCallback(callback: (ImageTag) -> Unit) {
        onEditTagClickedCallback = callback
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemsList.lastIndex
                    && itemsList[position] == ITEM_LOADING)
            VIEW_TYPE_LOADING
        else
            VIEW_TYPE_TAG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ImageTag> {
        return if (viewType == VIEW_TYPE_LOADING) LoadingViewHolder(parent) else TagViewHolder(parent)
    }

    fun updateTag(tag: ImageTag) {
        val position = itemsList.indexOf(tag)
        if (position >= 0)
            notifyItemChanged(position)
        else {
            itemsList.add(0, tag)
            notifyItemInserted(0)
        }
    }

    fun setProgressEnabled(enabled: Boolean) {
        with(itemsList) {
            if (enabled) {
                add(ITEM_LOADING)
                notifyItemInserted(size - 1)
            }
            else if (!isEmpty() && last() == ITEM_LOADING) {
                removeAt(lastIndex)
                notifyItemRemoved(lastIndex + 1)
            }
        }
    }

    inner class LoadingViewHolder(parent: ViewGroup):
            BaseViewHolder<ImageTag>(
                    parent,
                    R.layout.progress_view_holder)

    inner class TagViewHolder(parent: ViewGroup):
            BaseViewHolder<ImageTag>(
                    parent,
                    R.layout.tag_view_holder) {

        private val tag: TextView = itemView.findViewById(R.id.tag)
        private val edit: View = itemView.findViewById(R.id.edit)
        private val move: View = itemView.findViewById(R.id.move)

        init{
            itemView.isClickable = false
            itemView.setOnLongClickListener {
                itemTouchHelper.startDrag(this)
                true
            }
            itemView.setOnTouchListener { _, event ->
                if (event.action != ACTION_MOVE)
                    return@setOnTouchListener false
                return@setOnTouchListener recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE
            }
            move.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(this)
                }
                true
            }

        }

        override fun bindItem(item: ImageTag) {
            tag.text = item.tag
            tag.setTextColor(if (item.isCustom) Color.BLUE else Color.GRAY)
            edit.setOnClickListener { onEditTagClickedCallback?.invoke(item) }
        }
    }

    private inner class TagsTouchCallback: ItemTouchHelper.Callback() {

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            itemsList.removeAt(viewHolder.adapterPosition)
            notifyItemRemoved(viewHolder.adapterPosition)
        }

        override fun getMovementFlags(recyclerView: RecyclerView,
                                      viewHolder: RecyclerView.ViewHolder): Int {
            return makeMovementFlags(
                    ItemTouchHelper.DOWN
                            or ItemTouchHelper.UP,
                    ItemTouchHelper.RIGHT
                            or ItemTouchHelper.LEFT)
        }

        override fun onMove(recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            if (target.itemViewType == VIEW_TYPE_LOADING)
                return false
            Collections.swap(getItems(), viewHolder.adapterPosition, target.adapterPosition)
            notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun isLongPressDragEnabled(): Boolean = false

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (viewHolder == null || viewHolder.itemViewType == VIEW_TYPE_LOADING)
                return
            animateViewHolder(viewHolder, actionState)
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            animateViewHolder(viewHolder, ItemTouchHelper.ACTION_STATE_IDLE)
        }

        private fun animateViewHolder(viewHolder: RecyclerView.ViewHolder, actionState: Int) {
            fun colorFrom() = (viewHolder.itemView.background as? ColorDrawable)
                    ?.color
                    ?: Color.TRANSPARENT
            val resources = viewHolder.itemView.resources
            val colorTo = when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG ->
                    ActivityCompat.getColor(
                            viewHolder.itemView.context,
                            R.color.colorAccent_transparent)
                ItemTouchHelper.ACTION_STATE_SWIPE ->
                    ActivityCompat.getColor(
                            viewHolder.itemView.context,
                            R.color.colorError_transparent)
                else -> Color.TRANSPARENT

            }
            val colorAnimation = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    colorFrom(),
                    colorTo)
            colorAnimation.duration = resources.getInteger(R.integer.animation_duration_default).toLong() // milliseconds
            colorAnimation.addUpdateListener {
                animator -> viewHolder.itemView.setBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.start()
        }
    }
}
