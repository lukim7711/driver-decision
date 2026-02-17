package com.driverfinance.ui.screen.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.ShiftState
import com.driverfinance.domain.model.ShiftStatus
import com.driverfinance.ui.theme.Spacing
import java.time.format.DateTimeFormatter

private val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Shift control header.
 * Shows shift status + start/stop button.
 * Central to the driver's daily workflow.
 */
@Composable
fun ShiftControlSection(
    shift: ShiftState,
    onStartShift: () -> Unit,
    onEndShift: () -> Unit,
    onNewShift: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when (shift.status) {
        ShiftStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
        ShiftStatus.ENDED -> MaterialTheme.colorScheme.surfaceVariant
        ShiftStatus.NOT_STARTED -> MaterialTheme.colorScheme.surface
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        tonalElevation = if (shift.status == ShiftStatus.NOT_STARTED) 2.dp else 0.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status text
            Text(
                text = shift.statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = when (shift.status) {
                    ShiftStatus.ACTIVE -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            // Time range
            if (shift.startTime != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                val timeRange = buildString {
                    append(shift.startTime.format(timeFmt))
                    shift.endTime?.let {
                        append(" \u2013 ")
                        append(it.format(timeFmt))
                    }
                }
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Action button
            when (shift.status) {
                ShiftStatus.NOT_STARTED -> {
                    Button(
                        onClick = onStartShift,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = shift.buttonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                ShiftStatus.ACTIVE -> {
                    OutlinedButton(
                        onClick = onEndShift,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = shift.buttonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                ShiftStatus.ENDED -> {
                    OutlinedButton(
                        onClick = onNewShift,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = shift.buttonText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Confirmation dialog for ending shift.
 */
@Composable
fun EndShiftConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Akhiri Shift?") },
        text = {
            Text(
                text = "Auto capture akan dihentikan. " +
                    "Pastikan semua order sudah selesai.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Akhiri Shift")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
