package com.driverfinance.ui.screen.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.driverfinance.ui.navigation.Screen
import com.driverfinance.ui.theme.Spacing

private data class MoreMenuItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val route: String?
)

private val menuItems = listOf(
    MoreMenuItem("\uD83D\uDCB3", "Manajemen Hutang", "Kelola hutang & cicilan", null),
    MoreMenuItem("\uD83D\uDCC5", "Kewajiban & Jadwal", "Biaya tetap & jadwal kerja", null),
    MoreMenuItem("\uD83C\uDFAF", "Detail Target", "Lihat perhitungan target harian", null),
    MoreMenuItem("\uD83D\uDCCB", "Data Perlu Dicek", "Review data hasil capture", Screen.DataReview.route),
    MoreMenuItem("\uD83D\uDCF7", "Capture Manager", "Pengaturan auto capture", Screen.CaptureManager.route)
)

@Composable
fun MoreScreen(
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = Spacing.md)
    ) {
        Text(
            text = "Lainnya",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)
        )

        menuItems.forEach { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                supportingContent = { Text(item.subtitle) },
                leadingContent = {
                    Text(
                        text = item.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { item.route?.let { onNavigate(it) } }
            )
            HorizontalDivider()
        }
    }
}
