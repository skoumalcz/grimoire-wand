package com.skoumal.grimoire.wand.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Property allows consumers to create value dynamically with [creator] and dispose it automatically
 * with lifecycle hooks. Though this is designed to dispose the fields, it will only release the
 * stored [value].
 *
 * Release of the stored value is performed on [onDestroy] lifecycle callback and the value is then
 * initialized only if the lifecycle returns to _at least_ [Lifecycle.State.CREATED] state.
 *
 * This is only useful for values that **hold local context or views**! Using it for whatever else
 * types and values is an overkill and **wastes runtime memory**. Meaning - use this for adapters.
 *
 * @throws IllegalArgumentException If accessed beyond the lifecycle scope (ie. [Lifecycle.State.DESTROYED])
 * @throws Throwable If [creator] fails to provide the object it rethrows the exception
 * */
class AutoClean<T : Any> internal constructor(
    private val creator: () -> T
) : ReadOnlyProperty<LifecycleOwner, T>, DefaultLifecycleObserver {

    private var value: T? = null

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        val lifecycle = thisRef.lifecycle
        with(lifecycle) {
            removeObserver(this@AutoClean)
            addObserver(this@AutoClean)
        }

        require(lifecycle.currentState >= Lifecycle.State.INITIALIZED) {
            "Fetching this value requires at least initialized state!"
        }

        return synchronized(this) {
            value ?: creator().also { value = it }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        value = null
    }

}

/**
 * Property allows consumers to assign and fetch values and dispose it automatically with lifecycle
 * hooks. Though this is designed to dispose the fields, it will only release the stored [value].
 *
 * Release of the stored value is performed on [onDestroy] lifecycle callback and the value is then
 * initialized only if the lifecycle returns to _at least_ [Lifecycle.State.CREATED] state.
 *
 * This is only useful for values that **hold local context or views**! Using it for whatever else
 * types and values is an overkill and **wastes runtime memory**. Meaning - use this for adapters.
 *
 * Value can be assigned multiple times, keeping no history whatsoever. If, however, consumer
 * assigns the value in [Lifecycle.State.DESTROYED] state, it will be ignored, as callback has
 * already been called and it would likely cause memory leaks.
 *
 * @throws IllegalArgumentException If accessed beyond the lifecycle scope (ie. [Lifecycle.State.DESTROYED])
 * */
class MutableAutoClean<T : Any> internal constructor(
    default: T? = null
) : ReadWriteProperty<LifecycleOwner, T>, DefaultLifecycleObserver {

    @Volatile
    private var value: T? = default

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        val lifecycle = thisRef.lifecycle
        require(lifecycle.currentState >= Lifecycle.State.INITIALIZED) {
            "Cannot return value before lifecycle initializes"
        }
        return value
            ?: throw IllegalStateException("Cannot return internal value. It's never been assigned.")
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        val lifecycle = thisRef.lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            println("Saving value to ${MutableAutoClean::class.java} is not permitted after being destroyed. Ignoring value silently…")
            return
        }

        this.value = value
    }

    override fun onDestroy(owner: LifecycleOwner) {
        value = null
    }

}

/**
 * Provides common way to initialize [AutoClean] or [MutableAutoClean].
 * @see AutoClean
 * @see MutableAutoClean
 * */
fun <T : Any> autoClean(creator: () -> T) = AutoClean(creator)

/**
 * Provides common way to initialize [AutoClean] or [MutableAutoClean].
 * @see AutoClean
 * @see MutableAutoClean
 * */
fun <T : Any> autoClean(default: T? = null) = MutableAutoClean(default)
