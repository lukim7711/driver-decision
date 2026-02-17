package com.driverfinance.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.driverfinance.data.local.AppDatabase
import com.driverfinance.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "driver_finance.db"
        )
            .addCallback(SeedDatabaseCallback())
            .build()
    }

    /**
     * Pre-load quick_entry_categories + quick_entry_presets (F004 spec §6.2) dan
     * initialize ambitious_mode single row (F009 spec)
     * saat database pertama kali dibuat.
     */
    private class SeedDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val now = java.time.OffsetDateTime.now(java.time.ZoneId.of("Asia/Jakarta"))
                .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            // Generate IDs upfront so we can reference them for preset seeding
            val categories = listOf(
                CategorySeed("Bensin", "\u26FD", "EXPENSE", 0, 1, UUID.randomUUID().toString()),
                CategorySeed("Makan", "\uD83C\uDF54", "EXPENSE", 0, 2, UUID.randomUUID().toString()),
                CategorySeed("Rokok", "\uD83D\uDEAC", "EXPENSE", 0, 3, UUID.randomUUID().toString()),
                CategorySeed("Parkir", "\uD83C\uDD7F\uFE0F", "EXPENSE", 0, 4, UUID.randomUUID().toString()),
                CategorySeed("Pulsa", "\uD83D\uDCF1", "EXPENSE", 0, 5, UUID.randomUUID().toString()),
                CategorySeed("Lainnya", "\uD83D\uDCE6", "EXPENSE", 0, 6, UUID.randomUUID().toString()),
                CategorySeed("Tip Cash", "\uD83D\uDCB5", "INCOME", 0, 7, UUID.randomUUID().toString()),
                CategorySeed("Transfer Masuk", "\uD83C\uDFE6", "INCOME", 0, 8, UUID.randomUUID().toString()),
                CategorySeed("Kerja Sampingan", "\uD83D\uDD28", "INCOME", 0, 9, UUID.randomUUID().toString()),
                CategorySeed("Lainnya", "\uD83D\uDCB0", "INCOME", 0, 10, UUID.randomUUID().toString()),
                CategorySeed("Cicilan Hutang", "\uD83C\uDFE7", "EXPENSE", 1, 11, UUID.randomUUID().toString()),
                CategorySeed("Denda Hutang", "\u26A0\uFE0F", "EXPENSE", 1, 12, UUID.randomUUID().toString())
            )

            // Insert categories
            categories.forEach { cat ->
                db.execSQL(
                    """INSERT INTO quick_entry_categories
                       (id, name, emoji, type, is_system, sort_order, is_active, created_at, updated_at)
                       VALUES ('${cat.id}', '${cat.name}', '${cat.emoji}', '${cat.type}',
                               ${cat.isSystem}, ${cat.sortOrder}, 1, '$now', '$now')"""
                )
            }

            // Seed presets for non-system categories (F004 spec §6.2)
            val defaultPresets = listOf(5000, 10000, 15000, 20000, 25000, 30000, 50000)
            val incomePresetMap = mapOf(
                "Transfer Masuk" to listOf(50000, 100000, 200000, 500000, 1000000),
                "Kerja Sampingan" to listOf(50000, 100000, 150000, 200000, 300000, 500000)
            )
            val incomeLainnyaPresets = listOf(5000, 10000, 20000, 50000, 100000)

            categories.filter { it.isSystem == 0 }.forEach { cat ->
                val presets = when {
                    cat.type == "INCOME" && cat.name == "Lainnya" -> incomeLainnyaPresets
                    cat.type == "INCOME" -> incomePresetMap[cat.name] ?: defaultPresets
                    else -> defaultPresets
                }
                presets.forEachIndexed { index, amount ->
                    db.execSQL(
                        """INSERT INTO quick_entry_presets
                           (id, category_id, amount, label, sort_order, created_at)
                           VALUES ('${UUID.randomUUID()}', '${cat.id}', $amount, NULL, $index, '$now')"""
                    )
                }
            }

            // Ambitious mode single row (F009)
            db.execSQL(
                """INSERT INTO ambitious_mode
                   (id, is_active, target_months, activated_at, deactivated_reason, updated_at)
                   VALUES ('${UUID.randomUUID()}', 0, 0, NULL, NULL, '$now')"""
            )
        }
    }

    private data class CategorySeed(
        val name: String,
        val emoji: String,
        val type: String,
        val isSystem: Int,
        val sortOrder: Int,
        val id: String
    )

    @Provides fun provideScreenSnapshotDao(db: AppDatabase): ScreenSnapshotDao = db.screenSnapshotDao()
    @Provides fun provideParsingPatternDao(db: AppDatabase): ParsingPatternDao = db.parsingPatternDao()
    @Provides fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
    @Provides fun provideCapturedOrderDao(db: AppDatabase): CapturedOrderDao = db.capturedOrderDao()
    @Provides fun provideHistoryTripDao(db: AppDatabase): HistoryTripDao = db.historyTripDao()
    @Provides fun provideHistoryDetailDao(db: AppDatabase): HistoryDetailDao = db.historyDetailDao()
    @Provides fun provideMatchedOrderDao(db: AppDatabase): MatchedOrderDao = db.matchedOrderDao()
    @Provides fun provideDataReviewDao(db: AppDatabase): DataReviewDao = db.dataReviewDao()
    @Provides fun provideAiCorrectionLogDao(db: AppDatabase): AiCorrectionLogDao = db.aiCorrectionLogDao()
    @Provides fun provideQuickEntryCategoryDao(db: AppDatabase): QuickEntryCategoryDao = db.quickEntryCategoryDao()
    @Provides fun provideQuickEntryDao(db: AppDatabase): QuickEntryDao = db.quickEntryDao()
    @Provides fun provideQuickEntryPresetDao(db: AppDatabase): QuickEntryPresetDao = db.quickEntryPresetDao()
    @Provides fun provideDebtDao(db: AppDatabase): DebtDao = db.debtDao()
    @Provides fun provideDebtPaymentDao(db: AppDatabase): DebtPaymentDao = db.debtPaymentDao()
    @Provides fun provideFixedExpenseDao(db: AppDatabase): FixedExpenseDao = db.fixedExpenseDao()
    @Provides fun provideWorkScheduleDao(db: AppDatabase): WorkScheduleDao = db.workScheduleDao()
    @Provides fun provideAmbitiousModeDao(db: AppDatabase): AmbitiousModeDao = db.ambitiousModeDao()
    @Provides fun provideDailyTargetCacheDao(db: AppDatabase): DailyTargetCacheDao = db.dailyTargetCacheDao()
}
