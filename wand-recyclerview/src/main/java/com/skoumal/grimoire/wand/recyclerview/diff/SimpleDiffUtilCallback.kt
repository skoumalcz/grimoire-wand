package com.skoumal.grimoire.wand.recyclerview.diff

import androidx.recyclerview.widget.DiffUtil

class SimpleDiffUtilCallback<T : Any>(
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
        return differ.areItemsTheSame(
            oldList[oldItemPosition],
            newList[newItemPosition]
        )
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return differ.areContentsTheSame(
            oldList[oldItemPosition],
            newList[newItemPosition]
        )
    }

}