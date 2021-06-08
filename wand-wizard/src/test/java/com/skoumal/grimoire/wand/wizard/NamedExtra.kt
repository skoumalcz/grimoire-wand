package com.skoumal.grimoire.wand.wizard

fun NamedExtra(name: () -> String) = object : NamedExtra {
    override val named get() = name()
}