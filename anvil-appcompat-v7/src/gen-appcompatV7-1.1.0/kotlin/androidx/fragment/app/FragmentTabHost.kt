@file:Suppress("DEPRECATION", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "unused")

package androidx.fragment.app

import dev.inkremental.Inkremental
import dev.inkremental.bind
import dev.inkremental.dsl.android.widget.TabHostScope
import dev.inkremental.dsl.androidx.appcompat.AppcompatV7Setter
import dev.inkremental.dsl.androidx.appcompat.CustomAppCompatv7Setter
import dev.inkremental.v
import kotlin.Suppress
import kotlin.Unit

fun fragmentTabHost(configure: FragmentTabHostScope.() -> Unit = {}) =
    v<FragmentTabHost>(configure.bind(FragmentTabHostScope))
abstract class FragmentTabHostScope : TabHostScope() {
  companion object : FragmentTabHostScope() {
    init {
      Inkremental.registerAttributeSetter(AppcompatV7Setter)
      Inkremental.registerAttributeSetter(CustomAppCompatv7Setter)
    }
  }
}
