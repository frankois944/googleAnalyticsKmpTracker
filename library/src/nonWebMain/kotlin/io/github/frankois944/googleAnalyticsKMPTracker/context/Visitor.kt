package io.github.frankois944.googleAnalyticsKMPTracker.context

import io.github.frankois944.googleAnalyticsKMPTracker.storage.Preferences
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
internal class Visitor(
    val clientId: String,
    val userId: String?,
) {
    companion object {
        internal fun current(): Visitor {
            var clientID = Preferences.clientId
            if (clientID.isNullOrEmpty()) {
                clientID =
                    newClientId().also {
                        Preferences.clientId = it
                    }
            }
            val userId = Preferences.userId
            return Visitor(clientId = clientID, userId = userId)
        }

        private fun newClientId(): String = "${Random.nextInt(1, Int.MAX_VALUE)}.${Random.nextInt(1, Int.MAX_VALUE)}"
    }
}
