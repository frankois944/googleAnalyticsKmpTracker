@file:OptIn(ExperimentalTime::class)

package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.GARequest
import io.github.frankois944.googleAnalyticsKMPTracker.UserAgentProvider
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.model.GoogleAnalyticsEventParameterRequest
import io.github.frankois944.googleAnalyticsKMPTracker.model.GoogleAnalyticsEventRequest
import io.github.frankois944.googleAnalyticsKMPTracker.model.GoogleAnalyticsRequest
import io.github.frankois944.googleAnalyticsKMPTracker.utils.UuidGenerator
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

internal class HttpClientDispatcher(
    override val baseURL: String,
    onPrintLog: (String) -> Unit,
    override val apiSecret: String,
) : Dispatcher {
    val client: HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
            install(DefaultRequest)
            install(ContentEncoding) {
                deflate()
                gzip()
                identity()
            }
            defaultRequest {
                url(baseURL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(UserAgent) {
                agent = UserAgentProvider.getUserAgent()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 2.seconds.inWholeMilliseconds
            }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            onPrintLog(message)
                        }
                    }
                level = LogLevel.ALL
            }
        }

    val hearthBeatClient: HttpClient =
        client.config {
            install(HttpTimeout) {
                requestTimeoutMillis = 500.milliseconds.inWholeMilliseconds
            }
        }

    @Throws(Throwable::class, IllegalArgumentException::class)
    override suspend fun sendBulkEvent(events: List<Event>) {
        client
            .post {
                setBody(BulkRequest.create(events))
            }.handleResponse()
    }

    override suspend fun sendSingleEvent(event: Event) {
        val client = if (event.isPing) hearthBeatClient else client
        client
            .post {
                url {
                    parameters.append("firebase_app_id", event.firebaseAppId)
                    parameters.append("api_secret", apiSecret)
                }
                setBody(
                    event.GARequest
                )
    }.handleResponse()
}

private suspend fun HttpResponse.handleResponse() {
    if (!this.status.isSuccess()) {
        val message =
            """
Send event failed with status 
code: ${this.status.value}
body: ${this.bodyAsText()}
                """.trimIndent()
        if (this.status.value >= 400) {
            throw IllegalArgumentException(
                message,
            )
        } else {
            throw Throwable(
                message,
            )
        }
    }
}
}
