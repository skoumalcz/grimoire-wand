package com.skoumal.grimoire.wand.recyclerview

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

@Suppress("MemberVisibilityCanBePrivate", "FunctionName")
object RecyclerViewDispatchers {

    /** Provides single shared thread dispatcher specified as per [Single] method */
    @JvmField
    val Single = Single()

    /** Provides a new single threaded executor disguised as a coroutine dispatcher */
    fun Single() = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

}