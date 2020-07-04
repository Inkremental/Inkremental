package dev.inkremental.android

import android.view.View
import dev.inkremental.Inkremental
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass

internal class ReflectivePropertySetter : Inkremental.AttributeSetter<Any> {
    private fun assignable(a: KClass<*>?, b: KClass<*>?): Boolean = when {
        a == null -> false
        b == null -> false
        b == a -> true
        else -> when (b) {
            Int::class -> Long::class == a || Float::class == a || Double::class == a
            Long::class -> Float::class == a || Double::class == a
            Boolean::class -> false
            Double::class -> false
            Float::class -> Double::class == a
            Char::class -> Int::class == a || Long::class == a || Float::class == a || Double::class == a
            Short::class -> Int::class == a || Long::class == a || Float::class == a || Double::class == a
            Byte::class -> Short::class == a || Int::class == a || Long::class == a || Float::class == a || Double::class == a
            else -> false
        }
    }

    override fun set(v: View, name: String, value: Any?, prevValue: Any?): Boolean {
        val upperCasedName = "${name[0].toUpperCase()}${name.substring(1)}"
        val setter = "set$upperCasedName"
        val listener = "set${upperCasedName}Listener"
        var cls: Class<*>? = v::class.java
        while (cls != null) {
            try {
                for (m in cls.declaredMethods) {
                    if ((m.name == setter || m.name == listener) &&
                            m.parameterTypes.size == 1) {
                        val arg = m.parameterTypes[0]
                        if (value == null && !arg.isPrimitive || value != null && assignable(arg::class, value::class)) {
                            m.invoke(v, value)
                            return true
                        }
                    }
                }
                cls = cls.superclass
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return false
    }
}
