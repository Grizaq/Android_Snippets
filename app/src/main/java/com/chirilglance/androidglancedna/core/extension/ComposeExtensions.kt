package com.chirilglance.androidglancedna.core.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

/**
 * Create a debounced click modifier to prevent double clicks.
 * This non-composable version uses a static timestamp but is less reliable for
 * multiple buttons in the same UI, requires care when using multiple on the same screen
 *
 * Example:
 * ```
 * Button(
 *     onClick = { /* Not used */ },
 *     modifier = Modifier.simpleDebounceClickable(onClick = { navigateToDetail() })
 * )
 * ```
 *
 * @param debounceTime The time to wait between clicks (in milliseconds)
 * @param onClick The click handler
 */
fun Modifier.simpleDebounceClickable(
    debounceTime: Long = 300L, onClick: () -> Unit
): Modifier {
    // Use a companion object to hold the timestamp statically
    // Note: This is a simple approach that works for basic cases
    return clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - DebounceInfo.lastClickTime > debounceTime) {
            DebounceInfo.lastClickTime = currentTime
            onClick()
        }
    }
}

/**
 * Helper object to store the last click time for simpleDebounceClickable
 */
private object DebounceInfo {
    var lastClickTime: Long = 0
}

/**
 * Create a ripple-less clickable modifier without requiring @Composable context.
 * This is a simplified version that doesn't require remember.
 *
 * Example:
 * ```
 * Box(
 *     modifier = Modifier
 *         .size(48.dp)
 *         .simpleNoRippleClickable { onItemClick() }
 * )
 * ```
 *
 * @param onClick The click handler
 */
fun Modifier.simpleNoRippleClickable(onClick: () -> Unit): Modifier = clickable(
    interactionSource = MutableInteractionSource(), indication = null
) {
    onClick()
}

/**
 * A version that requires @Composable context but provides proper state management.
 * Use this when you need proper per-instance state.
 */
@Composable
fun Modifier.debouncedClick(
    debounceTime: Long = 300L, onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }

    return this.clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            onClick()
        }
    }
}

/**
 * A version that requires @Composable context but provides proper state management.
 * Use this when you need proper per-instance state.
 */
@Composable
fun Modifier.noRippleClickable(
    enabled: Boolean = true, onClickLabel: String? = null, role: Role? = null, onClick: () -> Unit
): Modifier = this.then(
    if (enabled) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick
        )
    } else {
        Modifier
    }
)

/**
 * Conditionally apply a modifier based on a condition.
 * Useful for dynamic styling based on state.
 *
 * Example:
 * ```
 * Modifier
 *   .conditional(isSelected) {
 *     background(MaterialTheme.colorScheme.primary)
 *   }
 * ```
 *
 * @param condition The condition to check
 * @param modifier The modifier to apply if the condition is true
 */
fun Modifier.conditional(
    condition: Boolean, modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        this.then(modifier(Modifier))
    } else {
        this
    }
}

/**
 * Apply a modifier only if the condition is true, with an optional else branch.
 * Useful for conditional styling with an alternative.
 *
 * Example:
 * ```
 * Modifier.applyIf(isSelected,
 *   ifTrue = { background(MaterialTheme.colorScheme.primary) },
 *   ifFalse = { background(MaterialTheme.colorScheme.surface) }
 * )
 * ```
 *
 * @param condition The condition to check
 * @param ifTrue The modifier to apply if the condition is true
 * @param ifFalse The modifier to apply if the condition is false (optional)
 */
fun Modifier.applyIf(
    condition: Boolean, ifTrue: Modifier.() -> Modifier, ifFalse: (Modifier.() -> Modifier)? = null
): Modifier {
    return if (condition) {
        then(ifTrue(Modifier))
    } else if (ifFalse != null) {
        then(ifFalse(Modifier))
    } else {
        this
    }
}

/**
 * Make a modifier visible or invisible based on a condition.
 * This keeps the element in the layout but makes it invisible.
 *
 * @param visible Whether the element should be visible
 */
fun Modifier.visible(visible: Boolean): Modifier {
    return if (visible) this else this.alpha(0f)
}

/**
 * Helper to handle focus states in Compose.
 * Useful for custom focus behaviors on text fields.
 *
 * @param focusRequester The focus requester to use
 * @param onFocusChanged Callback for when focus changes
 */
fun Modifier.handleFocus(
    focusRequester: FocusRequester, onFocusChanged: ((Boolean) -> Unit)? = null
): Modifier = this
    .focusRequester(focusRequester)
    .onFocusChanged {
        onFocusChanged?.invoke(it.isFocused)
    }

/**
 * Convert a Dp value to pixels as an Int.
 * Useful for interoperability with Canvas and other pixel-based APIs.
 *
 * Example:
 * ```
 * // When using a Canvas API that requires integer pixels
 * val pixelSize = size.dp.toPx()
 * canvas.drawCircle(centerX, centerY, pixelSize)
 * ```
 */
@Composable
fun Dp.toPx(): Int {
    return with(LocalDensity.current) { this@toPx.toPx().toInt() }
}

/**
 * Convert a Dp value to pixels as a Float.
 * Useful for interoperability with Canvas and other pixel-based APIs
 * that support floating point precision.
 *
 * Example:
 * ```
 * // When using a Canvas API that requires float pixels
 * val pixelSize = size.dp.toPxFloat()
 * canvas.drawCircle(centerX, centerY, pixelSize)
 * ```
 */
@Composable
fun Dp.toPxFloat(): Float {
    return with(LocalDensity.current) { this@toPxFloat.toPx() }
}

/**
 * Convert a TextUnit value to Dp.
 * Useful for aligning UI elements with text sizes for consistent spacing.
 *
 * Example:
 * ```
 * // Make icon size match text height
 * Icon(
 *     modifier = Modifier.size(MaterialTheme.typography.bodyLarge.fontSize.toDp()),
 *     imageVector = Icons.Default.Search,
 *     contentDescription = "Search"
 * )
 * ```
 */
@Composable
fun TextUnit.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

/**
 * Convert pixels (Int) to Dp.
 * Useful when working with pixel values from external sources.
 *
 * Example:
 * ```
 * // Convert a pixel measurement to Dp for Compose
 * val imageSize = 24
 * Image(
 *     modifier = Modifier.size(imageSize.toDp()),
 *     painter = painterResource(id = R.drawable.icon),
 *     contentDescription = null
 * )
 * ```
 */
@Composable
fun Int.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

/**
 * Convert pixels (Float) to Dp.
 * Useful when working with pixel values from external sources.
 */
@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}