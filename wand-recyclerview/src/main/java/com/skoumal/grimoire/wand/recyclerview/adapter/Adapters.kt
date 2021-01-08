package com.skoumal.grimoire.wand.recyclerview.adapter

import androidx.recyclerview.widget.RecyclerView

// classes
typealias AnyAdapter = RecyclerView.Adapter<*>
typealias Observer = RecyclerView.AdapterDataObserver

// functions
typealias OnPolicyChanged = (Observer) -> Unit
typealias OnChanged = (Observer) -> Unit
typealias OnItemRangeChanged = (observer: Observer, positionStart: Int, itemCount: Int, payload: Any?) -> Unit
typealias OnItemRangeInserted = (observer: Observer, positionStart: Int, itemCount: Int) -> Unit
typealias OnItemRangeRemoved = (observer: Observer, positionStart: Int, itemCount: Int) -> Unit
typealias OnItemRangeMoved = (observer: Observer, fromPosition: Int, toPosition: Int, itemCount: Int) -> Unit

fun AnyAdapter.registerAdapterDataObserver(
    onStateRestorationPolicyChanged: OnPolicyChanged = { onChanged(it) },
    onItemRangeChanged: OnItemRangeChanged = { a, _, _, _ -> onChanged(a) },
    onItemRangeInserted: OnItemRangeInserted = { a, _, _ -> onChanged(a) },
    onItemRangeRemoved: OnItemRangeRemoved = { a, _, _ -> onChanged(a) },
    onItemRangeMoved: OnItemRangeMoved = { a, _, _, _ -> onChanged(a) },
    onChanged: OnChanged,
): Observer = object : Observer() {

    override fun onChanged() =
        onChanged.invoke(this)

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) =
        onItemRangeChanged.invoke(this, positionStart, itemCount, null)

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) =
        onItemRangeChanged.invoke(this, positionStart, itemCount, payload)

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) =
        onItemRangeInserted.invoke(this, positionStart, itemCount)

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
        onItemRangeRemoved.invoke(this, positionStart, itemCount)

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) =
        onItemRangeMoved(this, fromPosition, toPosition, itemCount)

    override fun onStateRestorationPolicyChanged() =
        onStateRestorationPolicyChanged.invoke(this)

}.also {
    registerAdapterDataObserver(it)
    onChanged(it)
}

inline fun AnyAdapter.registerOnSizeChangedObserver(
    notifyOnce: Boolean = false,
    crossinline onSizeChanged: Observer.(Int) -> Unit
) = registerAdapterDataObserver {
    onSizeChanged(it, itemCount)
    if (notifyOnce) {
        unregisterAdapterDataObserver(it)
    }
}

inline fun AnyAdapter.registerOnAdapterEmptyObserver(
    notifyOnce: Boolean = false,
    crossinline onAdapterEmpty: Observer.() -> Unit
) = registerOnSizeChangedObserver {
    if (it <= 0) {
        onAdapterEmpty()
        if (notifyOnce) {
            unregisterAdapterDataObserver(this)
        }
    }
}

inline fun AnyAdapter.registerOnAdapterNotEmptyObserver(
    notifyOnce: Boolean = false,
    crossinline onAdapterNotEmpty: Observer.() -> Unit
) = registerOnSizeChangedObserver {
    if (it > 0) {
        onAdapterNotEmpty()
        if (notifyOnce) {
            unregisterAdapterDataObserver(this)
        }
    }
}