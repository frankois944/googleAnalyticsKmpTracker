package io.github.frankois944.googleAnalyticsKMPTracker.context

import android.content.Context
import java.lang.ref.WeakReference

internal actual fun storeContext(context: Any?) {
    require(context is Context) { "Context must be of type Android Context" }
    ContextObject.context = WeakReference(context)
}
