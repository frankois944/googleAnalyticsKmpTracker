package io.github.frankois944.googleAnalyticsKMPTracker.model

/**
 * Consent categories supported by Google Consent Mode (apps/web).
 * Using an enum prevents typos like "ad_stroage".
 */
public enum class ConsentType(
    internal val key: String,
) {
    AdStorage("ad_storage"),
    AdUserData("ad_user_data"),
    AdPersonalization("ad_personalization"),
    AnalyticsStorage("analytics_storage"),
}

/**
 * Consent values are restricted to the two valid states.
 */
public enum class ConsentState(
    internal val value: String,
) {
    Granted("granted"),
    Denied("denied"),
    ;

    internal companion object {
        fun fromString(value: String): ConsentState =
            entries.find {
                it.value.equals(value, true)
            } ?: Granted
    }
}

/**
 * Strongly-typed container for a consent decision set.
 *
 * - You can build it safely via [set]
 * - And convert to a wire-friendly Map<String, String> via [asMap]
 */
public class ConsentSelection private constructor(
    internal val decisions: Map<ConsentType, ConsentState>,
) {
    public operator fun get(type: ConsentType): ConsentState = decisions.getOrElse(type) { ConsentState.Granted }

    internal fun asMap(): Map<String, String> =
        decisions
            .mapKeys { (type, _) -> type.key }
            .mapValues { (_, state) -> state.value }

    internal companion object {
        public fun build(block: Builder.() -> Unit): ConsentSelection = Builder().apply(block).build()
    }

    public class Builder {
        private val decisions = linkedMapOf<ConsentType, ConsentState>()

        public fun set(
            type: ConsentType,
            state: ConsentState,
        ) {
            decisions[type] = state
        }

        internal fun build(): ConsentSelection = ConsentSelection(decisions.toMap())
    }
}
