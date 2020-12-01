package com.skoumal.grimoire.wand

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

sealed class ColorWand {

    abstract fun getColor(context: Context): ColorStateList

    class StateList(@JvmField @ColorRes private val res: Int) : ColorWand() {
        override fun getColor(context: Context): ColorStateList {
            return colorStateListCompat(context)
                ?: ColorStateList.valueOf(android.graphics.Color.BLACK)
        }

        private fun colorStateListCompat(context: Context) = try {
            AppCompatResources.getColorStateList(context, res)
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    class ColorInt(@JvmField private val color: Int) : ColorWand() {
        override fun getColor(context: Context): ColorStateList {
            return ColorStateList.valueOf(color)
        }
    }

    class Resource(@JvmField @ColorRes private val res: Int) : ColorWand() {
        override fun getColor(context: Context): ColorStateList {
            return ColorStateList.valueOf(colorCompat(context) ?: android.graphics.Color.BLACK)
        }

        private fun colorCompat(context: Context) = try {
            ContextCompat.getColor(context, res)
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    class Attribute(@JvmField @AttrRes private val res: Int) : ColorWand() {
        override fun getColor(context: Context): ColorStateList {
            return with(context.theme.obtainStyledAttributes(intArrayOf(res))) {
                getColorStateList(0)
                    ?: ColorStateList.valueOf(getColor(0, android.graphics.Color.BLACK))
            }
        }
    }

}
