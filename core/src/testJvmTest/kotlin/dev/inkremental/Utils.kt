package dev.inkremental

import dev.inkremental.platform.*
import org.junit.After
import org.junit.Before
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class Utils {
    var createdViews: MutableMap<KClass<*>, Int?> = mutableMapOf()
    var changedAttrs: MutableMap<String, Int?> = mutableMapOf()
    var empty = {  }
    lateinit var container: ViewGroup

    @Before
    fun setUp() {
        changedAttrs.clear()
        createdViews.clear()
        container = FakeGroup()
    }

    @After
    fun tearDown() {
        Inkremental.unmount(container)
    }

    init {
        Inkremental.registerAttributeSetter(object : Inkremental.AttributeSetter<Any> {
            override fun set(v: View, name: String, value: Any?, prevValue: Any?): Boolean {
                changedAttrs[name] = if (!changedAttrs.containsKey(name)) 1 else changedAttrs[name]!! + 1
                return false
            }
        })
        Inkremental.registerViewFactory(object : Inkremental.ViewFactory {
            override fun fromClass(parent: ViewGroup, viewClass: KClass<out View>): View? {
                createdViews[viewClass] = if (!createdViews.containsKey(viewClass)) 1 else createdViews[viewClass]!! + 1
                return viewClass.primaryConstructor!!.call()
            }
            override fun fromXml(parent: ViewGroup, xmlId: Int): View? = null
        })
    }
}
