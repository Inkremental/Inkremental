package dev.inkremental

import dev.inkremental.platform.*
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

/**
 * Inkremental class is a namespace for top-level static methods and interfaces. Most
 * users would only use it to call `Inkremental.render()`.
 *
 * Internally, Inkremental class defines how renderable functions are mounted into Views
 * and how they are lazily rendered, and this is the key functionality of the
 * Inkremental library.
 */
object Inkremental {
    /**
     * Currently rendered [Mount] point. Must be called from the renderable function, otherwise it is absent.
     */
    var currentMount: Mount? = null
        private set

    private val mounts: MutableMap<View, Mount> = WeakHashMap()
    private val viewFactories: MutableList<ViewFactory> = mutableListOf()
    private val attributeSetters: MutableList<AttributeSetter<Any>> = mutableListOf()
    private val renderUiSwitcher: UiSwitcher = UiSwitcher { render() }

    init {
        platformInit()
        registerAttributeSetter(RootAttributeSetter())
    }

    fun registerViewFactory(viewFactory: ViewFactory) {
        if (!viewFactories.contains(viewFactory)) {
            viewFactories.add(0, viewFactory)
        }
    }

    fun registerAttributeSetter(setter: AttributeSetter<Any>) {
        if (!attributeSetters.contains(setter)) {
            attributeSetters.add(0, setter)
        }
    }

    /**
     * Starts the new rendering cycle updating all mounted
     * renderables. Update happens in a lazy manner, only the values that has
     * been changed since last rendering cycle will be actually updated in the
     * views. This method can be called from any thread, so it's safe to use
     * `Inkremental.render()` in background services.
     */
    fun render() {
        if(renderUiSwitcher.scheduleOnUi()) return
        mounts.values.toSet().forEach { render(it) }
    }

    /**
     * Mounts a renderable function defining the layout into a View. If host is a
     * [ViewGroup] it is assumed to be empty, so the renderable would define what
     * its child views would be.
     * @param view a View into which the renderable r will be mounted
     * @param renderable a renderable function to mount into a View
     */
    fun <T : View> mount(view: T, renderable: () -> Unit): T {
        mounts[view] = Mount(view, renderable)
        render(view)
        return view
    }

    /**
     * Unmounts a mounted renderable corresponding to specified [view].
     * This would also unmount and, if [removeChildren] is `true`, clean up
     * all the child views inside the parent ViewGroup, which acted as a mount point.
     */
    @JvmOverloads
    fun unmount(view: View?, removeChildren: Boolean = true) {
        val mount = mounts[view]
        if (mount != null) {
            mounts.remove(view)
            mount.cleanTags()
            if (view is ViewGroup) {
                val childCount = view.childrenCount
                for (i in 0 until childCount) {
                    unmount(view.childAt(i))
                }
                if (removeChildren) {
                    view.clearChildren()
                }
            }
        }
    }

    /**
     * Returns currently rendered [View]. It gives access to the real view from
     * inside the renderable function.
     */
    fun <T : View?> currentView(): T? = currentMount?.iterator?.currentView() as T?

    fun render(view: View?) {
        val mount = mounts[view] ?: devError("Unable to run render: view is not mounted") ?: return
        render(mount)
    }

    fun render(mount: Mount) {
        if (mount.lock) {
            return
        }
        mount.lock = true
        val prev = currentMount
        currentMount = mount
        mount.iterator.start()
        mount.renderable()
        mount.iterator.end()
        currentMount = prev
        mount.lock = false
    }

    interface ViewFactory {
        fun fromClass(parent: ViewGroup, viewClass: KClass<out View>): View?
        fun fromXml(parent: ViewGroup, xmlId: Int): View?
    }

    interface AttributeSetter<T> {
        operator fun set(v: View, name: String, value: T?, prevValue: T?): Boolean
    }

    /**
     * [Mount] describes a mount point. Mount point is a renderable function
     * attached to some [ViewGroup]. Mount point keeps track of the virtual layout
     * declared by renderables.
     */
    class Mount(view: View, val renderable: () -> Unit) {
        /**
         * Used to guard the mount from rendering multiple times simultaneously
         */
        var lock = false
        val iterator = Iterator()
        private val rootView = WeakReference(view)

        /**
         * Tags are arbitrary data bound to specific views, such as last cached attribute values
         * or service information like whether custom initializer was already run on this view.
         */
        private val tags: MutableMap<View, MutableMap<String, Any?>> = WeakHashMap()

