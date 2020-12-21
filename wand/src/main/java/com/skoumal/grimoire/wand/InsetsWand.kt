package com.skoumal.grimoire.wand

import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow

data class InsetsWand(
    val statusBars: Insets = Insets.NONE,
    val navigationBars: Insets = Insets.NONE,
    val captionBar: Insets = Insets.NONE,
    val ime: Insets = Insets.NONE,
    val systemGestures: Insets = Insets.NONE,
    val mandatorySystemGestures: Insets = Insets.NONE,
    val tappableElement: Insets = Insets.NONE,
    val displayCutout: Insets = Insets.NONE,
    val systemBars: Insets = Insets.NONE,
) {

    constructor(
        insets: WindowInsetsCompat
    ) : this(
        insets.getInsets(WindowInsetsCompat.Type.statusBars()),
        insets.getInsets(WindowInsetsCompat.Type.navigationBars()),
        insets.getInsets(WindowInsetsCompat.Type.captionBar()),
        insets.getInsets(WindowInsetsCompat.Type.ime()),
        insets.getInsets(WindowInsetsCompat.Type.systemGestures()),
        insets.getInsets(WindowInsetsCompat.Type.mandatorySystemGestures()),
        insets.getInsets(WindowInsetsCompat.Type.tappableElement()),
        insets.getInsets(WindowInsetsCompat.Type.displayCutout()),
        insets.getInsets(WindowInsetsCompat.Type.systemBars()),
    )

    companion object {

        @Volatile
        private var savedInsets: InsetsWand? = null

        operator fun invoke(
            activity: ComponentActivity,
            consumeInsets: Boolean = false
        ) = invoke(
            view = activity.findViewById(android.R.id.content),
            consumeInsets = consumeInsets
        )

        operator fun invoke(
            fragment: Fragment,
            consumeInsets: Boolean = true
        ) = invoke(
            view = fragment.requireView(),
            consumeInsets = consumeInsets
        )

        operator fun invoke(view: View, consumeInsets: Boolean = true): Flow<InsetsWand> {
            val currentInsets = savedInsets
            val channel = Channel<InsetsWand>()

            if (currentInsets != null) {
                channel.offer(currentInsets)
            }

            ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
                with(InsetsWand(insets)) {
                    channel.offer(this)
                    savedInsets = this
                }

                if (consumeInsets) WindowInsetsCompat.CONSUMED
                else insets
            }

            return channel.consumeAsFlow()
        }

    }


}