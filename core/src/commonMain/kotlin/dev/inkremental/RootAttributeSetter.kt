package dev.inkremental

import dev.inkremental.platform.View

class RootAttributeSetter : Inkremental.AttributeSetter<Any> {
    override fun set(v: View, name: String, value: Any?, prevValue: Any?): Boolean =
        if(name == ATTR_INIT && value is Function<*>) {
            (value as (View) -> Any?)(v)
            true
        }
        else
            false
}
