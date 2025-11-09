package io.github.frankois944.matomoKMPTracker.dispatcher

import io.github.frankois944.matomoKMPTracker.Event
import io.github.frankois944.matomoKMPTracker.UserAgentProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.BadContentTypeFormatException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class HttpClientDispatcher(
    override val baseURL: String,
    override val userAgent: String?,
    val tokenAuth: String?,
    onPrintLog: (String) -> Unit,
) : Dispatcher {
    val client: HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(DefaultRequest)
            defaultRequest {
                url(baseURL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(UserAgent) {
                agent = userAgent ?: UserAgentProvider.getUserAgent()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60.seconds.inWholeMilliseconds
            }
            install(Logging) {
                logger =
                    object : io.ktor.client.plugins.logging.Logger {
                        override fun log(message: String) {
                            onPrintLog(message)
                        }
                    }
                level = io.ktor.client.plugins.logging.LogLevel.ALL
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryIf { _, response ->
                    !response.status.isSuccess()
                }
                retryOnExceptionIf { _, cause ->
                    cause is IOException
                }
                delayMillis { retry ->
                    retry * 1000L
                }
            }
        }

    val hearthBeatClient: HttpClient =
        client.config {
            install(HttpTimeout) {
                requestTimeoutMillis = 500.milliseconds.inWholeMilliseconds
            }
            install(HttpRequestRetry) {
                maxRetries = 0
            }
        }

    override suspend fun sendPing(event: Event) {
        if (event.isPing) {
            val result =
                hearthBeatClient.post {
                    setBody(BulkRequest.create(listOf(event), tokenAuth))
                }
            if (!result.status.isSuccess()) {
                throw Throwable("Send ping event failed with status code: ${result.status.value}")
            }
        }
    }

    @Throws(Throwable::class, IllegalArgumentException::class)
    override suspend fun send(events: List<Event>) {
        val result =
            client.post {
                setBody(BulkRequest.create(events, tokenAuth))
            }
        if (!result.status.isSuccess()) {
            val message =
                """
                Send events failed with status 
                code: ${result.status.value}
                body: ${result.bodyAsText()}
                """.trimIndent()
            if (result.status.value == 500) {
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