        /**
         * Puts a tag named [key] with value [value] into the [view] within current mount.
         */
        private operator fun View.set(key: String, value: Any?) {
            val attrs = tags.getOrPut(this) { mutableMapOf() }
            attrs[key] = value
        }

        /**
         * Returns value of the tag named [key] from [this] view within current mount or `null` if the tag is absent.
         */
        private operator fun View?.get(key: String): Any? {
            return tags[this]?.get(key)
        }

        /**
         * Internal tag showing whether [this] view is managed by Inkremental or not
         */
        private inline var View.isManaged: Boolean
            get() = this[ATTR_MANAGED] != 0
            set(value) { this[ATTR_MANAGED] = if(value) 1 else 0 }

        /**
         * Internal tag holding layout id [this] view was inflated from.
         */
        private inline var View.layoutId: Int?
            get() = this[ATTR_LAYOUT_ID] as? Int
            set(value) { this[ATTR_LAYOUT_ID] = value }

        /**
         * Removes all tags from this mount.
         */
        fun cleanTags() {
            tags.clear()
        }

        /**
         * Allows to navigate over the tree of views and their attributes.
         */
        @OptIn(ExperimentalStdlibApi::class)
        inner class Iterator {
            var views: ArrayDeque<View?> = ArrayDeque()
            var indices: ArrayDeque<Int> = ArrayDeque()

            fun start() {
                require(views.size == 0)
                require(indices.size == 0)
                indices.addLast(0)
                val view = rootView.get()
                if (view != null) {
                    views.addLast(view)
                }
            }

            fun start(clazz: KClass<out View>?, layoutId: Int) {
                val index = indices.last()
                val parentView = (views.last() ?: devError("views queue is empty on start(...) call") ?: return) as? ViewGroup
                    ?: error("Children are allowed only inside view groups")
                var view: View? = null
                if (index < parentView.childrenCount) {
                    view = parentView.childAt(index)
                }
                if (clazz != null && (view == null || view::class != clazz)) {
                    parentView.removeChild(view)
                    for (factory in viewFactories) {
                        view = factory.fromClass(parentView, clazz)
                        if (view != null) {
                            view.isManaged = true
                            parentView.addChild(view, index)
                            break
                        }
                    }
                } else if (clazz == null && (view == null || layoutId != view.layoutId)) {
                    parentView.removeChild(view)
                    for (factory in viewFactories) {
                        view = factory.fromXml(parentView, layoutId)
                        if (view != null) {
                            view.isManaged = true
                            view.layoutId = layoutId
                            parentView.addChild(view, index)
                            break
                        }
                    }
                }
                check(view != null)
                views.addLast(view)
                indices.addLast(indices.removeLast() + 1)
                indices.addLast(0)
            }

            fun end() {
                val index = indices.last()
                val view = views.last()
                if (view != null && view is ViewGroup && view.layoutId == null &&
                    (mounts[view] == null || mounts[view] === this@Mount)) {
                    if (index < view.childrenCount) {
                        removeUnmanagedViews(view, index, view.childrenCount - index)
                    }
                }
                indices.removeLast()
                if (view != null) {
                    views.removeLast()
                }
            }

            fun <T : Any> attr(name: String, value: T?) {
                val currentView = views.last() ?: return
                val currentValue = currentView[name] as T?
                if (currentValue == null || (currentValue != value && name != ATTR_INIT)) {
                    for (setter in attributeSetters) {
                        if (setter.set(currentView, name, value, currentValue)) {
                            currentView[name] = if (name != ATTR_INIT) value else true
                            return
                        }
                    }
                }
            }

            private fun removeUnmanagedViews(container: ViewGroup, start: Int, count: Int) {
                val end = start + count - 1
                for (i in end downTo start) {
                    val v = container.childAt(i)
                    if (v.isManaged) { // TODO negate? We should have a test covering this case.
                        container.removeChild(v)
                    }
                }
            }

            fun skip() {
                val container = views.last() as ViewGroup
                var index = indices.removeLast()
                while (index < container.childrenCount) {
                    val view = container.childAt(index)
                    if (view.isManaged) {
                        indices.addLast(index)
                        return
                    }
                    index++
                }
                indices.addLast(index)
            }

            fun currentView(): View? {
                return views.lastOrNull()
            }
        }
    }
}

private const val ATTR_MANAGED = "_managed"
private const val ATTR_LAYOUT_ID = "_layoutId"

private val devFlag = true
private fun devError(message: Any): Nothing? = if(devFlag) error(message) else null
