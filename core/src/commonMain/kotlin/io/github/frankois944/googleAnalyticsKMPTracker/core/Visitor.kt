@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.core

import kotlin.uuid.ExperimentalUuidApi

public class Visitor(
    /**
     * Required. Unique identifier for a specific installation of a Firebase app.
     */
    public val appInstanceId: String,
    /**
     * Optional. A unique identifier for a user. See User-ID for cross-platform analysis for more information on this
     * identifier. Can include only utf-8 characters.
     **/
    public val userId: String?,
) {
    public companion object
}
