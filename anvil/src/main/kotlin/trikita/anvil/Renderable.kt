package trikita.anvil

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.util.AttributeSet

fun renderable(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        emitView: () -> Unit
): RenderableView {
    return object : RenderableView(context, attrs, defStyleAttr) {
        override fun view() {
            emitView()
        }
    }
}

fun Activity.renderable(
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        emitView: () -> Unit
): RenderableView {
    return renderable(this, attrs, defStyleAttr, emitView)
}

fun Fragment.renderable(
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        emitView: () -> Unit
): RenderableView {
    return renderable(activity, attrs, defStyleAttr, emitView)
}