package io.github.frankois944.googleAnalyticsKMPTracker.core

/**
 * Order item as described in: https://matomo.org/faq/reports/advanced-manually-tracking-ecommerce-actions-in-matomo/#tracking-orders-to-matomo-required
 */
public class OrderItem(
    // The SKU of the order item
    public val sku: String,
    // The name of the order item
    public val name: String = "",
    // The category of the order item
    public val category: String = "",
    // The price of the order item
    public val price: Double = 0.0,
    // The quantity of the order item
    public val quantity: Int = 1,
) {
    public companion object
}
