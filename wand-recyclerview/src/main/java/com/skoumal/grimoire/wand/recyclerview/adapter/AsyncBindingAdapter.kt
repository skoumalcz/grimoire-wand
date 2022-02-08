package com.skoumal.grimoire.wand.recyclerview.adapter

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.skoumal.grimoire.wand.recyclerview.ExtrasBinder
import com.skoumal.grimoire.wand.recyclerview.viewholder.BindingViewHolder
import java.lang.ref.WeakReference

/**
 * Adapter merging principles of [AsyncListDiffer] and [BindingViewHolder]
 *
 * @see AsyncListDiffer
 * @see BindingViewHolder
 * */
abstract class AsyncBindingAdapter<Data>(
    differ: DiffUtil.ItemCallback<Data>,
    lifecycleOwner: LifecycleOwner?,
    // don't weak reference binder, bcs it will get cleared if called through ExtrasBinder {}
    private val extrasBinder: ExtrasBinder? = null
) : RecyclerView.Adapter<BindingViewHolder<Data>>() {

    private val lifecycleOwner = WeakReference(lifecycleOwner)
    private val differ by lazy { AsyncListDiffer(this, differ) }

    val currentList: List<Data>
        get() = differ.currentList

    @Deprecated(
        "Use constructor with lifecycle owner to properly allow binding to (un)bind",
        level = DeprecationLevel.ERROR
    )
    constructor(
        differ: DiffUtil.ItemCallback<Data>,
        extrasBinder: ExtrasBinder? = null
    ) : this(
        differ = differ,
        lifecycleOwner = null,
        extrasBinder = extrasBinder
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<Data> {
        return BindingViewHolder(parent, viewType, lifecycleOwner.get(), extrasBinder)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<Data>, position: Int) {
        when (val item = getItemAt(position)) {
            null -> holder.onClearData()
            else -> holder.onBindData(item)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    abstract override fun getItemViewType(position: Int): Int

    fun getItemAt(position: Int): Data? {
        return currentList.getOrNull(position)
    }

    fun submitList(list: List<Data>) {
        differ.submitList(list)
    }

}