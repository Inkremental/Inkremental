package dev.inkremental.platform

import dev.inkremental.Inkremental

internal actual fun platformInit() {
    Inkremental.registerAttributeSetter(object : Inkremental.AttributeSetter<Any> {
        override fun set(v: View, name: String, value: Any?, prevValue: Any?): Boolean {
            v.data[name] = value
            return true
        }
    })
}

actual open class View {
    internal val data: MutableMap<String, Any?> = mutableMapOf()
    operator fun get(key: String): Any? = data[key]
    operator fun set(key: String, value: Any?) { data[key] = value }
}
actual abstract class ViewGroup : View() {
    internal val children: MutableList<View> = mutableListOf()
}

actual val ViewGroup.childrenCount: Int
    get() = this.children.size

actual fun ViewGroup.childAt(position: Int): View = children[position]

actual fun ViewGroup.addChild(child: View, position: Int) {
    children.add(position, child)
}

actual fun ViewGroup.removeChild(child: View?) {
    if(child != null) children.remove(child)
}

actual fun ViewGroup.clearChildren() {
    children.clear()
}

class FakeGroup : ViewGroup()
