package com.driverfinance.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.driverfinance.data.local.dao.*
import com.driverfinance.data.local.entity.*

@Database(
    entities = [
        ScreenSnapshotEntity::class,
        ParsingPatternEntity::class,
        TripEntity::class,
        CapturedOrderEntity::class,
        HistoryTripEntity::class,
        HistoryDetailEntity::class,
        MatchedOrderEntity::class,
        DataReviewEntity::class,
        AiCorrectionLogEntity::class,
        QuickEntryCategoryEntity::class,
        QuickEntryEntity::class,
        DebtEntity::class,
        DebtPaymentEntity::class,
        FixedExpenseEntity::class,
        WorkScheduleEntity::class,
        AmbitiousModeEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun screenSnapshotDao(): ScreenSnapshotDao
    abstract fun parsingPatternDao(): ParsingPatternDao
    abstract fun tripDao(): TripDao
    abstract fun capturedOrderDao(): CapturedOrderDao

    abstract fun historyTripDao(): HistoryTripDao
    abstract fun historyDetailDao(): HistoryDetailDao

    abstract fun matchedOrderDao(): MatchedOrderDao
    abstract fun dataReviewDao(): DataReviewDao
    abstract fun aiCorrectionLogDao(): AiCorrectionLogDao

    abstract fun quickEntryCategoryDao(): QuickEntryCategoryDao
    abstract fun quickEntryDao(): QuickEntryDao

    abstract fun debtDao(): DebtDao
    abstract fun debtPaymentDao(): DebtPaymentDao

    abstract fun fixedExpenseDao(): FixedExpenseDao
    abstract fun workScheduleDao(): WorkScheduleDao
    abstract fun ambitiousModeDao(): AmbitiousModeDao
}
