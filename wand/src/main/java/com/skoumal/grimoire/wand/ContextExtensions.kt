package com.skoumal.grimoire.wand

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

fun Context.drawable(@DrawableRes res: Int) = AppCompatResources.getDrawable(this, res)
