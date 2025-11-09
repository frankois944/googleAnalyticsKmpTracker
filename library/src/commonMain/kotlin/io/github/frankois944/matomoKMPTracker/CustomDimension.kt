package io.github.frankois944.matomoKMPTracker

import kotlinx.serialization.Serializable

/**
 * For more information on custom dimensions visit https://piwik.org/docs/custom-dimensions/
 */
@Serializable
public class CustomDimension(
    /**
     *  The index of the dimension. A dimension with this index must be setup in the Matomo backend.
     */
    public val index: Int,
    /**
     *  The value you want to set for this dimension.
     */
    public val value: String,
)
