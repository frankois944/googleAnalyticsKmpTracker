package io.github.frankois944.matomoKMPTracker

import kotlinx.serialization.Serializable

/**
 * Order item as described in: https://matomo.org/faq/reports/advanced-manually-tracking-ecommerce-actions-in-matomo/#tracking-orders-to-matomo-required
 */
@Serializable
public class OrderItem(
    // The SKU of the order item
    public val sku: String,
    // The name of the order item
    public val name: String = "",
    // The category of the order item
    public val category: String = "",
    // The price of the order item
    public val price: Float = 0.0f,
    // The quantity of the order item
    public val quantity: Int = 1,
)
