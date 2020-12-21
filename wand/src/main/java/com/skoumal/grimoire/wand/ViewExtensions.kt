package com.skoumal.grimoire.wand

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Tries best effort to return [controller][WindowInsetsController] in three possible ways:
 *
 * 1) Returns [controller][WindowInsetsController] immediately
 *
 *    - Whenever this function returns the [controller][WindowInsetsController] immediately, it
 *    means that the view is laid out and ready for animations or other view manipulations.
 *
 * 2) Returns [controller][WindowInsetsController] _after_ getting notified by [view's observer][ViewTreeObserver]
 *
 *    - View might not be laid out just yet (typically if called immediately after attaching the
 *    view in onViewCreated [fragment] / onCreate [Activity]), so the function tries to hold the
 *    return statement until after the notification from view observer
 *    - Additionally this might still not return any observer
 *
 * 3) Waits for the second pass of notifications from [view's observer][ViewTreeObserver]
 *
 *    - This step is for the most part identical to 2), except whenever this time view returns null
 *    the function throws [IllegalArgumentException]
 * */
@RequiresApi(Build.VERSION_CODES.R)
@Throws(IllegalArgumentException::class)
suspend fun View.awaitWindowInsetsController(): WindowInsetsController {

    suspend fun await() = suspendCoroutine<WindowInsetsController?> {
        setRemovingOnGlobalLayoutListener {
            it.resume(windowInsetsController)
        }
    }

    return windowInsetsController ?: await() ?: requireNotNull(await())
}

fun <V : View> V.setRemovingOnGlobalLayoutListener(listener: RemovingOnGlobalLayoutListener) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            listener.onGlobalLayout()
        }
    })
}

fun interface RemovingOnGlobalLayoutListener : ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout()
}