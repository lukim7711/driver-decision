package com.driverfinance.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Centralized spacing values used across all composables.
 * CONSTITUTION Section 2: semua spacing di theme/, tidak boleh hardcode.
 */
object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp

    // Touch target minimum (CONSTITUTION Section 2: minimum 48dp)
    val minTouchTarget = 48.dp

    // Tab bar
    val tabBarHeight = 64.dp
    val tabBarElevatedOffset = 8.dp

    // Card
    val cardPadding = 16.dp
    val cardRadius = 12.dp
    val cardElevation = 2.dp

    // Screen
    val screenPadding = 16.dp
    val screenTopPadding = 24.dp
}
