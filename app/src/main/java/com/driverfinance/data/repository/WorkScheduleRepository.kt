package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.WorkScheduleDao
import com.driverfinance.data.local.entity.WorkScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduleRepository @Inject constructor(
    private val dao: WorkScheduleDao
) {

    fun getSchedulesBetween(startDate: String, endDate: String): Flow<List<WorkScheduleEntity>> =
        dao.getBetween(startDate, endDate)

    suspend fun getScheduleForDate(date: String): WorkScheduleEntity? =
        dao.getByDate(date)

    suspend fun upsert(schedule: WorkScheduleEntity) = dao.upsert(schedule)

    suspend fun getRemainingWorkDays(): Int {
        val today = LocalDate.now()
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())
        val tomorrow = today.plusDays(1)

        if (tomorrow.isAfter(endOfMonth)) return 0

        val tomorrowStr = tomorrow.toString()
        val endStr = endOfMonth.toString()

        val offDays = dao.getOffDayCountBetween(tomorrowStr, endStr)
        val totalDays = java.time.temporal.ChronoUnit.DAYS.between(tomorrow, endOfMonth).toInt() + 1

        return (totalDays - offDays).coerceAtLeast(0)
    }
}
