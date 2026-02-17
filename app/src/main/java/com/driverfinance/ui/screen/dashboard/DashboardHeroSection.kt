package com.driverfinance.ui.screen.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import com.driverfinance.ui.theme.Spacing

/**
 * Hero number section for F005 Dashboard.
 * Reads daily_target_cache from F007.
 * Tap → navigate to Target Detail screen.
 */
@Composable
fun DashboardHeroSection(
    cache: DailyTargetCacheEntity?,
    todayProfit: Int,
    onTapDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTapDetail() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            if (cache == null) {
                // No target configured
                Text(
                    text = "Belum ada kewajiban bulan ini",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Tambahkan hutang atau biaya tetap",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                return@Card
            }

            when (cache.status) {
                "NO_OBLIGATION" -> {
                    Text(
                        text = "Belum ada kewajiban bulan ini",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tambahkan hutang atau biaya tetap",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                "ACHIEVED" -> {
                    Text(
                        text = "\uD83C\uDF89 Kewajiban bulan ini tercukupi!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sisa hari ini = bonus",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    LinearProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                    )
                }
                else -> {
                    val remaining = (cache.targetAmount - todayProfit).coerceAtLeast(0)
                    val progress = if (cache.targetAmount > 0) {
                        (todayProfit.toFloat() / cache.targetAmount).coerceIn(0f, 1.5f)
                    } else 1f
                    val progressPercent = (progress * 100).toInt()

                    if (todayProfit == 0) {
                        Text(
                            text = "Target hari ini: Rp${formatHeroRupiah(cache.targetAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    } else if (remaining > 0) {
                        Text(
                            text = "Kurang Rp${formatHeroRupiah(remaining)} lagi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        val surplus = todayProfit - cache.targetAmount
                        Text(
                            text = "\u2705 Target tercapai! Lebih Rp${formatHeroRupiah(surplus)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))
                    LinearProgressIndicator(
                        progress = { progress.coerceAtMost(1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${progressPercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Target: Rp${formatHeroRupiah(cache.targetAmount)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

private fun formatHeroRupiah(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
