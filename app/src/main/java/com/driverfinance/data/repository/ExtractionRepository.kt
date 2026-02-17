package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.AiCorrectionLogDao
import com.driverfinance.data.local.dao.DataReviewDao
import com.driverfinance.data.local.dao.MatchedOrderDao
import com.driverfinance.data.local.entity.AiCorrectionLogEntity
import com.driverfinance.data.local.entity.DataReviewEntity
import com.driverfinance.data.local.entity.MatchedOrderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtractionRepository @Inject constructor(
    private val matchedOrderDao: MatchedOrderDao,
    private val dataReviewDao: DataReviewDao,
    private val aiCorrectionLogDao: AiCorrectionLogDao
) {

    // --- Matched Orders ---

    suspend fun saveMatchedOrder(entity: MatchedOrderEntity) = matchedOrderDao.insert(entity)

    suspend fun updateMatchedOrder(entity: MatchedOrderEntity) = matchedOrderDao.update(entity)

    suspend fun getMatchedOrdersByStatus(status: String): List<MatchedOrderEntity> =
        matchedOrderDao.getByStatusSync(status)

    suspend fun getMatchedOrderByCapturedOrderId(id: String): MatchedOrderEntity? =
        matchedOrderDao.getByCapturedOrderId(id)

    suspend fun getMatchedOrderByHistoryDetailId(id: String): MatchedOrderEntity? =
        matchedOrderDao.getByHistoryDetailId(id)

    // --- Data Reviews ---

    suspend fun saveDataReview(entity: DataReviewEntity) = dataReviewDao.insert(entity)

    suspend fun updateDataReview(entity: DataReviewEntity) = dataReviewDao.update(entity)

    suspend fun getDataReviewById(id: String): DataReviewEntity? = dataReviewDao.getById(id)

    fun getPendingReviews(): Flow<List<DataReviewEntity>> = dataReviewDao.getPending()

    suspend fun getTodayPendingCount(date: String): Int = dataReviewDao.getTodayPendingCount(date)

    // --- AI Corrections Log ---

    suspend fun saveAiCorrectionLog(entity: AiCorrectionLogEntity) = aiCorrectionLogDao.insert(entity)
}
