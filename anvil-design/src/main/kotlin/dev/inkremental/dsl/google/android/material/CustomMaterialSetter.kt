@file:Suppress("DEPRECATION", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "unused")

package dev.inkremental.dsl.google.android.material

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.os.Build
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.inkremental.Inkremental
import dev.inkremental.android.Dip
import dev.inkremental.android.dip
import dev.inkremental.attr
import dev.inkremental.dsl.android.view.ViewScope
import dev.inkremental.dsl.google.android.material.appbar.AppBarLayoutScope
import dev.inkremental.dsl.google.android.material.bottomnavigation.BottomNavigationViewScope

fun ViewScope.collapseMode(collapseMode: Int) = attr("collapseMode", collapseMode)
fun ViewScope.scrollFlags(scrollFlags: Int) = attr("scrollFlags", scrollFlags)
fun ViewScope.behavior(behavior: CoordinatorLayout.Behavior<*>) = attr("behavior", behavior)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
fun AppBarLayoutScope.appBarElevation(elevation: Dip) = attr("appBarElevation", elevation.value)
fun BottomNavigationViewScope.inflateMenu(@MenuRes menu: Int) = attr("bottomMenu", menu)

object CustomMaterialSetter : Inkremental.AttributeSetter<Any> {
    override fun set(v: View, name: String, value: Any?, prevValue: Any?): Boolean = when (name) {
        "collapseMode" -> when {
            v.layoutParams is CollapsingToolbarLayout.LayoutParams && value is Int -> {
                (v.layoutParams as CollapsingToolbarLayout.LayoutParams).collapseMode = value
                true
            }
            else -> false
        }
        "scrollFlags" -> when {
            v.layoutParams is AppBarLayout.LayoutParams && value is Int -> {
                (v.layoutParams as AppBarLayout.LayoutParams).scrollFlags = value
                true
            }
            else -> false
        }
        "behavior" -> when {
            v.layoutParams is CoordinatorLayout.LayoutParams && value is CoordinatorLayout.Behavior<*>? -> {
                (v.layoutParams as CoordinatorLayout.LayoutParams).behavior = value
                true
            }
            else -> false
        }
        "appBarElevation" -> when {
            v is AppBarLayout && value is Int -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    v.stateListAnimator = StateListAnimator().apply {
                        addState(IntArray(0), ObjectAnimator.ofFloat(v, "elevation", dip(value).toFloat()))
                    }
                }
                true
            }
            else -> false
        }
        "bottomMenu" -> when {
            v is BottomNavigationView && value is Int -> {
                v.menu.clear()
                v.inflateMenu(value)
                true
            }
            else -> false
        }
        else -> false
    }
}
