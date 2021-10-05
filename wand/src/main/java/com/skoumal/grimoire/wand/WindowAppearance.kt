package com.skoumal.grimoire.wand

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsControllerCompat

abstract class WindowAppearance private constructor(
    private val activity: Activity
) {

    private val themeStatusBarColor: Int
        get() = activity.theme.color(android.R.attr.statusBarColor)

    private val themeNavigationBarColor: Int
        get() = activity.theme.color(android.R.attr.navigationBarColor)

    private val themeBackgroundColor: Int
        get() = activity.theme.color(R.attr.colorSurface)

    protected open val statusBarColor: Int
        get() = when {
            isStatusBarTransparent -> themeBackgroundColor
            else -> themeStatusBarColor
        }

    protected open val navigationBarColor: Int
        get() = when {
            isNavigationBarTransparent -> themeBackgroundColor
            else -> themeNavigationBarColor
        }

    protected open val isStatusBarLight
        get() = !statusBarColor.isDark

    protected open val isNavigationBarLight
        get() = !navigationBarColor.isDark

    protected val isStatusBarTransparent
        get() = themeStatusBarColor.isTransparent

    protected val isNavigationBarTransparent
        get() = themeNavigationBarColor.isTransparent

    protected val isStatusBarTranslucent
        get() = themeStatusBarColor.isTranslucent

    protected val isNavigationBarTranslucent
        get() = themeNavigationBarColor.isTranslucent

    protected open fun applyVisibility(window: Window, view: View) {
        if (!isStatusBarTranslucent || !isNavigationBarTranslucent) return
        view.systemUiVisibility = view.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun apply() {
        val window = activity.window
        val view = window.decorView

        val manager = WindowInsetsControllerCompat(window, view)

        manager.isAppearanceLightStatusBars = isStatusBarLight
        manager.isAppearanceLightNavigationBars = isNavigationBarLight

        applyVisibility(window, view)
    }


    private class Default(activity: Activity) : WindowAppearance(activity)

    @RequiresApi(Build.VERSION_CODES.Q)
    private class API29(activity: Activity) : WindowAppearance(activity) {

        override fun applyVisibility(window: Window, view: View) {
            super.applyVisibility(window, view)
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }

    }

    companion object {

        @JvmName("getInstance")
        operator fun invoke(activity: Activity) = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> API29(activity)
            else -> Default(activity)
        }

    }

}

fun Activity.applyWindowAppearance() = WindowAppearance(this).apply()