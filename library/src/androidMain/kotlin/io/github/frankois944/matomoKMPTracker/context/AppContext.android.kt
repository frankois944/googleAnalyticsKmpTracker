package io.github.frankois944.matomoKMPTracker.context

import io.github.frankois944.matomoKMPTracker.NativeContext
import java.lang.ref.WeakReference

internal object ContextObject {
    var context: WeakReference<NativeContext>? = null
}

internal actual fun storeContext(context: NativeContext?) {
    ContextObject.context = WeakReference(context)
}
