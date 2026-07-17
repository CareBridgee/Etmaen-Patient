package com.carenest.designsystem.components.swipingcards

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Creates and remembers a [SwipingCardStackState] to programmatically control
 * the [SwipingCardStack] (e.g. triggering a swipe from a "Next" button).
 */
@Composable
fun rememberSwipingCardStackState(): SwipingCardStackState {
    val scope = rememberCoroutineScope()
    return remember(scope) { SwipingCardStackState(scope) }
}

/**
 * State object for [SwipingCardStack].
 */
class SwipingCardStackState(
    private val scope: CoroutineScope
) {
    internal var deck: DeckState? = null
    internal var onSwipeTrigger: ((SwipeDirection) -> Unit)? = null

    /** Whether an animation (swipe or settle) is currently in progress. */
    val isAnimating: Boolean
        get() = deck?.isAnimating ?: false

    /**
     * Programmatically trigger a swipe of the front card.
     *
     * @param direction the direction to swipe towards.
     */
    fun swipe(direction: SwipeDirection = SwipeDirection.Right) {
        val d = deck ?: return
        if (d.isAnimating) return

        scope.launch {
            d.commitSwipe(
                scope = this,
                direction = direction,
                onRotate = { onSwipeTrigger?.invoke(direction) }
            )
        }
    }
}
