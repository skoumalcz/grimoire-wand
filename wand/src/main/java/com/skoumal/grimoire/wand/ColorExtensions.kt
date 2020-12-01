package com.skoumal.grimoire.wand

import android.content.Context
import android.content.res.ColorStateList

fun Context.colorStateList(colorRes: Int): ColorStateList =
    ColorWand.StateList(colorRes).getColor(this)

fun Context.color(colorRes: Int): Int =
    ColorWand.Resource(colorRes).getColor(this).defaultColor

fun Context.attribute(attrRes: Int): Int =
    ColorWand.Attribute(attrRes).getColor(this).defaultColor

// ---

fun color(int: Int): ColorWand =
    ColorWand.ColorInt(int)

fun colorRes(res: Int): ColorWand =
    ColorWand.Resource(res)

fun colorStateList(res: Int): ColorWand =
    ColorWand.StateList(res)

fun colorAttr(attr: Int): ColorWand =
    ColorWand.Attribute(attr)