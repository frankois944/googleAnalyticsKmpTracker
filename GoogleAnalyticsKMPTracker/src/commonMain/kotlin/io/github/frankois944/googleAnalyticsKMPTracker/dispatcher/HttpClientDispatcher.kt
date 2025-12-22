package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.UserAgentProvider
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.queryItems
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
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
                agent = userAgent ?: UserAgentProvider.getUserAgent()
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10.seconds.inWholeMilliseconds
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
                setBody(BulkRequest.create(events, tokenAuth))
            }.handleResponse()
    }

    override suspend fun sendSingleEvent(event: Event) {
        val client = if (event.isPing) hearthBeatClient else client
        client
            .get {
                url {
                    event.queryItems.forEach { query ->
                        query.value?.let { value ->
                            parameters.append(query.key, value.toString())
                        }
                        tokenAuth?.let {
                            parameters.append("token_auth", tokenAuth)
                        }
                    }
                }
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
