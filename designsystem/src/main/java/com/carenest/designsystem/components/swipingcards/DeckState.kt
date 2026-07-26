package com.carenest.designsystem.components.swipingcards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

// ─── Spring constants ───────────────────────────────────────────────────────

private const val SETTLE_SPRING_DAMPING = 0.75f
private const val SETTLE_SPRING_STIFFNESS = 1000f
private const val PROMOTE_SPRING_DAMPING = 0.8f
private const val PROMOTE_SPRING_STIFFNESS = 700f

// ─── Background repulsion factors ───────────────────────────────────────────

private const val BACKGROUND_VERTICAL_REPULSION_FACTOR = 0.35f
private const val BACKGROUND_HORIZONTAL_REPULSION_FACTOR = 0.25f

private const val DEG_TO_RAD = PI / 180.0

// ─── Stack position visual config ───────────────────────────────────────────

/**
 * All visual properties for a card at a given stack position — single source of truth.
 */
internal data class StackPositionConfig(
    val scale: Float,
    val rotationZ: Float,
    val alpha: Float,
    val repulsionFactor: Float,
    val elevation: Dp,
)

internal fun stackPositionConfig(position: Int): StackPositionConfig = when (position) {
    0 -> StackPositionConfig(scale = 1.0f, rotationZ = 0f, alpha = 1f, repulsionFactor = 0f, elevation = 12.dp)
    1 -> StackPositionConfig(scale = 0.92f, rotationZ = -6f, alpha = 1f, repulsionFactor = 1.0f, elevation = 8.dp)
    2 -> StackPositionConfig(scale = 0.84f, rotationZ = 4f, alpha = 1f, repulsionFactor = 0.6f, elevation = 4.dp)
    3 -> StackPositionConfig(scale = 0.76f, rotationZ = -2f, alpha = 1f, repulsionFactor = 0.3f, elevation = 2.dp)
    else -> StackPositionConfig(scale = 0.7f, rotationZ = 2f, alpha = 0.8f, repulsionFactor = 0.3f, elevation = 2.dp)
}

/**
 * Compute X so a background card's bottom corner aligns with card 0's bottom corner.
 * Odd positions (1, 3) align left-bottom; even positions (2) align right-bottom.
 */
internal fun idleTranslationXPx(
    position: Int,
    scale: Float,
    rotationZDeg: Float,
    cardWidthPx: Float,
): Float {
    if (position == 0) return 0f
    val cosR = cos(rotationZDeg * DEG_TO_RAD).toFloat()
    val halfW = cardWidthPx / 2f
    return if ((position % 2) == 1) {
        -halfW + (halfW * scale) * cosR
    } else {
        halfW - (halfW * scale) * cosR
    }
}

// ─── Per-card animated state ─────────────────────────────────────────────────

/**
 * Per-card animated properties — each key tracks its own visual state independently.
 * [Animatable] is used for spring-based settling/promotion; the public
 * [repulsionX] and [repulsionY] vars are used for direct synchronous
 * updates during drag (background repulsion), falling back to the Animatable value
 * when no drag is active.
 */
@Stable
internal class CardAnimState(config: StackPositionConfig, initialXPx: Float) {
    val scale = Animatable(config.scale)
    val rotationZ = Animatable(config.rotationZ)
    val translationX = Animatable(initialXPx)
    val translationY = Animatable(0f)
    val alpha = Animatable(config.alpha)
    val elevation = Animatable(config.elevation.value)

    // Direct-write state for synchronous background repulsion during drag.
    // The graphicsLayer reads these instead of the Animatable values while dragging.
    var repulsionX by mutableFloatStateOf(initialXPx)
    var repulsionY by mutableFloatStateOf(0f)
    var isDragging by mutableStateOf(value = false)
}

// ─── DeckState ───────────────────────────────────────────────────────────────

/**
 * Optimistic, key-addressed state for the deck. Holds the internal order (which may
 * be ahead of the caller's list after a swipe) and the per-card animation state.
 */
@Stable
internal class DeckState {

    var internalOrder by mutableStateOf<List<Any>>(emptyList())
        private set

    var swipedKey by mutableStateOf<Any?>(null)
        private set

    var isAnimating by mutableStateOf(false)
    var hasPassedThreshold by mutableStateOf(false)

    // Config mirrored from the composable each composition.
    var maxVisibleCards by mutableIntStateOf(4)
    var maxRotationY by mutableFloatStateOf(38f)
    var swipeThresholdFraction by mutableFloatStateOf(0.20f)

    // Layout-dependent values — set by the composable from measured constraints.
    var containerWidthPx by mutableFloatStateOf(0f)
    var containerHeightPx by mutableFloatStateOf(0f)

