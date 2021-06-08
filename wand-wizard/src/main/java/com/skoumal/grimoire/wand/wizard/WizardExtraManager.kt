package com.skoumal.grimoire.wand.wizard

import android.accounts.AccountManager

internal class WizardExtraManager(
    private val manager: AccountManager,
    private val account: WizardAccountHolder
) {

    operator fun get(extra: NamedExtra): String? {
        val account = account.getOrNull() ?: return null
        return manager.getUserData(account, extra.named)
    }

    operator fun get(extras: Array<out NamedExtra>): Map<NamedExtra, String?> {
        if (extras.isEmpty()) {
            return emptyMap()
        }

        val map = mutableMapOf<NamedExtra, String?>()

        for (extra in extras) {
            map[extra] = get(extra)
        }

        return map
    }

    operator fun set(extra: NamedExtra, value: String?) {
        val account = account.getOrNull() ?: return
        manager.setUserData(account, extra.named, value)
    }

}