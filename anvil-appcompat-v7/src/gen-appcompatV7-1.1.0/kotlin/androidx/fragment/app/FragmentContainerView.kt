@file:Suppress("DEPRECATION", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "unused")

package androidx.fragment.app

import dev.inkremental.Inkremental
import dev.inkremental.bind
import dev.inkremental.dsl.android.widget.FrameLayoutScope
import dev.inkremental.dsl.androidx.appcompat.AppcompatV7Setter
import dev.inkremental.dsl.androidx.appcompat.CustomAppCompatv7Setter
import dev.inkremental.v
import kotlin.Suppress
import kotlin.Unit

fun fragmentContainerView(configure: FragmentContainerViewScope.() -> Unit = {}) =
    v<FragmentContainerView>(configure.bind(FragmentContainerViewScope))
abstract class FragmentContainerViewScope : FrameLayoutScope() {
  companion object : FragmentContainerViewScope() {
    init {
      Inkremental.registerAttributeSetter(AppcompatV7Setter)
      Inkremental.registerAttributeSetter(CustomAppCompatv7Setter)
    }
  }
}