    // Top-card drag state (written by the gesture handler synchronously).
    var dragX by mutableFloatStateOf(0f)
    var dragY by mutableFloatStateOf(0f)
    var rotationY by mutableFloatStateOf(0f)

    // Per-key anim state map.
    private val animStates = mutableStateListOf<Pair<Any, CardAnimState>>()

    // ── Initialisation ──────────────────────────────────────────────────────

    /**
     * Initialise the deck from a fresh [keys] list. Called exactly once before any
     * rendering; subsequent external changes should go through [applyReconcile].
     */
    fun init(keys: List<Any>) {
        internalOrder = keys
        animStates.clear()
        keys.forEachIndexed { index, key ->
            animStates.add(key to cardAnimStateForPosition(index))
        }
    }

    // ── Anim state access ───────────────────────────────────────────────────

    fun animStateFor(key: Any): CardAnimState? =
        animStates.firstOrNull { it.first == key }?.second

    private fun cardAnimStateForPosition(position: Int): CardAnimState {
        val cfg = stackPositionConfig(position)
        val idleX = idleTranslationXPx(
            position = position,
            scale = cfg.scale,
            rotationZDeg = cfg.rotationZ,
            cardWidthPx = containerWidthPx,
        )
        return CardAnimState(cfg, idleX)
    }

    // ── Reconciliation ──────────────────────────────────────────────────────

    /**
     * Apply the result of a [DeckReconciler.reconcile] call: update [internalOrder],
     * remove anim state for dropped keys, and add anim state for new keys.
     */
    fun applyReconcile(result: ReconcileResult) {
        internalOrder = result.newOrder
        result.removed.forEach { key -> animStates.removeAll { it.first == key } }
        result.added.forEach { key ->
            val position = result.newOrder.indexOf(key)
            if (position >= 0) {
                animStates.add(key to cardAnimStateForPosition(position))
            }
        }
    }

    // ── Swipe mechanics ─────────────────────────────────────────────────────

    /**
     * Called synchronously inside the drag gesture handler each frame.
     * Updates drag offsets, rotationY, threshold state, and background repulsion.
     * Background repulsion is applied via direct [CardAnimState.repulsionX/Y] writes
     * (not coroutines) so Compose reads them on the very next frame.
     */
    fun onDrag(dx: Float, dy: Float) {
        dragX += dx
        dragY += dy

        // Perspective Y-rotation driven by horizontal drag.
        val swipeThresholdPx = containerWidthPx * swipeThresholdFraction
        rotationY = (dragX / swipeThresholdPx) * maxRotationY

        hasPassedThreshold =
            abs(dragX) >= swipeThresholdPx ||
                abs(dragY) >= containerHeightPx * swipeThresholdFraction

        // Repel background cards proportional to drag magnitude.
        val dragMagnitude = sqrt(dragX * dragX + dragY * dragY)
        internalOrder.drop(1).forEachIndexed { idx, key ->
            val pos = idx + 1
            val cfg = stackPositionConfig(pos)
            animStateFor(key)?.let { anim ->
                val idleX = idleTranslationXPx(pos, cfg.scale, cfg.rotationZ, containerWidthPx)
                val repelX = dragX * cfg.repulsionFactor * BACKGROUND_HORIZONTAL_REPULSION_FACTOR
                val repelY = -dragMagnitude * cfg.repulsionFactor * BACKGROUND_VERTICAL_REPULSION_FACTOR
                anim.repulsionX = idleX + repelX
                anim.repulsionY = repelY
                anim.isDragging = true
            }
        }
    }

    /**
     * Settle the top card back to its idle position after a cancelled drag.
     */
    suspend fun settleBack() {
        val topKey = internalOrder.firstOrNull() ?: return
        val topAnim = animStateFor(topKey) ?: return
        val settleSpec = spring<Float>(dampingRatio = SETTLE_SPRING_DAMPING, stiffness = SETTLE_SPRING_STIFFNESS)

        // Capture drag state to snap Animatable values for a seamless transition.
        val currentDragX = dragX
        val currentDragY = dragY

        // Reset global drag state first.
        dragX = 0f
        dragY = 0f
        rotationY = 0f
        hasPassedThreshold = false

        coroutineScope {
            // Settle top card from its swiped position.
            launch {
                topAnim.translationX.snapTo(topAnim.translationX.value + currentDragX)
                topAnim.translationY.snapTo(topAnim.translationY.value + currentDragY)
                topAnim.translationX.animateTo(0f, settleSpec)
                topAnim.translationY.animateTo(0f, settleSpec)
                topAnim.elevation.animateTo(stackPositionConfig(0).elevation.value, settleSpec)
            }

            // Settle all background cards.
            internalOrder.drop(1).forEachIndexed { idx, key ->
                val pos = idx + 1
                val cfg = stackPositionConfig(pos)
                animStateFor(key)?.let { bgAnim ->
                    val idleX = idleTranslationXPx(pos, cfg.scale, cfg.rotationZ, containerWidthPx)
                    launch {
                        // Snap to current repulsion before disabling drag mode to prevent jumps.
                        if (bgAnim.isDragging) {
                            bgAnim.translationX.snapTo(bgAnim.repulsionX)
                            bgAnim.translationY.snapTo(bgAnim.repulsionY)
                            bgAnim.isDragging = false
                        }
                        bgAnim.translationX.animateTo(idleX, settleSpec)
                        bgAnim.translationY.animateTo(0f, settleSpec)
                    }
                }
            }
        }
    }

