package io.github.frankois944.googleAnalyticsKMPTracker

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalWasmJsInterop::class)
/**
 * Loads the gtag.js script and initializes the dataLayer.
 */
internal fun loadGtagJS(measurementId: String) {
    js("""
        (function(id) {
            var script = document.createElement('script');
            script.async = true;
            script.src = 'https://www.googletagmanager.com/gtag/js?id=' + id;
            document.head.appendChild(script);

            window.dataLayer = window.dataLayer || [];
            window.gtag = function() {
                window.dataLayer.push(arguments);
            };
            window.gtag('js', new Date());
            window.gtag('config', id);
        })(measurementId);
    """)
}

/**
 * Retrieves the client ID from gtag.
 */
internal fun getClientId(measurementId: String) {
    js("""
        window.gtag('get', measurementId, 'client_id', (clientId) => {
            console.log(clientId);
            console.log("\n");
        });
    """)
}