package com.skoumal.grimoire.wand.wizard

import android.os.Bundle

data class Wizard(
    val name: String,
    val password: String,
    val extra: Map<NamedExtra, String?> = emptyMap()
)

fun Map<NamedExtra, String?>.toBundle(): Bundle? {
    if (isEmpty()) {
        return null
    }

    val bundle = Bundle()

    for (entry in this) {
        if (entry.value == null) {
            continue
        }
        bundle.putString(entry.key.named, entry.value)
    }

    return bundle
}
