package dev.inkremental.platform

/**
 * Test implementation which never reports the need to switch to UI thread.
 */
actual class UiSwitcher actual constructor(action: () -> Unit) {
    actual fun scheduleOnUi(): Boolean = false
}
