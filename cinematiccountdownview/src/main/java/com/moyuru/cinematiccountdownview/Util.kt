package com.moyuru.cinematiccountdownview

import android.view.View

internal val Float.absoluteValue get() = Math.abs(this)

internal fun <T> T.apply(func: T.() -> Unit): T {
  func(this)
  return this
}

internal fun View.dp(n: Int) = n * resources.displayMetrics.density

internal fun <T, R> T.run(func: T.() -> R): R = func(this)
