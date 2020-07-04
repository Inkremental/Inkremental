package dev.inkremental

import dev.inkremental.platform.View

abstract class RootViewScope {
    fun init(action: (View) -> Unit) = attr(ATTR_INIT, action)
    companion object : RootViewScope()
}

internal const val ATTR_INIT = "init"
