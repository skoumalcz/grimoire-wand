package com.skoumal.grimoire.wand.recyclerview.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.skoumal.grimoire.wand.recyclerview.BR
import com.skoumal.grimoire.wand.recyclerview.ExtrasBinder

/**
 * Very simple binding view holder designed only to reduce boilerplate. Its usage is identical to
 * the regular [ViewHolder][RecyclerView.ViewHolder] except it provides a [onBindData] method which
 * should be called within the scope of [RecyclerView.Adapter.onBindViewHolder].
 *
 * [onClearData] may or may not be called depending on whether adapter's implementation decides to use
 * it. It signals to the binding that the view can be hidden, invalidated, shimmering or defined in
 * other default behavior.
 *
 * Optionally you can provide a [ExtrasBinder] interface to assure binding to your view's external
 * data, such as viewModel's LiveData, Observable or similar.
 * */
open class BindingViewHolder<Data>(
    private val binding: ViewDataBinding,
    private val extrasBinder: ExtrasBinder? = null
) : RecyclerView.ViewHolder(binding.root) {

    constructor(
        parent: ViewGroup,
        layoutRes: Int,
        lifecycleOwner: LifecycleOwner?,
        extrasBinder: ExtrasBinder? = null
    ) : this(
        binding = createBinding(parent, layoutRes, lifecycleOwner),
        extrasBinder = extrasBinder
    )

    @Deprecated(
        "This constructor doesn't automatically assign lifecycleOwner to the binding, leaking the view.",
        level = DeprecationLevel.ERROR
    )
    constructor(
        parent: ViewGroup,
        layoutRes: Int,
        extrasBinder: ExtrasBinder? = null
    ) : this(
        parent = parent,
        layoutRes = layoutRes,
        lifecycleOwner = null,
        extrasBinder = extrasBinder,
    )

    open fun onBindData(data: Data) {
        binding.setVariable(BR.item, data)
        extrasBinder?.onBindExtras(binding)
    }

    open fun onClearData() {
        binding.setVariable(BR.item, null)
        extrasBinder?.onClearExtras(binding)
    }

    companion object {

        private fun createBinding(
            parent: ViewGroup,
            layoutRes: Int,
            owner: LifecycleOwner?
        ): ViewDataBinding {
            val inflater = parent.layoutInflater
            val binding: ViewDataBinding =
                DataBindingUtil.inflate(inflater, layoutRes, parent, false)

            binding.lifecycleOwner = owner

            return binding
        }

    }

}

private val View.layoutInflater get() = LayoutInflater.from(context)