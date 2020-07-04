package dev.inkremental

/*
@ExperimentalTime
class BenchmarkTest : Utils() {
    private var mode = 0
    @Test
    fun testRenderBenchmark() {
        val r = {
            for (i in 0..9) {
                group(transform(i)) {
                    for (j in 0..9) {
                        item(transform(i * 10 + j))
                    }
                }
            }
        }
        mode = 0
        Inkremental.mount(container, r)
        val noChanges = measureTime {
            for (i in 0 until N) {
                Inkremental.render()
            }
        }
        println("render/no-changes: ${noChanges.inNanoseconds / N} us")
        Inkremental.unmount(container, true)
        mode = 1
        Inkremental.mount(container, r)
        val smallChanges = measureTime {
            for (i in 0 until N) {
                Inkremental.render()
            }
        }
        println("render/small-changes: ${smallChanges.inNanoseconds / N} us")
        Inkremental.unmount(container, true)
        mode = 2
        Inkremental.mount(container, r)
        val bigChanges = measureTime {
            for (i in 0 until N) {
                Inkremental.render()
            }
        }
        println("render/big-changes: ${bigChanges.inNanoseconds / N} us")
    }

    private fun transform(i: Int): Int = when (mode) {
        0 -> i
        1 -> if (i == 1) Random.nextInt(100) else i
        2 -> Random.nextInt(100)
        else -> 0
    }

    private fun group(i: Int, r: () -> Unit) {
        v<ViewGroup, FrameLayoutScope>(FrameLayoutScope) {
            id(i * 100)
            tag("layout")
            r()
        }
    }

    private fun item(i: Int) {
        v<View, ViewScope>(ViewScope) {
            id(i)
            tag("item$i")
        }
    }

    companion object {
        private const val N = 100000
    }
}
*/
