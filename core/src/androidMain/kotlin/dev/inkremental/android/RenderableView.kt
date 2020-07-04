package dev.inkremental.android

import android.content.Context
import android.widget.FrameLayout
import dev.inkremental.Inkremental

abstract class RenderableView(context: Context) : FrameLayout(context) {
    abstract val renderable: () -> Unit

    public override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Inkremental.mount(this, renderable)
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Inkremental.unmount(this)
    }
}
