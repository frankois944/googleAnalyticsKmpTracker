@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.core

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi

@Serializable
public class Visitor(
    /**
     * Required. Unique identifier for a specific installation of a Firebase app.
     */
    public val clientId: String,
    /**
     * Optional. A unique identifier for a user. See User-ID for cross-platform analysis for more information on this
     * identifier. Can include only utf-8 characters.
     **/
    public val userId: String?,
)
