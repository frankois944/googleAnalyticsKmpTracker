package io.github.frankois944.matomoKMPTracker.dispatcher

import io.github.frankois944.matomoKMPTracker.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BulkRequest(
    @SerialName("requests")
    val requests: List<String>,
    @SerialName("token_auth")
    val tokenAuth: String?,
) {
    companion object {
        fun create(
            requests: List<Event>,
            tokenAuth: String?,
        ): BulkRequest =
            BulkRequest(
                requests = buildRequest(requests),
                tokenAuth = tokenAuth,
            )

        private fun buildRequest(events: List<Event>): List<String> =
            buildList {
                events.forEach { event ->
                    val query =
                        buildString {
                            append("?")
                            append(
                                buildList {
                                    event.queryItems.forEach { item ->
                                        if (item.value != null) {
                                            add("${item.key}=${item.value}")
                                        }
                                    }
                                }.joinToString("&"),
                            )
                        }
                    add(query)
                }
            }
    }
}
