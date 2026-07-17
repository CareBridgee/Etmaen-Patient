package com.carenest.designsystem.components.swipingcards

/**
 * Information about a committed swipe, emitted exactly once when the front card
 * crosses the swipe threshold.
 *
 * @param card the swiped card.
 * @param key the swiped card's stable key.
 * @param direction the dominant direction of the commit gesture.
 * @param resultingOrder the deck's internal order after the rotation, in card terms.
 */
data class SwipeResult<T>(
    val card: T,
    val key: Any,
    val direction: SwipeDirection,
    val resultingOrder: List<T>,
)
