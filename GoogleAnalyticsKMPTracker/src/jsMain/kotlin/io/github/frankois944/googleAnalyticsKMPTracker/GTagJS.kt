@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalWasmJsInterop::class)
/**
 * Loads the gtag.js script and initializes the dataLayer.
 */
internal fun loadGtagJS(measurementId: String) {
    js("""
(function () {
    var MEASUREMENT_ID = measurementId;
    
    // Create dataLayer
    window.dataLayer = window.dataLayer || [];
    
    // Define gtag
    window.gtag = function () {
    window.dataLayer.push(arguments);
    };
    
    // Load Google tag script
    var script = document.createElement("script");
    script.async = true;
    script.src =
    "https://www.googletagmanager.com/gtag/js?id=" + MEASUREMENT_ID;
    document.head.appendChild(script);
    
    // Initialize GA
    window.gtag("js", new Date());
    window.gtag("config", MEASUREMENT_ID);
})();
    """)
}


internal fun sendEvent(eventName: String, params: JsAny?) {
    js("""
        window.gtag('event', eventName, params);
    """)
}
internal fun set(parameterName: String, params: JsAny?) {
    js("""
        window.gtag('set', eventName, params);
    """)
}
internal fun consent(consentArgs: JsAny) {
    js("""
        window.gtag('consent', consentArgs);
    """)
}