    /**
     * Commit a swipe: fly the top card off-screen, rotate the deck, and spring-animate
     * the remaining cards into their new stack positions.
     *
     * @param direction committed swipe direction.
     */
    suspend fun commitSwipe(
        scope: CoroutineScope,
        direction: SwipeDirection,
        onRotate: () -> Unit = {},
    ) {
        isAnimating = true

        val topKey = internalOrder.firstOrNull() ?: run { isAnimating = false; return }
        val topAnim = animStateFor(topKey) ?: run { isAnimating = false; return }

        // Mark this key as swiping so it stays visible in the composition.
        swipedKey = topKey

        // Capture drag state to snap Animatable values for a seamless fly-out.
        val currentDragX = dragX
        val currentDragY = dragY

        // Target off-screen position for the fly-out.
        val flyX = when (direction) {
            SwipeDirection.Left -> -containerWidthPx * 1.5f
            SwipeDirection.Right -> containerWidthPx * 1.5f
            else -> dragX
        }
        val flyY = when (direction) {
            SwipeDirection.Up -> -containerHeightPx * 1.5f
            SwipeDirection.Down -> containerHeightPx * 1.5f
            else -> dragY
        }

        val flySpec = spring<Float>(dampingRatio = 1.0f, stiffness = 1500f)

        // Snap and fly-out the top card.
        // We reset dragX/Y/rotationY AFTER snapping topAnim but BEFORE deck rotation.
        topAnim.translationX.snapTo(topAnim.translationX.value + currentDragX)
        topAnim.translationY.snapTo(topAnim.translationY.value + currentDragY)

        dragX = 0f
        dragY = 0f
        rotationY = 0f
        hasPassedThreshold = false

        // Launch fly-out in parallel
        scope.launch {
            coroutineScope {
                launch { topAnim.translationX.animateTo(flyX, flySpec) }
                launch { topAnim.translationY.animateTo(flyY, flySpec) }
                launch { topAnim.alpha.animateTo(0f, flySpec) }
            }
            swipedKey = null
        }

        // Rotate deck: top → back (optimistic update).
        val newOrder = DeckReconciler.rotateFrontToBack(internalOrder)
        internalOrder = newOrder
        onRotate()

        // Reset top card anim state to the back position (invisible, ready to cycle in).
        val backPosition = newOrder.lastIndex
        val backCfg = stackPositionConfig(backPosition)
        val backIdleX = idleTranslationXPx(backPosition, backCfg.scale, backCfg.rotationZ, containerWidthPx)
        
        topAnim.scale.snapTo(backCfg.scale)
        topAnim.rotationZ.snapTo(backCfg.rotationZ)
        topAnim.elevation.snapTo(backCfg.elevation.value)
        topAnim.translationX.snapTo(backIdleX + flyX) 
        topAnim.translationY.snapTo(flyY)
        topAnim.alpha.snapTo(0f)

        val promoteSpec = spring<Float>(dampingRatio = PROMOTE_SPRING_DAMPING, stiffness = PROMOTE_SPRING_STIFFNESS)

        // Promote remaining cards and cycle the back card in — all in parallel.
        coroutineScope {
            newOrder.forEachIndexed { index, key ->
                val cfg = stackPositionConfig(index)
                val idleX = idleTranslationXPx(index, cfg.scale, cfg.rotationZ, containerWidthPx)
                animStateFor(key)?.let { anim ->
                    if (anim.isDragging) {
                        anim.translationX.snapTo(anim.repulsionX)
                        anim.translationY.snapTo(anim.repulsionY)
                        anim.isDragging = false
                    }
                    launch { anim.scale.animateTo(cfg.scale, promoteSpec) }
                    launch { anim.rotationZ.animateTo(cfg.rotationZ, promoteSpec) }
                    launch { anim.translationX.animateTo(idleX, promoteSpec) }
                    launch { anim.translationY.animateTo(0f, promoteSpec) }
                    launch { anim.alpha.animateTo(cfg.alpha, promoteSpec) }
                    launch { anim.elevation.animateTo(cfg.elevation.value, promoteSpec) }
                }
            }
        }

        isAnimating = false
    }
}
