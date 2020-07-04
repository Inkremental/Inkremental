package dev.inkremental.android

import android.content.Context
import android.view.LayoutInflater
import dev.inkremental.Inkremental
import dev.inkremental.platform.View
import dev.inkremental.platform.ViewGroup
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass

internal class AndroidViewFactory : Inkremental.ViewFactory {
    override fun fromClass(parent: ViewGroup, viewClass: KClass<out View>): View? {
        return try {
            viewClass.java.getConstructor(Context::class.java).newInstance(parent.context)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        }
    }

    override fun fromXml(parent: ViewGroup, xmlId: Int): View? {
        return LayoutInflater.from(parent.context).inflate(xmlId, parent, false)
    }
}
