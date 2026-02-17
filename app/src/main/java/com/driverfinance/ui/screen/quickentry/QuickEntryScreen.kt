package com.driverfinance.ui.screen.quickentry

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.data.local.entity.QuickEntryCategoryEntity
import com.driverfinance.data.local.entity.QuickEntryPresetEntity
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEntryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuickEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.savedEvent.collect { confirmation ->
            snackbarHostState.showSnackbar(
                message = "\u2705 ${confirmation.categoryIcon} ${confirmation.categoryName} Rp${formatRupiah(confirmation.amount)}" +
                    (confirmation.note?.let { " \u2014 $it" } ?: "")
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Cepat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Check, contentDescription = "Selesai")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is QuickEntryUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(6) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { }
                    }
                }
            }
            is QuickEntryUiState.Success -> {
                Column(modifier = Modifier.padding(innerPadding)) {
                    PrimaryTabRow(selectedTabIndex = state.activeTab.ordinal) {
                        EntryTab.entries.forEach { tab ->
                            Tab(
                                selected = state.activeTab == tab,
                                onClick = { viewModel.switchTab(tab) },
                                text = { Text(tab.label) }
                            )
                        }
                    }

                    AnimatedContent(
                        targetState = state.phase,
                        label = "phase_transition"
                    ) { phase ->
                        when (phase) {
                            is QuickEntryPhase.CategoryGrid -> CategoryGridContent(
                                categories = state.categories,
                                todayCount = state.todayCount,
                                todayTotal = state.todayTotal,
                                tabLabel = state.activeTab.label.lowercase(),
                                onCategoryClick = { viewModel.selectCategory(it) }
                            )
                            is QuickEntryPhase.NominalSelect -> NominalSelectContent(
                                category = phase.category,
                                presets = phase.presets,
                                note = state.note,
                                isSaving = state.isSaving,
                                onPresetClick = { viewModel.selectPreset(it) },
                                onCustomClick = { viewModel.openNumpad() },
                                onNoteChange = { viewModel.updateNote(it) },
                                onBack = { viewModel.backToGrid() }
                            )
                            is QuickEntryPhase.NumpadCustom -> NumpadContent(
                                category = phase.category,
                                currentInput = phase.currentInput,
                                note = state.note,
                                isSaving = state.isSaving,
                                onDigit = { viewModel.numpadInput(it) },
                                onSubmit = { viewModel.submitNumpad() },
                                onNoteChange = { viewModel.updateNote(it) },
                                onBack = { viewModel.backToGrid() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryGridContent(
    categories: List<QuickEntryCategoryEntity>,
    todayCount: Int,
    todayTotal: Int,
    tabLabel: String,
    onCategoryClick: (QuickEntryCategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(Spacing.md)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.weight(1f)
        ) {
            items(categories, key = { it.id }) { category ->
                ElevatedCard(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable { onCategoryClick(category) },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(Spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = category.emoji,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(Spacing.md)) {
                Text(
                    text = "Hari ini: $todayCount $tabLabel",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Rp${formatRupiah(todayTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NominalSelectContent(
    category: QuickEntryCategoryEntity,
    presets: List<QuickEntryPresetEntity>,
    note: String,
    isSaving: Boolean,
    onPresetClick: (Int) -> Unit,
    onCustomClick: () -> Unit,
    onNoteChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.md)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = category.emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = "${category.name} \u2014 Berapa?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.fillMaxWidth()
        ) {
            presets.forEach { preset ->
                ElevatedButton(
                    onClick = { if (!isSaving) onPresetClick(preset.amount) },
                    enabled = !isSaving,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = formatRupiah(preset.amount),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))
        OutlinedButton(
            onClick = onCustomClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("\u270F\uFE0F Ketik nominal lain")
        }

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Catatan (opsional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("\u2190 Kembali ke kategori")
        }
    }
}

@Composable
private fun NumpadContent(
    category: QuickEntryCategoryEntity,
    currentInput: String,
    note: String,
    isSaving: Boolean,
    onDigit: (String) -> Unit,
    onSubmit: () -> Unit,
    onNoteChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayAmount = currentInput.toIntOrNull() ?: 0
    val isValid = displayAmount > 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = category.emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = "${category.name} \u2014 Berapa?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (displayAmount > 0) "Rp${formatRupiah(displayAmount)}" else "Rp 0",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        val numpadKeys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("000", "0", "DEL")
        )

        numpadKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                row.forEach { key ->
                    val isSpecial = key == "DEL" || key == "000"
                    if (isSpecial) {
                        OutlinedButton(
                            onClick = { onDigit(key) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (key == "DEL") "\u232B" else key,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { onDigit(key) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
        }

        Spacer(modifier = Modifier.height(Spacing.md))
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Catatan (opsional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.md))
        Button(
            onClick = onSubmit,
            enabled = isValid && !isSaving,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simpan", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(Spacing.xs))
        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("\u2190 Kembali ke pilihan nominal")
        }

        Spacer(modifier = Modifier.height(Spacing.md))
    }
}

private fun formatRupiah(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
