package com.skoumal.grimoire.wand.wizard

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random.Default.nextBytes
import kotlin.random.Random.Default.nextInt

class WizardAccountHolderTest {

    private lateinit var manager: AccountManager
    private lateinit var type: String
    private lateinit var resolver: WizardAccountResolver
    private lateinit var holder: WizardAccountHolder

    @Before
    fun prepare() {
        manager = Mockito.mock(AccountManager::class.java)
        type = String(nextBytes(nextInt(10, 20)))
        resolver = WizardAccountResolver { _, _ -> true }
        holder = WizardAccountHolder(manager, resolver, type)
    }

    // ---

    @Test
    fun findsAccount() {
        val account = Mockito.mock(Account::class.java)
        val accounts = arrayOf(account)
        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(accounts)

        assertThat(holder.get()).isSameInstanceAs(account)
    }

    @Test(expected = NoSuchElementException::class)
    fun throwsOnGet() {
        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(emptyArray())
        assertThat(holder.get()).isNull()
    }

    @Test
    fun findsAccountAfterInvalidate() {
        val account = Mockito.mock(Account::class.java)
        val accounts = arrayOf(account)
        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(accounts)
        holder.get()

        val account2 = Mockito.mock(Account::class.java)
        val accounts2 = arrayOf(account2)
        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(accounts2)

        holder.invalidate()
        assertThat(holder.get()).isSameInstanceAs(account2)
    }

    @Test
    fun createsAccount() {
        val account = Mockito.mock(Account::class.java)
        val password = String(nextBytes(nextInt(10, 20)))
        val extra: Bundle? = null

        Mockito.`when`(manager.addAccountExplicitly(account, password, extra))
            .thenReturn(true)

        assertThat(holder.create(account, password, extra)).isTrue()
    }

    @Test(expected = NoSuchElementException::class)
    fun invalidatesAfterCreate() {
        val account = Mockito.mock(Account::class.java)
        val password = String(nextBytes(nextInt(10, 20)))
        val extra: Bundle? = null

        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(emptyArray())
        Mockito.`when`(manager.addAccountExplicitly(account, password, extra))
            .thenReturn(true)

        holder.create(account, password, extra)
        holder.get()
    }

    // ---

    @Test
    fun extension_getOrNull_returnsNull() {
        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(emptyArray())
        assertThat(holder.getOrNull()).isNull()
    }

    @Test
    fun extension_getOrNull_returnsValue() {
        val account = Mockito.mock(Account::class.java)
        Mockito.`when`(manager.getAccountsByType(type)).thenReturn(arrayOf(account))
        assertThat(holder.getOrNull()).isSameInstanceAs(account)
    }

}