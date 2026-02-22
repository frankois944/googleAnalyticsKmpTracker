package io.github.frankois944.googleAnalyticsKMPTracker.model

/**
 * Consent categories supported by Google Consent Mode (apps/web).
 * Using an enum prevents typos like "ad_stroage".
 */
internal enum class ConsentType(
    val key: String,
) {
    AdStorage("ad_storage"),
    AdUserData("ad_user_data"),
    AdPersonalization("ad_personalization"),
    AnalyticsStorage("analytics_storage"),
    ;

    override fun toString(): String = key
}

/**
 * Consent values are restricted to the two valid states.
 */
internal enum class ConsentState(
    val value: String,
) {
    Granted("granted"),
    Denied("denied"),
    ;

    override fun toString(): String = value
}

/**
 * Strongly-typed container for a consent decision set.
 *
 * - You can build it safely via [set]
 * - And convert to a wire-friendly Map<String, String> via [asMap]
 */
internal class ConsentSelection private constructor(
    private val decisions: Map<ConsentType, ConsentState>,
) {
    fun get(type: ConsentType): ConsentState? = decisions[type]

    fun asMap(): Map<String, String> =
        decisions
            .mapKeys { (type, _) -> type.key }
            .mapValues { (_, state) -> state.value }

    companion object {
        fun build(block: Builder.() -> Unit): ConsentSelection = Builder().apply(block).build()
    }

    class Builder {
        private val decisions = linkedMapOf<ConsentType, ConsentState>()

        fun set(
            type: ConsentType,
            state: ConsentState,
        ) {
            decisions[type] = state
        }

        fun build(): ConsentSelection = ConsentSelection(decisions.toMap())
    }
}
