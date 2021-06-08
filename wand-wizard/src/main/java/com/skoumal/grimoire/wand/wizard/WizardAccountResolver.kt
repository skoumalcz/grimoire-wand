package com.skoumal.grimoire.wand.wizard

import android.accounts.Account
import android.accounts.AccountManager

fun interface WizardAccountResolver {

    fun performFilter(manager: AccountManager, account: Account): Boolean

}