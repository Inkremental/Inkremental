@file:Suppress("DEPRECATION") // due to android.app.Fragment, which we still have to support

package dev.inkremental.android

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import dev.inkremental.*

fun renderable(
    context: Context,
    r: () -> Unit
): View = object : RenderableView(context) {
    override var renderable: () -> Unit = {
        r()
    }
}

fun Activity.renderable(
    r: () -> Unit
): View = renderable(this, r)

fun Activity.renderableContentView(
    r: () -> Unit
): View = renderable(r).also { setContentView(it) }

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("android.app.Fragment is deprecated. You should migrate to androidx.fragment.")
fun Fragment.renderable(
    r: () -> Unit
): View = renderable(if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context else activity, r)

inline fun <reified S: RootViewScope> xml(@LayoutRes layoutId: Int, s: S, noinline r: S.() -> Unit = {}) = xml(layoutId, r.bind(s))
fun xml(@LayoutRes layoutId: Int, r: () -> Unit = {}) {
    Inkremental.currentMount?.iterator?.start(null, layoutId)
    r()
    end()
}

inline fun <reified S: RootViewScope> withId(@IdRes id: Int, s: S, noinline r: S.() -> Unit = {}) = withId(id, r.bind(s))
fun withId(@IdRes id: Int, r: () -> Unit): View {
    var v = Inkremental.currentView<View>()
    requireNotNull(v) { "Inkremental.currentView() is null" }
    v = v.findViewById(id)
    requireNotNull(v) { "No view found for ID $id" } // TODO convert id to string
    return Inkremental.mount(v, r)
}

val r: Resources
    get() = Inkremental.currentView<View>()!!.resources

val isPortrait: Boolean
    get() = r.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

// gravity constants
const val TOP = Gravity.TOP
const val BOTTOM = Gravity.BOTTOM
const val LEFT = Gravity.LEFT // TODO add lint rules to own constants
const val RIGHT = Gravity.RIGHT // TODO add lint rules to own constants
const val CENTER_VERTICAL = Gravity.CENTER_VERTICAL
const val GROW_VERTICAL = Gravity.FILL_VERTICAL
const val CENTER_HORIZONTAL = Gravity.CENTER_HORIZONTAL
const val GROW_HORIZONTAL = Gravity.FILL_HORIZONTAL
const val CENTER = CENTER_VERTICAL or CENTER_HORIZONTAL
const val GROW = GROW_VERTICAL or GROW_HORIZONTAL
const val CLIP_VERTICAL = Gravity.CLIP_VERTICAL
const val CLIP_HORIZONTAL = Gravity.CLIP_HORIZONTAL
const val START = Gravity.START
const val END = Gravity.END
