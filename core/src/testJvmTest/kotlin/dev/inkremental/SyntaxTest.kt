package dev.inkremental

import dev.inkremental.platform.*
import org.junit.Assert
import kotlin.test.Test

class SyntaxTest : Utils() {
    private val showNestedLayout = true
    private val numberOfChildViews = 5

    @Test
    fun testSyntax() {
        Inkremental.mount(container) {
            v<FakeGroup> {
                attr("tag", 1)
                v<View> { attr("tag", 2) }
                if (showNestedLayout) {
                    v<FakeGroup> {
                        for (i in 0 until numberOfChildViews) {
                            v<View> { attr("tag", i) }
                        }
                    }
                }
            }
        }
        val layout = container.childAt(0) as ViewGroup
        Assert.assertEquals(1, layout["tag"])
        val header = layout.childAt(0)
        Assert.assertEquals(2, header["tag"])
        val content = layout.childAt(1) as ViewGroup
        for (i in 0 until numberOfChildViews) {
            val v = content.childAt(i)
            Assert.assertEquals(i, v["tag"])
        }
    }
}
