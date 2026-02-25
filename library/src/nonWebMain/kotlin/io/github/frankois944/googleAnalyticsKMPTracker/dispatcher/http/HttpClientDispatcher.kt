package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http

import io.github.frankois944.googleAnalyticsKMPTracker.context.UserAgentProvider
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.Dispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder.getGaBody
import io.github.frankois944.googleAnalyticsKMPTracker.events.Event
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
import kotlin.time.Duration.Companion.seconds

internal class HttpClientDispatcher(
    override val baseURL: String?,
    override val apiSecret: String,
    onPrintLog: (String) -> Unit,
) : Dispatcher {
    private val isValidationMode: Boolean = baseURL.contains("/debug/")

    val client: HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
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
                agent = UserAgentProvider.userAgent
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

    override suspend fun sendBulkEvent(events: List<Event>) {
        client
            .post {
                url {
                    parameters.append("measurement_id", events.first().measurementId)
                    parameters.append("api_secret", apiSecret)
                }
                setBody(
                    events.getGaBody(isValidationMode),
                )
            }.handleResponse()
    }

    override suspend fun sendSingleEvent(event: Event) {
        client
            .post {
                url {
                    parameters.append("measurement_id", event.measurementId)
                    parameters.append("api_secret", apiSecret)
                }
                setBody(
                    event.getGaBody(isValidationMode),
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
            throw IllegalArgumentException(
                message,
            )
        }
    }
}
