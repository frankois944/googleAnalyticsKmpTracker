package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.web

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event

internal enum class ConsentType(
    val value: String,
) {
    AD_USER_DATA("ad_user_data"),
    AD_PERSONALIZATION("ad_personalization"),
    AD_STORAGE("ad_storage"),
    ANALYTICS_STORAGE("analytics_storage"),
}

internal expect class WebDispatcher() {
    /**
     * Use the event command to send event data.
     */
    fun sendSingleEvent(event: Event)

    /**
     * The set command lets you define parameters that will be associated with every subsequent event on the page.
     */
    fun setUserProperty(
        key: String,
        value: Any?,
    )

    /**
     * Use the consent command to configure consent.
     */
    fun setConsent(type: List<Pair<ConsentType, Boolean>>)
}
