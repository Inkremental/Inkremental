package dev.inkremental

import dev.inkremental.platform.*
import kotlin.test.*

class CurrentViewTest : Utils() {
    @Test
    fun testCurrentView() {
        assertNull(Inkremental.currentView())
        Inkremental.mount(container) {
            assertTrue(Inkremental.currentView<View>() is ViewGroup)
            v<FakeGroup, RootViewScope>(RootViewScope) {
                assertTrue(Inkremental.currentView<View>() is ViewGroup)
                v<View, RootViewScope>(RootViewScope) {
                    assertTrue(Inkremental.currentView<View>() is View)
                    attr("text", "bar")
                    val view = Inkremental.currentView<View>()!!// should cast automatically
                    assertEquals("bar", view["text"])
                }
                assertTrue(Inkremental.currentView<View>() is ViewGroup)
            }
            assertTrue(Inkremental.currentView<View>() is ViewGroup)
        }
        assertNull(Inkremental.currentView())
    }
}
