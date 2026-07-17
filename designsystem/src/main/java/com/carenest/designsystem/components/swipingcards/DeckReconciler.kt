package com.carenest.designsystem.components.swipingcards

/**
 * Pure ordering + reconciliation logic for the card deck. No Compose or Android
 * dependencies so it can be unit-tested independently.
 *
 * The deck maintains an *optimistic* internal order: a swipe immediately rotates the
 * front card to the back before the external list confirms the change. When the
 * external list later delivers the same mutation it is treated as a confirmation and
 * no animation restart occurs.
 */
internal object DeckReconciler {

    /** Throws [IllegalArgumentException] naming the first duplicate key found. */
    fun requireUniqueKeys(keys: List<Any>) {
        val seen = HashSet<Any>(keys.size)
        for (key in keys) {
            require(seen.add(key)) {
                "SwipingCardStack: duplicate card key '$key'. Keys returned by `key` must be " +
                    "unique within the supplied list."
            }
        }
    }

    /**
     * Moves the front key to the back of [order] (the classic swipe-to-cycle operation).
     * Returns the new order.
     */
    fun rotateFrontToBack(order: List<Any>): List<Any> {
        if (order.size <= 1) return order
        return order.drop(1) + order.first()
    }

    /**
     * Reconcile [currentOrder] against a new [externalKeys] list.
     *
     * - Cards present in both lists stay in the current optimistic position.
     * - Cards added in [externalKeys] but missing from [currentOrder] are appended at the back.
     * - Cards removed from [externalKeys] are dropped wherever they sit.
     *
     * Returns a [ReconcileResult] describing the new order plus the sets of added/removed keys.
     */
    fun reconcile(currentOrder: List<Any>, externalKeys: List<Any>): ReconcileResult {
        val externalSet = externalKeys.toHashSet()
        val currentSet = currentOrder.toHashSet()

        val removed = currentSet - externalSet
        val added = externalSet - currentSet

        // Retain existing cards in their current optimistic position, drop removed ones.
        val retained = currentOrder.filter { it in externalSet }

        // Append newly added keys in external order.
        val externalOrder = externalKeys.filter { it in added }
        val newOrder = retained + externalOrder

        return ReconcileResult(newOrder = newOrder, added = added, removed = removed)
    }
}

/** Result of reconciling the optimistic internal order against a new external order. */
data class ReconcileResult(
    val newOrder: List<Any>,
    val added: Set<Any>,
    val removed: Set<Any>,
)
