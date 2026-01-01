package io.github.frankois944.googleAnalyticsKMPTracker.user

import android.os.Build
import android.webkit.WebSettings
import io.github.frankois944.googleAnalyticsKMPTracker.context.ContextObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale

internal actual object UserAgentProvider {
    fun getHttpAgent(): String? =
        runBlocking {
            val context = ContextObject.context?.get()
            requireNotNull(context) { "Android Context must be not null" }
            withContext(Dispatchers.Main) {
                WebSettings.getDefaultUserAgent(context)
            }
        }

    fun getJVMVersion(): String? = getSystemProperty("java.vm.version")

    fun getSystemProperty(key: String): String? = System.getProperty(key)

    fun getRelease(): String = Build.VERSION.RELEASE

    fun getModel(): String = Build.MODEL

    fun getBuildId(): String = Build.ID

    actual fun getUserAgent(): String {
        val httpAgent: String? = getHttpAgent()
        if (httpAgent == null || httpAgent.startsWith("Apache-HttpClient/UNAVAILABLE (java")) {
            val dalvik = getJVMVersion() ?: "0.0.0"
            val android = getRelease()
            val model = getModel()
            val build = getBuildId()
            return String.Companion.format(
                Locale.US,
                "Dalvik/%s (Linux; U; Android %s; %s Build/%s)",
                dalvik,
                android,
                model,
                build,
            )
        }
        return httpAgent
    }

}