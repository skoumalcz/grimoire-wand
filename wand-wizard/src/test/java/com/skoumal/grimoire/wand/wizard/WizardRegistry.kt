package com.skoumal.grimoire.wand.wizard

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random.Default.nextBytes
import kotlin.random.Random.Default.nextInt

class WizardRegistryTest {

    private lateinit var registry: WizardRegistry

    @Before
    fun prepare() {
        registry = Mockito.mock(WizardRegistry::class.java)
    }

    // ---

    @Test
    fun extension_exists_trueIfExists() {
        val wizard = Wizard("", "")
        Mockito.`when`(registry.getWizard()).thenReturn(wizard)

        assertThat(registry.exists()).isTrue()
    }

    @Test
    fun extension_exists_falseIfNotExists() {
        Mockito.`when`(registry.getWizard()).thenReturn(null)

        assertThat(registry.exists()).isFalse()
    }

    // ---

    @Test(expected = IllegalArgumentException::class)
    fun extension_requireWizard_throwsWhenEmpty() {
        Mockito.`when`(registry.getWizard()).thenReturn(null)

        registry.requireWizard()
    }

    @Test
    fun extension_requireWizard_returnsIfExists() {
        val wizard = Wizard("", "")
        Mockito.`when`(registry.getWizard()).thenReturn(wizard)

        assertThat(registry.requireWizard()).isSameInstanceAs(wizard)
    }

    // ---

    @Test
    fun extension_get_fillsInstance() {
        val value = String(nextBytes(nextInt(10, 20)))
        val extra = object : NamedExtra {
            override val named: String = ""
        }
        Mockito.`when`(registry.getExtra(extra)).thenReturn(value)

        val map = mutableMapOf(extra as NamedExtra to null as String?)
        registry[map]

        assertThat(map[extra]).isSameInstanceAs(value)
    }

    @Test
    fun extension_get_returnsValidFields() {
        val value = String(nextBytes(nextInt(10, 20)))
        val extra = object : NamedExtra {
            override val named: String = ""
        }
        Mockito.`when`(registry.getExtra(extra)).thenReturn(value)

        val map = mutableMapOf<NamedExtra, String?>(
            extra to null,
            extra to null,
            extra to null,
            extra to null,
        )
        registry[map]

        map.forEach {
            assertThat(it.value).isSameInstanceAs(value)
        }
    }

    @Test
    fun extension_get_replacesFields() {
        val value = String(nextBytes(nextInt(10, 20)))
        val extra = object : NamedExtra {
            override val named: String = ""
        }
        Mockito.`when`(registry.getExtra(extra)).thenReturn(null)

        val map = mutableMapOf<NamedExtra, String?>(
            extra to value,
            extra to value,
            extra to value,
            extra to value,
        )
        registry[map]

        map.forEach {
            assertThat(it.value).isNull()
        }
    }

    // ---

    @Test(expected = IllegalStateException::class)
    fun extension_set_setsFields() {
        val extra: NamedExtra = object : NamedExtra {
            override val named = "foobar"
        }
        Mockito.`when`(registry.putExtra(extra, null)).thenThrow(IllegalStateException())
        registry[extra] = null
    }

}