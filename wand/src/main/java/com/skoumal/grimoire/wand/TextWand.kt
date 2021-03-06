package com.skoumal.grimoire.wand

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class TextWand {

    abstract fun getText(resources: Resources): CharSequence

    protected fun Array<out Any>.resolveRecursively(resources: Resources) = arrayMap {
        if (it is TextWand) it.getText(resources)
        else it
    }

    private inline fun <T, reified R> Array<T>.arrayMap(element: (T) -> R) = Array(size) {
        element(this[it])
    }

    class Resource(
        @JvmField @StringRes private val res: Int,
        @JvmField private vararg val args: Any
    ) : TextWand() {
        override fun getText(resources: Resources): CharSequence {
            return resources.getString(res, *args.resolveRecursively(resources))
        }
    }

    class Quantity(
        @JvmField @PluralsRes private val res: Int,
        @JvmField private val quantity: Int,
        @JvmField private vararg val args: Any
    ) : TextWand() {
        override fun getText(resources: Resources): CharSequence {
            return resources.getQuantityString(res, quantity, *args.resolveRecursively(resources))
        }
    }

    class Sequence(
        @JvmField private val charSequence: CharSequence
    ) : TextWand() {
        override fun getText(resources: Resources): CharSequence {
            return charSequence
        }
    }

}