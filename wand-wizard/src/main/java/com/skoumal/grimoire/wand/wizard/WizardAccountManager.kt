package com.skoumal.grimoire.wand.wizard

import android.accounts.AccountManager
import android.os.Build
import android.os.Handler
import android.os.Looper

internal class WizardAccountManager(
    private val manager: AccountManager,
    private val account: WizardAccountHolder,
    private val extra: WizardExtraManager
) {

    fun get(vararg extras: NamedExtra): Wizard? {
        val account = account.getOrNull() ?: return null
        val password = manager.getPassword(account) ?: return null
        val extra = extra[extras]
        return Wizard(
            name = account.name.orEmpty(),
            password = password,
            extra = extra
        )
    }

    fun remove(handler: Handler = Handler(Looper.getMainLooper())) {
        var removable = account.getOrNull()

        while (removable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                manager.removeAccountExplicitly(removable)
            } else {
                @Suppress("DEPRECATION")
                manager.removeAccount(removable, null, handler)
            }

            account.invalidate()
            removable = account.getOrNull()
        }

        account.invalidate()
    }

    fun create(wizard: Wizard): Boolean {
        return account.create(
            name = wizard.name,
            password = wizard.password,
            extra = wizard.extra.toBundle()
        )
    }

}