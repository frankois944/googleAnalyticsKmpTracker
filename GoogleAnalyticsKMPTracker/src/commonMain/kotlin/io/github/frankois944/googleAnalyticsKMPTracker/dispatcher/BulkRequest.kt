@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.queryItems
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

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
                                    event
                                        .queryItems
                                        .filter { it.value != null }
                                        .forEach { item ->
                                            item.value?.let { value ->
                                                add("${item.key}=$value")
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
