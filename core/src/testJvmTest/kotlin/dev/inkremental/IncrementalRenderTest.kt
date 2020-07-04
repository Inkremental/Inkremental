package dev.inkremental

import dev.inkremental.platform.*
import kotlin.test.Test
import kotlin.test.assertEquals

class IncrementalRenderTest: Utils() {
    private var fooValue = "a"
    private var showView = true

    @Test
    fun testConstantsRenderedOnce() {
        Inkremental.mount(container) { v<FakeGroup> { attr("text", "bar") } }
        assertEquals(1, createdViews[FakeGroup::class])
        assertEquals(1, changedAttrs["text"])
        Inkremental.render()
        assertEquals(1, createdViews[FakeGroup::class])
        assertEquals(1, changedAttrs["text"])
    }

    @Test
    fun testDynamicAttributeRenderedLazily() {
        Inkremental.mount(container) { v<FakeGroup> { attr("text", fooValue) } }
        assertEquals(1, changedAttrs["text"])
        Inkremental.render()
        assertEquals(1, changedAttrs["text"])
        fooValue = "b"
        Inkremental.render()
        assertEquals(2, changedAttrs["text"])
        Inkremental.render()
        assertEquals(2, changedAttrs["text"])
    }

    @Test
    fun testDynamicViewRenderedLazily() {
        Inkremental.mount(container) {
            v<FakeGroup> {
                v<FakeGroup>()
                if(showView) {
                    v<View>()
                }
            }
        }
        val layout = container.childAt(0) as ViewGroup
        assertEquals(2, layout.childrenCount)
        assertEquals(1, createdViews[View::class])
        Inkremental.render()
        assertEquals(1, createdViews[View::class])
        showView = false
        Inkremental.render()
        assertEquals(1, layout.childrenCount)
        assertEquals(1, createdViews[View::class])
        Inkremental.render()
        assertEquals(1, createdViews[View::class])
        showView = true
        Inkremental.render()
        assertEquals(2, layout.childrenCount)
        assertEquals(2, createdViews[View::class])
        Inkremental.render()
        assertEquals(2, createdViews[View::class])
    }

    private var firstMountValue = "foo"
    private var secondMountValue = "bar"

    @Test
    fun testRenderUpdatesAllMounts() {
        val rootA = FakeGroup()
        val rootB = FakeGroup()
        Inkremental.mount(rootA) { attr("text", firstMountValue) }
        Inkremental.mount(rootB) { attr("tag", secondMountValue) }
        assertEquals("foo", rootA["text"])
        assertEquals("bar", rootB["tag"])

        firstMountValue = "baz"
        secondMountValue = "qux"
        Inkremental.render()

        assertEquals("baz", rootA["text"])
        assertEquals("qux", rootB["tag"])
    }
}
