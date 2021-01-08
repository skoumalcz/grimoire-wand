package com.skoumal.grimoire.wand.recyclerview

import androidx.databinding.ViewDataBinding

interface ExtrasBinder {

    fun onBindExtras(binding: ViewDataBinding)
    fun onClearExtras(binding: ViewDataBinding)

}

inline fun ExtrasBinder(
    crossinline onClearExtras: (binding: ViewDataBinding) -> Unit = {},
    crossinline onBindExtras: (binding: ViewDataBinding) -> Unit,
) = object : ExtrasBinder {
    override fun onBindExtras(binding: ViewDataBinding) {
        onBindExtras.invoke(binding)
    }

    override fun onClearExtras(binding: ViewDataBinding) {
        onClearExtras.invoke(binding)
    }
}
