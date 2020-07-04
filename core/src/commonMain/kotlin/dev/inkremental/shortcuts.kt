package dev.inkremental

import dev.inkremental.platform.View
import kotlin.reflect.KClass

fun v(c: KClass<out View>, r: () -> Unit = {}) {
    Inkremental.currentMount?.iterator?.start(c, 0)
    r()
    end()
}
inline fun <reified T: View> v(noinline r: () -> Unit = {}) = v(T::class, r)
inline fun <reified T: View, reified S: RootViewScope> v(s: S, noinline r: S.() -> Unit = {}) = v(T::class, r.bind(s))

fun end() = Inkremental.currentMount?.iterator?.end()
fun skip() = Inkremental.currentMount?.iterator?.skip()

fun <T, U> ((T) -> U).bind(value: T): () -> U = { this(value) }

fun <T : Any> attr(name: String, value: T?) {
    Inkremental.currentMount?.iterator?.attr<T>(name, value)
}
