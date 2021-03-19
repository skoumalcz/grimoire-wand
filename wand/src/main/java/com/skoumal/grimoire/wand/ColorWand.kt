package com.skoumal.grimoire.wand

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.sqrt

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

fun Context.colorStateList(colorRes: Int): ColorStateList =
    ColorWand.StateList(colorRes).getColor(this)

fun Context.color(colorRes: Int): Int =
    ColorWand.Resource(colorRes).getColor(this).defaultColor

fun Context.attribute(attrRes: Int): Int =
    ColorWand.Attribute(attrRes).getColor(this).defaultColor

// ---

fun asColor(int: Int): ColorWand =
    ColorWand.ColorInt(int)

fun asColorRes(res: Int): ColorWand =
    ColorWand.Resource(res)

fun asColorStateList(res: Int): ColorWand =
    ColorWand.StateList(res)

fun asColorAttr(attr: Int): ColorWand =
    ColorWand.Attribute(attr)

// ---

val Int.isDark
    get() = sqrt(
        red * red * .241 +
                green * green * .691 +
                blue * blue * .068
    ) < 130

val Int.isTransparent get() = alpha == 0
val Int.isTranslucent get() = alpha != 255

// ---

fun Resources.Theme.color(attr: Int) = TypedValue()
    .also { resolveAttribute(attr, it, true) }
    .data