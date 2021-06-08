package com.skoumal.grimoire.wand.wizard

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import org.jetbrains.annotations.TestOnly

internal class WizardAccountHolder(
    private val manager: AccountManager,
    private val resolver: WizardAccountResolver,
    private val type: String
) {

    private var account: Account? = null

    @Throws(NoSuchElementException::class)
    fun get(): Account {
        return account ?: find().also {
            account = it
        }
    }

    @Throws(NoSuchElementException::class)
    private fun find(): Account {
        return manager
            .getAccountsByType(type)
            .first { resolver.performFilter(manager, it) }
    }

    fun invalidate() {
        account = null
    }

    @TestOnly
    fun create(account: Account, password: String, extra: Bundle?): Boolean {
        return manager.addAccountExplicitly(account, password, extra).also {
            invalidate()
        }
    }

    fun create(name: String, password: String, extra: Bundle?): Boolean {
        val account = Account(name, type)
        return create(account, password, extra)
    }

}

internal fun WizardAccountHolder.getOrNull(): Account? {
    return try {
        get()
    } catch (e: NoSuchElementException) {
        null
    }
}