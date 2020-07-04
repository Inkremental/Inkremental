package dev.inkremental.platform

internal expect fun platformInit()

/**
 * Scheduler which helps with conflating [action] invocations to the UI thread.
 */
expect class UiSwitcher(action: () -> Unit) {
    /**
     * Checks whether it's invoked from UI thread. If so, returns false. If not,
     * plans [action] to be invoked on UI thread and returns true.
     */
    actual fun scheduleOnUi(): Boolean
}

expect fun <T, U> WeakHashMap(): MutableMap<T, U>

expect class WeakReference<T>(referent: T) {
    fun get(): T?
}

/**
 * Represents any UI component.
 */
expect open class View

/**
 * Represents container UI component which can hold child components. Children
 * should be linearly addressable.
 */
expect abstract class ViewGroup : View

/**
 * Returns number of children of this container.
 */
expect val ViewGroup.childrenCount: Int

/**
 * Returns child component of this container at the [position].
 */
expect fun ViewGroup.childAt(position: Int): View

/**
 * Adds [child] to the list of children of this container at the [position].
 */
expect fun ViewGroup.addChild(child: View, position: Int)

/**
 * Removes first child equal to [child] from this container.
 */
expect fun ViewGroup.removeChild(child: View?)

/**
 * Removes all children from this container.
 */
expect fun ViewGroup.clearChildren()
