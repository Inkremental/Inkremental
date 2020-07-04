package dev.inkremental.android

import dev.inkremental.RootViewScope

abstract class AndroidViewScope : RootViewScope() {
    val Int.dp : Dip
        get() = Dip(this)

    val Float.sp : Sp
        get() = Sp(this)

    val Int.px : Px
        get() = Px(this)

    val Int.sizeDp : Size.EXACT
        get() = Size.EXACT(this.dp.toPx())

    val Int.sizePx : Size.EXACT
        get() = Size.EXACT(this.px)

    fun Dip.toPx() : Px {
        return Px(dip(this.value))
    }
}
