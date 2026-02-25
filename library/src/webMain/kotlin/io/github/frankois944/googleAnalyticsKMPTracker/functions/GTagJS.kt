@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.functions

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.js

// https://developers.google.com/tag-platform/gtagjs/reference#config

/*
    AdStorage("ad_storage"),
    AdUserData("ad_user_data"),
    AdPersonalization("ad_personalization"),
    AnalyticsStorage("analytics_storage"),
 */

/**
 * Loads the gtag.js script and initializes the dataLayer.
 */
@OptIn(ExperimentalWasmJsInterop::class)
internal fun loadGtagJS(
    measurementId: String,
    adStorageStatus: String,
    adUserDataStatus: String,
    adPersonalizationStatus: String,
    analyticsStorageStatus: String,
    userId: String?,
    callback: () -> Unit,
) {
    js(
        """
        (function(id) {
            var script = document.createElement('script');
            script.async = true;
            script.src = 'https://www.googletagmanager.com/gtag/js?id=' + id;
            document.head.appendChild(script);

            window.dataLayer = window.dataLayer || [];
            window.gtag = function() {
                window.dataLayer.push(arguments);
            };
            // consent
            gtag('consent', 'default', {
                'ad_storage': adStorageStatus,
                'ad_user_data': adUserDataStatus,
                'ad_personalization': adPersonalizationStatus,
                'analytics_storage': analyticsStorageStatus   
            });
            // user_id
            gtag('config', id, {
                'user_id': userId,
                'debug_mode': true
            });
            window.gtag('js', new Date());
            window.gtag('config', id);
            callback()
        })(measurementId);
    """,
    )
}

internal fun config(
    configName: String,
    params: JsAny?,
) {
    js(
        """
            console.log(configName, params)
            window.gtag('config', configName, params);
    """,
    )
}

internal fun config(configName: String) {
    js(
        """
        window.gtag('config', configName);
    """,
    )
}

internal fun sendEvent(
    eventName: String,
    params: JsAny?,
) {
    js(
        """
        window.gtag('event', eventName, params);
    """,
    )
}

internal fun sendEvent(eventName: String) {
    js(
        """
        window.gtag('event', eventName);
    """,
    )
}

internal fun set(
    parameterName: String,
    params: JsAny?,
) {
    js(
        """
            console.log('set', parameterName, params);
        window.gtag('set', parameterName, params);
    """,
    )
}

internal fun set(parameterName: String) {
    js(
        """
        window.gtag('set', parameterName);
    """,
    )
}

internal fun consent(
    consentArgs: JsAny,
    consentParams: JsAny,
) {
    js(
        """
        window.gtag('consent', consentArgs, consentParams);
    """,
    )
}

internal fun consent(consentArgs: JsAny) {
    js(
        """
        window.gtag('consent', consentArgs);
    """,
    )
}

internal fun get(
    target: String,
    fieldName: String,
    callback: (String) -> Unit,
) {
    js(
        """
        window.gtag('get', target, fieldName, function(field) {
            callback(field);
        });
    """,
    )
}
