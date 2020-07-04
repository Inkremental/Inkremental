package dev.inkremental.platform

import java.lang.ref.WeakReference as JvmWeakReference
import java.util.WeakHashMap as JvmWeakHashMap

actual fun <T, U> WeakHashMap(): MutableMap<T, U> = JvmWeakHashMap()

actual typealias WeakReference<T> = JvmWeakReference<T>
