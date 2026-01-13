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
    var MEASUREMENT_ID = "measurementId";
    
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

/**
 * Retrieves the client ID from gtag.
 */
internal fun getClientId(measurementId: String) {
    js("""
        window.gtag('get', measurementId, 'client_id', function(clientId) {
            console.log(clientId);
            console.log("\n");
        });
    """)
}