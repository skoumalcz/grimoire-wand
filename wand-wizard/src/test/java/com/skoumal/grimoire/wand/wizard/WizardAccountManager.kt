package com.skoumal.grimoire.wand.wizard

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.os.Handler
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import kotlin.random.Random.Default.nextBytes
import kotlin.random.Random.Default.nextInt

class WizardAccountManagerTest {

    private lateinit var manager: AccountManager
    private lateinit var holder: WizardAccountHolder
    private lateinit var extra: WizardExtraManager
    private lateinit var account: WizardAccountManager

    @Before
    fun prepare() {
        manager = Mockito.mock(AccountManager::class.java)
        holder = WizardAccountHolder(manager, { _, _ -> true }, "")
        extra = WizardExtraManager(manager, holder)
        account = WizardAccountManager(manager, holder, extra)
    }

    // ---

    @Test
    fun get_returnsNull_ifEmpty() {
        Mockito.`when`(manager.getAccountsByType("")).thenReturn(emptyArray())

        assertThat(account.get()).isNull()
    }

    @Test
    fun get_returnsValue() {
        val name = String(nextBytes(nextInt(10, 20)))
        val mockedAccount = Mockito.mock(Account::class.java)
        val password = String(nextBytes(nextInt(10, 20)))
        Mockito.`when`(manager.getPassword(mockedAccount)).thenReturn(password)
        Mockito.`when`(manager.getAccountsByType("")).thenReturn(arrayOf(mockedAccount))

        val wizard = this.account.get()

        requireNotNull(wizard)
        //assertThat(wizard.name).isEqualTo(name) // this will sadly always fail
        assertThat(wizard.password).isEqualTo(password)
        assertThat(wizard.extra).isEmpty()
    }

    // ---

    @Test
    fun remove_clearsAll() {
        val handler = Mockito.mock(Handler::class.java)
        val accounts = mutableListOf(
            Mockito.mock(Account::class.java),
            Mockito.mock(Account::class.java),
            Mockito.mock(Account::class.java)
        )
        Mockito.`when`(manager.getAccountsByType("")).then {
            accounts.toTypedArray()
        }
        Mockito.`when`(
            manager.removeAccount(
                Mockito.any(),
                eq(null as AccountManagerCallback<Boolean>?),
                eq(handler)
            )
        ).then {
            accounts.remove(it.getArgument(0))
            null
        }
        Mockito.`when`(manager.removeAccountExplicitly(Mockito.any())).then {
            accounts.remove(it.getArgument(0))
        }
        account.remove(handler)

        assertThat(accounts).isEmpty()
    }

}
