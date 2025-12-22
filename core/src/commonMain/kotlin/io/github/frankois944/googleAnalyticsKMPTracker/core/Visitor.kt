@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.core

import kotlin.uuid.ExperimentalUuidApi

public class Visitor(
    /**
     * The unique visitor ID, must be a 16 characters hexadecimal string.
     * Every unique visitor must be assigned a different ID and this ID must not change after it is assigned.
     * If this value is not set Matomo (formerly Piwik) will still track visits,
     * but the unique visitors metric might be less accurate.
     *
     * api-key: _id
     */
    public val id: String,
    /**
     * Defines the User ID for this request.
     *
     * User ID is any non-empty unique string identifying the user (such as an email address or a username).
     * To access this value, users must be logged-in in your system so you can fetch this user ID from your system,
     * and pass it to Matomo. The User ID appears in the visits log, the Visitor profile,
     * and you can Segment reports for one or several User ID (userId segment).
     * When specified, the User ID will be "enforced". This means that if there is no recent visit with this User ID,
     * a new one will be created. If a visit is found in the last 30 minutes with your specified User ID,
     * then the new action will be recorded to this existing visit.
     *
     * api-key: uid
     **/
    public val userId: String?,
) {
    public companion object
}
