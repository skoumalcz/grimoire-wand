package com.skoumal.grimoire.wand

import android.content.res.Resources
import kotlin.math.ceil
import kotlin.math.roundToInt

fun Int.toDp(): Int = ceil(toFloat().toDp()).roundToInt()
fun Int.toPx(): Int = toFloat().toPx().roundToInt()

fun Float.toDp() = this / Resources.getSystem().displayMetrics.density
fun Float.toPx() = this * Resources.getSystem().displayMetrics.density