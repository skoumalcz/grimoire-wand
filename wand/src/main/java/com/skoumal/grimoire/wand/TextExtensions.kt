package com.skoumal.grimoire.wand

import android.content.Context

fun Context.text(stringRes: Int): CharSequence =
    TextWand.Resource(stringRes).getText(resources)

fun Context.quantity(stringRes: Int, quantity: Int, vararg args: Any): CharSequence =
    TextWand.Quantity(stringRes, quantity, *args).getText(resources)

// ---

fun text(text: CharSequence): TextWand =
    TextWand.Sequence(text)

fun textRes(res: Int): TextWand =
    TextWand.Resource(res)

fun textQuantity(res: Int, quantity: Int, vararg args: Any): TextWand =
    TextWand.Quantity(res, quantity, *args)
