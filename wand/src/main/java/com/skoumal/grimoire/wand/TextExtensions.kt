package com.skoumal.grimoire.wand

import android.content.Context

fun Context.text(stringRes: Int, vararg args: Any): CharSequence =
    TextWand.Resource(stringRes, *args).getText(resources)

fun Context.quantityText(stringRes: Int, quantity: Int, vararg args: Any): CharSequence =
    TextWand.Quantity(stringRes, quantity, *args).getText(resources)

// ---

fun asText(text: CharSequence): TextWand =
    TextWand.Sequence(text)

fun asText(res: Int, vararg args: Any): TextWand =
    TextWand.Resource(res, *args)

fun asText(res: Int, quantity: Int, vararg args: Any): TextWand =
    TextWand.Quantity(res, quantity, *args)
