package com.skoumal.grimoire.wand.wizard

import android.content.Context

interface WizardRegistry {

    fun removeWizard(wizard: Wizard?)
    fun putWizard(wizard: Wizard): Boolean
    fun getWizard(vararg extra: NamedExtra): Wizard?

    fun putExtra(named: NamedExtra, value: String?)
    fun getExtra(named: NamedExtra): String?


    class Builder(context: Context) {

        private val context = context.applicationContext
        private lateinit var type: String
        private var resolver = WizardAccountResolver { _, _ -> true }

        fun setAccountType(type: String) = apply {
            this.type = type
        }

        fun setAccountResolver(resolver: WizardAccountResolver) = apply {
            this.resolver = resolver
        }

        fun build(): WizardRegistry {
            return DefaultWizardRegistry(
                context = context,
                type = type,
                resolver = resolver
            )
        }

    }

}

fun WizardRegistry.exists(): Boolean {
    return getWizard() != null
}

fun WizardRegistry.requireWizard(vararg extra: NamedExtra): Wizard {
    return requireNotNull(getWizard(*extra))
}

operator fun WizardRegistry.get(extras: MutableMap<NamedExtra, String?>) {
    extras.keys.forEach {
        extras[it] = getExtra(it)
    }
}

operator fun WizardRegistry.set(extra: NamedExtra, value: String?) {
    putExtra(extra, value)
}