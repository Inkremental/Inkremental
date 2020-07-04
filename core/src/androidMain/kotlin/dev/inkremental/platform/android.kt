package dev.inkremental.platform

import dev.inkremental.Inkremental
import dev.inkremental.android.AndroidViewFactory
import dev.inkremental.android.ReflectivePropertySetter

internal actual fun platformInit() {
    Inkremental.registerViewFactory(AndroidViewFactory())
    Inkremental.registerAttributeSetter(ReflectivePropertySetter())
}

actual typealias View = android.view.View
actual typealias ViewGroup = android.view.ViewGroup

actual val ViewGroup.childrenCount: Int
    get() = this.childCount

actual fun ViewGroup.childAt(position: Int): View = getChildAt(position)

actual fun ViewGroup.addChild(child: View, position: Int) {
    addView(child, position)
}

actual fun ViewGroup.removeChild(child: View?) {
    removeView(child)
}

actual fun ViewGroup.clearChildren() {
    removeAllViews()
    //removeViews(0, childCount)
}
