package com.skoumal.grimoire.wand.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skoumal.grimoire.wand.recyclerview.ExtrasBinder
import com.skoumal.grimoire.wand.recyclerview.diff.AdapterListDiffer
import com.skoumal.grimoire.wand.recyclerview.diff.SimpleAdapterListDiffer
import com.skoumal.grimoire.wand.recyclerview.diff.getItemAt
import com.skoumal.grimoire.wand.recyclerview.diff.size
import com.skoumal.grimoire.wand.recyclerview.viewholder.BindingViewHolder

/**
 * Adapter merging principles of [AdapterListDiffer] and [BindingViewHolder]
 *
 * @see AdapterListDiffer
 * @see BindingViewHolder
 * */
abstract class AsyncBindingAdapter<Data>(
    differ: DiffUtil.ItemCallback<Data>,
    private val extrasBinder: ExtrasBinder? = null
) : RecyclerView.Adapter<BindingViewHolder<Data>>(),
    AdapterListDiffer<Data> by SimpleAdapterListDiffer<Data>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<Data> {
        return BindingViewHolder(parent, viewType, extrasBinder)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<Data>, position: Int) {
        when (val item = getItemAt(position)) {
            null -> holder.onClearData()
            else -> holder.onBindData(item)
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    abstract override fun getItemViewType(position: Int): Int

}