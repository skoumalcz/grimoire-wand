package com.skoumal.grimoire.wand.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil

class SimpleDiffUtilCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val differ: DiffUtil.ItemCallback<T>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return when {
            old == null && new == null -> true
            old == null || new == null -> false
            else -> differ.areItemsTheSame(old, new)
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]

        return when {
            old == null && new == null -> true
            old == null || new == null -> false
            else -> differ.areContentsTheSame(old, new)
        }
    }

}