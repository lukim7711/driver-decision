package com.driverfinance.domain.usecase.obligation

import com.driverfinance.data.local.entity.FixedExpenseEntity
import com.driverfinance.data.repository.FixedExpenseRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class ManageFixedExpenseUseCase @Inject constructor(
    private val repository: FixedExpenseRepository
) {

    suspend fun create(input: FixedExpenseInput): String {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val entity = FixedExpenseEntity(
            id = UUID.randomUUID().toString(),
            emoji = input.emoji,
            name = input.name,
            amount = input.amount,
            note = input.note?.takeIf { it.isNotBlank() },
            isActive = 1,
            createdAt = now,
            updatedAt = now
        )
        repository.insert(entity)
        return entity.id
    }

    suspend fun update(id: String, input: FixedExpenseInput) {
        val existing = repository.getById(id) ?: return
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        repository.update(
            existing.copy(
                emoji = input.emoji,
                name = input.name,
                amount = input.amount,
                note = input.note?.takeIf { it.isNotBlank() },
                updatedAt = now
            )
        )
    }

    suspend fun delete(id: String) {
        val existing = repository.getById(id) ?: return
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        repository.update(existing.copy(isActive = 0, updatedAt = now))
    }

    suspend fun seedFromTemplate(selectedTemplates: List<FixedExpenseTemplate>) {
        selectedTemplates.forEach { template ->
            create(
                FixedExpenseInput(
                    emoji = template.emoji,
                    name = template.name,
                    amount = template.defaultAmount,
                    note = null
                )
            )
        }
    }

    suspend fun shouldShowTemplate(): Boolean = !repository.hasAnyRecords()

    companion object {
        val DEFAULT_TEMPLATES = listOf(
            FixedExpenseTemplate("\uD83D\uDCF1", "Pulsa / Paket Data", 50_000),
            FixedExpenseTemplate("\u26A1", "Listrik", 200_000),
            FixedExpenseTemplate("\uD83D\uDCA7", "Air (PDAM)", 100_000),
            FixedExpenseTemplate("\uD83C\uDFE0", "Kontrakan / Kos", 500_000),
            FixedExpenseTemplate("\uD83D\uDCFA", "Internet / WiFi", 150_000),
            FixedExpenseTemplate("\uD83C\uDF93", "Uang Sekolah Anak", 300_000),
            FixedExpenseTemplate("\uD83D\uDD27", "Servis Kendaraan Rutin", 100_000)
        )
    }
}

data class FixedExpenseInput(
    val emoji: String,
    val name: String,
    val amount: Int,
    val note: String?
)

data class FixedExpenseTemplate(
    val emoji: String,
    val name: String,
    val defaultAmount: Int
)
