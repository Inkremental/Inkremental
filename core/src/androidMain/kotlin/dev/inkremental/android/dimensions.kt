package dev.inkremental.android

import android.util.TypedValue
import kotlin.math.roundToInt

fun dip(value: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, value,
    r.displayMetrics
)

fun dip(value: Int): Int = dip(value.toFloat()).roundToInt()

fun sip(value: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, value,
    r.displayMetrics
)

fun sip(value: Int): Int = sip(value.toFloat()).roundToInt()

// weight constants
sealed class Size {
    object MATCH : Size()
    object WRAP : Size()
    class EXACT(val size: Px) : Size()
}

inline class Sp(val value: Float)
inline class Dip(val value: Int)
inline class Px(val value: Int)
