package com.skoumal.grimoire.wand

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.sqrt

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