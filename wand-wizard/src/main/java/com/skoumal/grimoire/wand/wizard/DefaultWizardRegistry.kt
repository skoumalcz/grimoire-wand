package com.skoumal.grimoire.wand.wizard

import android.accounts.AccountManager
import android.content.Context

internal class DefaultWizardRegistry(
    context: Context,
    type: String,
    resolver: WizardAccountResolver
) : WizardRegistry {

    private val manager = AccountManager.get(context)
    private val accountHolder = WizardAccountHolder(manager, resolver, type)
    private val extraManager = WizardExtraManager(manager, accountHolder)
    private val accountManager = WizardAccountManager(manager, accountHolder, extraManager)

    override fun removeWizard(wizard: Wizard?) {
        accountManager.remove()
    }

    override fun putWizard(wizard: Wizard): Boolean {
        return accountManager.create(wizard)
    }

    override fun getWizard(vararg extra: NamedExtra): Wizard? {
        return accountManager.get(*extra)
    }

    override fun putExtra(named: NamedExtra, value: String?) {
        extraManager[named] = value
    }

    override fun getExtra(named: NamedExtra): String? {
        return extraManager[named]
    }

}
