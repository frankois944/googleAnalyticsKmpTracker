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
 */
public class ConsentSelection private constructor(
    internal val decisions: Map<ConsentType, ConsentState>,
) {
    public operator fun get(type: ConsentType): ConsentState = decisions.getOrElse(type) { ConsentState.Granted }

    override fun toString(): String = decisions.entries.joinToString(", ") { "${it.key}: ${it.value}" }

    internal companion object {
        fun build(block: Builder.() -> Unit): ConsentSelection = Builder().apply(block).build()
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
