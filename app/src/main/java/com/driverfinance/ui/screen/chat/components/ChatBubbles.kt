package com.driverfinance.ui.screen.chat.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.driverfinance.ui.theme.Spacing

/**
 * User message bubble — right-aligned, primary color.
 * F008 spec mockup B: user messages on the right.
 */
@Composable
fun UserBubble(text: String) {
    val maxWidth = (LocalConfiguration.current.screenWidthDp * 0.75f).dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 4.dp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.widthIn(max = maxWidth)
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(Spacing.md),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/**
 * AI message bubble — left-aligned, surfaceVariant.
 * Supports error state with retry button.
 * F008 spec: angka bold (**Rp150.000**) — rendered via annotated string in future.
 * For now: plain text display.
 */
@Composable
fun AiBubble(
    text: String,
    isError: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    val maxWidth = (LocalConfiguration.current.screenWidthDp * 0.85f).dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.widthIn(max = maxWidth)
        ) {
            Column(modifier = Modifier.padding(Spacing.md)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isError) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                if (isError && onRetry != null) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    TextButton(onClick = onRetry) {
                        Text(
                            text = "Coba Lagi",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

/**
 * Loading bubble — animated dots.
 * F008 spec mockup E: "\uD83E\uDD16 \u25CF\u25CF\u25CF  Sedang menganalisa data..."
 */
@Composable
fun LoadingBubble() {
    val maxWidth = (LocalConfiguration.current.screenWidthDp * 0.6f).dp
    val transition = rememberInfiniteTransition(label = "loadingDots")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = maxWidth)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.md)
            ) {
                Text(
                    text = "\uD83E\uDD16 \u25CF\u25CF\u25CF",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.alpha(alpha)
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = "Sedang menganalisa data...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
