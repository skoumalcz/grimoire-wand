package com.skoumal.grimoire.wand

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class TextWand {

    abstract fun getText(resources: Resources): CharSequence

    protected fun Array<out Any>.resolveRecursively(resources: Resources) = map {
        if (it is TextWand) it.getText(resources)
        else it
    }.toTypedArray()

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