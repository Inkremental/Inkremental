package dev.inkremental

import dev.inkremental.platform.*
import kotlin.test.Test
import kotlin.test.assertEquals

class MountTest : Utils() {
    var testLayout = {
        v<View, RootViewScope>(RootViewScope) { attr("text", "bar") }
    }

    @Test
    fun testMountReturnsMountPoint() {
        assertEquals(container, Inkremental.mount(container, empty))
    }

    @Test
    fun testMountRendersViews() {
        Inkremental.mount(container, testLayout)
        assertEquals(1, container.childrenCount)
        val v = container.childAt(0)
        assertEquals("bar", v["text"])
    }

    @Test
    fun testUnmountRemovesViews() {
        Inkremental.mount(container, testLayout)
        assertEquals(1, container.childrenCount)
        Inkremental.unmount(container)
        assertEquals(0, container.childrenCount)
    }

    @Test
    fun testMountReplacesViews() {
        Inkremental.mount(container, testLayout)
        assertEquals(1, container.childrenCount)
        Inkremental.unmount(container)
        Inkremental.mount(container, empty)
        assertEquals(0, container.childrenCount)
        Inkremental.unmount(container)
        Inkremental.mount(container, testLayout)
        assertEquals(1, container.childrenCount)
        Inkremental.unmount(container)
    }

    @Test
    fun testMountInfoView() {
        val v = Inkremental.mount(View()) {
            attr("id", 100)
            attr("text", "bar")
            attr("tag", "foo")
        }
        assertEquals(100, v["id"])
        assertEquals("foo", v["tag"])
        assertEquals("bar", v["text"])
    }
}
