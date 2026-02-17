package com.driverfinance.ui.screen.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.DashboardData
import com.driverfinance.ui.theme.Gray200
import com.driverfinance.ui.theme.ProgressYellow
import com.driverfinance.ui.theme.Spacing
import com.driverfinance.ui.theme.TargetMetGreen
import com.driverfinance.util.toRupiah

/**
 * Hero section with target progress.
 *
 * 3 states (F005 spec Section 6.3 #2):
 * - Target available + not met → "Kurang RpXXX lagi" + yellow progress
 * - Target available + met → "\uD83C\uDF89 Target Tercapai!" + green 100%
 * - No target → Profit Bersih as hero + hint to set target
 */
@Composable
fun HeroTargetSection(
    data: DashboardData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (data.isTargetAvailable) {
            if (data.isTargetMet) {
                TargetMetContent(data = data)
            } else {
                TargetInProgressContent(data = data)
            }
        } else {
            NoTargetContent(data = data)
        }
    }
}

@Composable
private fun TargetMetContent(data: DashboardData) {
    Text(
        text = "\uD83C\uDF89 Target Tercapai!",
        style = MaterialTheme.typography.headlineLarge,
        color = TargetMetGreen
    )
    Spacer(modifier = Modifier.height(Spacing.xs))
    Text(
        text = "Lebih ${data.lebihTarget.toRupiah()} dari target",
        style = MaterialTheme.typography.bodyLarge,
        color = TargetMetGreen
    )
    Spacer(modifier = Modifier.height(Spacing.md))
    TargetProgressBar(progress = 1f, isTargetMet = true)
    Spacer(modifier = Modifier.height(Spacing.xs))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Target hari ini: ${data.dailyTarget?.toRupiah().orEmpty()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "100%",
            style = MaterialTheme.typography.labelMedium,
            color = TargetMetGreen
        )
    }
}

@Composable
private fun TargetInProgressContent(data: DashboardData) {
    Text(
        text = "Kurang ${data.sisaTarget.toRupiah()} lagi",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(Spacing.md))
    TargetProgressBar(progress = data.progressPercent, isTargetMet = false)
    Spacer(modifier = Modifier.height(Spacing.xs))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Target hari ini: ${data.dailyTarget?.toRupiah().orEmpty()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${data.progressPercentInt}%",
            style = MaterialTheme.typography.labelMedium,
            color = ProgressYellow
        )
    }
}

@Composable
private fun NoTargetContent(data: DashboardData) {
    Text(
        text = "Profit Hari Ini",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(Spacing.xs))
    Text(
        text = data.profitBersih.toRupiah(),
        style = MaterialTheme.typography.displayLarge,
        color = if (data.profitBersih >= 0) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.error
        }
    )
    Spacer(modifier = Modifier.height(Spacing.sm))
    Text(
        text = "Set target harian di Settings",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Custom rounded progress bar.
 * Thicker than default M3 LinearProgressIndicator for glanceability.
 */
@Composable
private fun TargetProgressBar(
    progress: Float,
    isTargetMet: Boolean,
    modifier: Modifier = Modifier
) {
    val barColor = if (isTargetMet) TargetMetGreen else ProgressYellow
    val barShape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(barShape)
            .background(Gray200)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
                .clip(barShape)
                .background(barColor)
        )
    }
}
