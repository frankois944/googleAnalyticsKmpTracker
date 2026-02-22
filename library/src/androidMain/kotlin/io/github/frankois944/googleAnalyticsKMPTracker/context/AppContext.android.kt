package io.github.frankois944.googleAnalyticsKMPTracker.context

import android.content.Context
import java.lang.ref.WeakReference

public object ContextObject {
    public var context: WeakReference<Context>? = null
}

internal actual fun storeContext(context: Any?) {
    require(context is Context) { "Context must be not null and of type Android Context" }
    ContextObject.context = WeakReference(context)
}
