package dev.inkremental

import dev.inkremental.platform.View
import kotlin.test.*

class InitTest : Utils() {
    var called: MutableMap<String, Boolean?> = mutableMapOf()

    @Test
    fun testInit() {
        println("============================")
        Inkremental.mount(container) {
            //init(makeFunc("once"))
            v<View, RootViewScope>(RootViewScope) {
                init(makeFunc("setUpView"))
            }
        }
        println("============================")
        //assertTrue(called["once"]!!)
        assertTrue(called["setUpView"]!!)
        called.clear()
        Inkremental.render()
        //assertFalse(called.containsKey("once"))
        assertFalse(called.containsKey("setUpView"))
    }

    // new function will be created each time, but only the first one should be called
    private fun makeFunc(id: String): (View) -> Unit = {
        require(!called.containsKey(id)) { "Init func for $id called more than once!" }
        called[id] = true
    }
}
