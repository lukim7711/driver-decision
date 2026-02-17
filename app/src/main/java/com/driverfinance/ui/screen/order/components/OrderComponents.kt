package com.driverfinance.ui.screen.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.CaptureState
import com.driverfinance.domain.model.CaptureStatus
import com.driverfinance.domain.model.OrderItem
import com.driverfinance.domain.model.TripItem
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private val rupiahFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
private val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

// ==================== CAPTURE STATUS BANNER ====================

/**
 * Banner showing Auto Capture service status.
 * F001 spec: notification bar status.
 */
@Composable
fun CaptureStatusBanner(
    capture: CaptureState,
    modifier: Modifier = Modifier
) {
    val bgColor = when (capture.status) {
        CaptureStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
        CaptureStatus.LEARNING -> MaterialTheme.colorScheme.tertiaryContainer
        CaptureStatus.INACTIVE -> MaterialTheme.colorScheme.errorContainer
        CaptureStatus.PERMISSION_NEEDED -> MaterialTheme.colorScheme.errorContainer
    }
    val textColor = when (capture.status) {
        CaptureStatus.ACTIVE -> MaterialTheme.colorScheme.onPrimaryContainer
        CaptureStatus.LEARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        CaptureStatus.INACTIVE -> MaterialTheme.colorScheme.onErrorContainer
        CaptureStatus.PERMISSION_NEEDED -> MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.md,
                vertical = Spacing.sm
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = capture.statusEmoji,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = capture.statusText,
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor
                )
                if (capture.todayOrderCount > 0) {
                    Text(
                        text = "\uD83D\uDCE6 ${capture.todayOrderCount} order \u2022 \uD83D\uDE97 ${capture.todayTripCount} trip",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
        }
    }
}

// ==================== DAILY SUMMARY ====================

/**
 * Daily summary row showing trips, orders, and earnings.
 */
@Composable
fun DailySummaryRow(
    tripCount: Int,
    orderCount: Int,
    totalEarning: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryPill(
            emoji = "\uD83D\uDE97",
            value = "$tripCount",
            label = "trip"
        )
        SummaryPill(
            emoji = "\uD83D\uDCE6",
            value = "$orderCount",
            label = "order"
        )
        SummaryPill(
            emoji = "\uD83D\uDCB0",
            value = "Rp${rupiahFmt.format(totalEarning)}",
            label = "pendapatan"
        )
    }
}

@Composable
private fun SummaryPill(
    emoji: String,
    value: String,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Spacing.md,
                vertical = Spacing.sm
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== TRIP CARD ====================

/**
 * Trip card showing trip info + order count + capture status.
 * F001 spec mockup D: trip list in Capture Manager.
 */
@Composable
fun TripCard(
    trip: TripItem,
    onTripClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTripClick)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            // Header: trip number + service type + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trip.serviceType.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "Trip #${trip.tripNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    trip.tripCode?.let {
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = trip.startTime.format(timeFmt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Service type + order count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${trip.serviceType.displayName} \u2022 ${trip.orderCount} pesanan" +
                        if (trip.isCombined) " \u2022 Gabungan" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                trip.earningText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Bonus
            trip.bonusPoints?.let { points ->
                Text(
                    text = "\u2B50 $points Poin Bonus",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Capture status
            Text(
                text = trip.captureStatusText,
                style = MaterialTheme.typography.labelSmall,
                color = if (trip.allCaptured) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

// ==================== TRIP DETAIL SHEET ====================

/**
 * Bottom-sheet-style detail for a trip.
 * F001 spec mockup E: per-order detail within a trip.
 */
@Composable
fun TripDetailSheet(
    trip: TripItem,
    onDismiss: () -> Unit
) {
    // Using full-screen overlay for simplicity
    // TODO: Replace with ModalBottomSheet when M3 stable
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${trip.serviceType.emoji} Trip #${trip.tripNumber} \u2014 ${trip.startTime.format(timeFmt)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${trip.serviceType.displayName} \u2022 ${trip.orderCount} pesanan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                trip.earningText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Order list
            trip.orders.forEachIndexed { index, order ->
                OrderDetailCard(order = order, index = index + 1)
                if (index < trip.orders.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = Spacing.sm)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailCard(
    order: OrderItem,
    index: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Order SN
        Text(
            text = "\uD83D\uDCE6 Pesanan $index \u2014 #${order.orderSn ?: "N/A"}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(Spacing.xs))

        // Pickup
        order.pickupAddress?.let { addr ->
            AddressRow(
                label = "Pickup",
                address = addr,
                area = order.pickupArea
            )
        }

        // Delivery
        order.deliveryAddress?.let { addr ->
            AddressRow(
                label = "Antar",
                address = addr,
                area = order.deliveryArea
            )
        }

        // Payment + weight
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val payText = buildString {
                order.paymentMethod?.let { append("\uD83D\uDCB0 ${it.displayName}") }
                order.parcelWeight?.let {
                    if (isNotEmpty()) append(" \u2022 ")
                    append(it)
                }
            }
            if (payText.isNotEmpty()) {
                Text(
                    text = payText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            order.paymentAmount?.let { amount ->
                Text(
                    text = "Rp${rupiahFmt.format(amount)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AddressRow(
    label: String,
    address: String,
    area: String?
) {
    Row(modifier = Modifier.padding(vertical = 1.dp)) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(48.dp)
        )
        Column {
            Text(
                text = address,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==================== HISTORY REMINDER ====================

/**
 * Reminder card for trips missing rincian (F002).
 */
@Composable
fun HistoryReminderCard(
    tripsWithoutDetail: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDCCB",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Column {
                Text(
                    text = "$tripsWithoutDetail trip belum ada rincian",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Buka Riwayat di Shopee untuk data lengkap",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

// ==================== EMPTY STATE ====================

/**
 * Empty state for Order tab.
 */
@Composable
fun EmptyOrderState(
    shiftActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\uD83D\uDCE6",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = if (shiftActive) "Menunggu order masuk..."
            else "Belum ada order hari ini",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = if (shiftActive) "Order akan otomatis ter-capture saat masuk"
            else "Tap \u201CMulai Narik\u201D untuk memulai shift",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
