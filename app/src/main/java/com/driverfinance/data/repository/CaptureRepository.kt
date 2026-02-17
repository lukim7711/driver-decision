package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.CapturedOrderDao
import com.driverfinance.data.local.dao.HistoryDetailDao
import com.driverfinance.data.local.dao.HistoryTripDao
import com.driverfinance.data.local.dao.ParsingPatternDao
import com.driverfinance.data.local.dao.ScreenSnapshotDao
import com.driverfinance.data.local.dao.TripDao
import com.driverfinance.data.local.entity.CapturedOrderEntity
import com.driverfinance.data.local.entity.HistoryDetailEntity
import com.driverfinance.data.local.entity.HistoryTripEntity
import com.driverfinance.data.local.entity.ParsingPatternEntity
import com.driverfinance.data.local.entity.ScreenSnapshotEntity
import com.driverfinance.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaptureRepository @Inject constructor(
    private val snapshotDao: ScreenSnapshotDao,
    private val tripDao: TripDao,
    private val orderDao: CapturedOrderDao,
    private val patternDao: ParsingPatternDao,
    private val historyTripDao: HistoryTripDao,
    private val historyDetailDao: HistoryDetailDao
) {

    // --- Snapshots ---

    suspend fun saveSnapshot(snapshot: ScreenSnapshotEntity) = snapshotDao.insert(snapshot)

    suspend fun getUnprocessedSnapshots(): List<ScreenSnapshotEntity> = snapshotDao.getUnprocessed()

    suspend fun getSnapshotCountByType(type: String): Int = snapshotDao.getByTypeCount(type)

    suspend fun markSnapshotProcessed(id: String) = snapshotDao.markProcessed(id)

    // --- Trips (F001) ---

    suspend fun saveTrip(trip: TripEntity) = tripDao.insert(trip)

    suspend fun updateTrip(trip: TripEntity) = tripDao.update(trip)

    fun getTodayTrips(date: String): Flow<List<TripEntity>> = tripDao.getByDate(date)

    suspend fun getActiveTripWithinMinutes(date: String, cutoffTime: String): TripEntity? =
        tripDao.getActiveTripWithinMinutes(date, cutoffTime)

    // --- Captured Orders (F001) ---

    suspend fun saveCapturedOrder(order: CapturedOrderEntity) = orderDao.insert(order)

    suspend fun updateCapturedOrder(order: CapturedOrderEntity) = orderDao.update(order)

    suspend fun getOrderByOrderSn(orderSn: String): CapturedOrderEntity? = orderDao.getByOrderSn(orderSn)

    fun getOrdersByTripId(tripId: String): Flow<List<CapturedOrderEntity>> = orderDao.getByTripId(tripId)

    // --- History Trips (F002) ---

    suspend fun saveHistoryTrip(historyTrip: HistoryTripEntity) = historyTripDao.insert(historyTrip)

    suspend fun updateHistoryTrip(historyTrip: HistoryTripEntity) = historyTripDao.update(historyTrip)

    fun getTodayHistoryTrips(date: String): Flow<List<HistoryTripEntity>> =
        historyTripDao.getByDate(date)

    suspend fun findDuplicateHistoryTrip(
        date: String,
        time: String,
        serviceType: String,
        totalEarning: Int
    ): HistoryTripEntity? = historyTripDao.findDuplicate(date, time, serviceType, totalEarning)

    suspend fun getHistoryTripsWithoutLink(date: String): List<HistoryTripEntity> =
        historyTripDao.getUnlinkedByDate(date)

    // --- History Details (F002) ---

    suspend fun saveHistoryDetail(detail: HistoryDetailEntity) = historyDetailDao.insert(detail)

    suspend fun updateHistoryDetail(detail: HistoryDetailEntity) = historyDetailDao.update(detail)

    suspend fun getHistoryDetailByOrderSn(orderSn: String): HistoryDetailEntity? =
        historyDetailDao.getByOrderSn(orderSn)

    fun getDetailsByHistoryTripId(historyTripId: String): Flow<List<HistoryDetailEntity>> =
        historyDetailDao.getByHistoryTripId(historyTripId)

    suspend fun getDetailsCountByHistoryTripId(historyTripId: String): Int =
        historyDetailDao.getCountByHistoryTripId(historyTripId)

    // --- Patterns ---

    suspend fun getActivePatterns(): List<ParsingPatternEntity> = patternDao.getActive()

    suspend fun getActivePatternsByScreenType(type: String): List<ParsingPatternEntity> =
        patternDao.getActiveByScreenType(type)

    suspend fun savePattern(pattern: ParsingPatternEntity) = patternDao.insert(pattern)
}
