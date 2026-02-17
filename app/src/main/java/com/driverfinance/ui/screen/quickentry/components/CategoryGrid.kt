package com.driverfinance.ui.screen.quickentry.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.CategoryUiModel
import com.driverfinance.ui.theme.Spacing

/**
 * 3-column grid of category buttons.
 * Each button: emoji (large) + name (small).
 * Min touch target 48dp (CONSTITUTION Section 2).
 */
@Composable
fun CategoryGrid(
    categories: List<CategoryUiModel>,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryItem(
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(Spacing.cardRadius),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = Spacing.lg, horizontal = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.emoji,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
