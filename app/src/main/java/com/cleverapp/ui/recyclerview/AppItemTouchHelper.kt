package com.cleverapp.ui.recyclerview

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class AppItemTouchHelper<T: BaseAdapter<out Any>> (
        adapter: T,
        actionState: Int,
        directions: Int,
        disableLongPress: Boolean = false)
    : ItemTouchHelper(Callback(adapter, actionState, directions, disableLongPress))

private class Callback(
        private val adapter: BaseAdapter<out Any>,
        private val actionState: Int,
        private val directions: Int,
        private val disableLongPress: Boolean)
    : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeFlag(actionState, directions)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        Collections.swap(adapter.items, viewHolder.adapterPosition, target.adapterPosition)
        adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun isLongPressDragEnabled(): Boolean {
        return !disableLongPress
    }
}
