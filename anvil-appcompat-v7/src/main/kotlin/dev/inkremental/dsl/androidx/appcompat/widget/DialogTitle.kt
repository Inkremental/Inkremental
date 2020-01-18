@file:Suppress("DEPRECATION", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "unused")

package dev.inkremental.dsl.androidx.appcompat.widget

import androidx.appcompat.widget.DialogTitle
import dev.inkremental.Anvil
import dev.inkremental.bind
import dev.inkremental.dsl.androidx.appcompat.AppCompatv7Setter
import dev.inkremental.dsl.androidx.appcompat.CustomAppCompatv7Setter
import dev.inkremental.v
import kotlin.Suppress
import kotlin.Unit

fun dialogTitle(configure: DialogTitleScope.() -> Unit = {}) =
    v<DialogTitle>(configure.bind(DialogTitleScope))
abstract class DialogTitleScope : AppCompatTextViewScope() {
  companion object : DialogTitleScope() {
    init {
      Anvil.registerAttributeSetter(AppCompatv7Setter)
      Anvil.registerAttributeSetter(CustomAppCompatv7Setter)
    }
  }
}