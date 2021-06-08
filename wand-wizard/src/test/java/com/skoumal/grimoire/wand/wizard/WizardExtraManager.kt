package com.skoumal.grimoire.wand.wizard

import android.accounts.Account
import android.accounts.AccountManager
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random.Default.nextBytes
import kotlin.random.Random.Default.nextInt

class WizardExtraManagerTest {

    private lateinit var extra: WizardExtraManager
    private lateinit var holder: WizardAccountHolder
    private lateinit var manager: AccountManager

    @Before
    fun prepare() {
        manager = Mockito.mock(AccountManager::class.java)
        holder = WizardAccountHolder(manager, { _, _ -> true }, "")
        extra = WizardExtraManager(manager, holder)
    }

    // ---

    private fun onNamedReturn(named: NamedExtra, body: () -> String?) {
        val account = Mockito.mock(Account::class.java)
        Mockito
            .`when`(manager.getAccountsByType(""))
            .thenReturn(arrayOf(account))

        Mockito.`when`(manager.getUserData(account, named.named)).thenReturn(body())
    }

    @Test
    fun get_returnsValue() {
        val named = NamedExtra { "" }
        val value = String(nextBytes(nextInt(10, 20)))

        onNamedReturn(named) { value }

        assertThat(extra[named]).isEqualTo(value)
    }

    @Test
    fun get_returnsNull() {
        val named = NamedExtra { "" }

        onNamedReturn(named) { null }

        assertThat(extra[named]).isNull()
    }

    @Test
    fun get_returnsNull_onMissingAccount() {
        val named = NamedExtra { "" }
        Mockito
            .`when`(manager.getAccountsByType(""))
            .thenReturn(arrayOf())

        assertThat(extra[named]).isNull()
    }

    @Test
    fun get_returnsMap() {
        val named = NamedExtra { "" }
        val value = String(nextBytes(nextInt(10, 20)))

        onNamedReturn(named) { value }

        assertThat(extra[arrayOf(named)]).containsEntry(named, value)
    }

    // ---

    private fun onNamedThrow(named: NamedExtra, value: String?) {
        val account = Mockito.mock(Account::class.java)
        Mockito
            .`when`(manager.getAccountsByType(""))
            .thenReturn(arrayOf(account))
        Mockito
            .`when`(manager.setUserData(account, named.named, value))
            .thenThrow(IllegalStateException())
    }

    @Test(expected = IllegalStateException::class)
    fun set_assignsValue() {
        val named = NamedExtra { "" }
        val value = String(nextBytes(nextInt(10, 20)))

        onNamedThrow(named, value)

        extra[named] = value
    }

    @Test(expected = IllegalStateException::class)
    fun set_clearsValue() {
        val named = NamedExtra { "" }
        val value: String? = null

        onNamedThrow(named, value)

        extra[named] = value
    }

    @Test
    fun set_fails_onMissingAccount() {
        val named = NamedExtra { "" }

        Mockito
            .`when`(manager.getAccountsByType(""))
            .thenReturn(arrayOf())

        val account = Mockito.mock(Account::class.java)
        Mockito
            .`when`(manager.setUserData(account, named.named, null))
            .thenThrow(IllegalStateException())

        extra[named] = null
    }
}