package com.skoumal.grimoire.wand

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun Context.drawable(@DrawableRes res: Int) = AppCompatResources.getDrawable(this, res)

// region Lifecycle

val Activity.windowInsetsControllerCompat
    inline get() = ViewCompat.getWindowInsetsController(findViewById(android.R.id.content))

fun Activity.show(types: Int) {
    windowInsetsControllerCompat?.show(types)
}

fun Activity.hide(types: Int) {
    windowInsetsControllerCompat?.hide(types)
}


val Fragment.windowInsetsControllerCompat
    inline get() = activity?.windowInsetsControllerCompat

fun Fragment.show(types: Int) {
    windowInsetsControllerCompat?.show(types)
}

fun Fragment.hide(types: Int) {
    windowInsetsControllerCompat?.hide(types)
}

// endregion

// region Keyboard (IME)

@Suppress("NOTHING_TO_INLINE")
inline fun Activity.showIME() = show(WindowInsetsCompat.Type.ime())

@Suppress("NOTHING_TO_INLINE")
inline fun Activity.hideIME() = hide(WindowInsetsCompat.Type.ime())


@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.showIME() = show(WindowInsetsCompat.Type.ime())

@Suppress("NOTHING_TO_INLINE")
inline fun Fragment.hideIME() = hide(WindowInsetsCompat.Type.ime())

// endregion