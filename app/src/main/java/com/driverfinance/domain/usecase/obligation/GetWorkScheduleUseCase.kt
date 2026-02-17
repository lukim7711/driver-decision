package com.driverfinance.domain.usecase.obligation

import com.driverfinance.data.local.entity.WorkScheduleEntity
import com.driverfinance.data.repository.WorkScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class GetWorkScheduleUseCase @Inject constructor(
    private val repository: WorkScheduleRepository
) {

    operator fun invoke(): Flow<WorkScheduleData> {
        val today = LocalDate.now()
        val startOfThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endOfNextWeek = startOfThisWeek.plusDays(13)

        return repository.getSchedulesBetween(
            startDate = startOfThisWeek.toString(),
            endDate = endOfNextWeek.toString()
        ).map { schedules ->
            val scheduleMap = schedules.associateBy { it.date }

            val thisWeek = buildWeek(startOfThisWeek, scheduleMap)
            val nextWeek = buildWeek(startOfThisWeek.plusDays(7), scheduleMap)

            val workingThisWeek = thisWeek.count { it.isWorking }
            val workingNextWeek = nextWeek.count { it.isWorking }

            WorkScheduleData(
                thisWeek = thisWeek,
                nextWeek = nextWeek,
                thisWeekStart = startOfThisWeek.toString(),
                thisWeekEnd = startOfThisWeek.plusDays(6).toString(),
                nextWeekStart = startOfThisWeek.plusDays(7).toString(),
                nextWeekEnd = endOfNextWeek.toString(),
                workingDaysThisWeek = workingThisWeek,
                offDaysThisWeek = 7 - workingThisWeek,
                workingDaysNextWeek = workingNextWeek,
                offDaysNextWeek = 7 - workingNextWeek
            )
        }
    }

    private fun buildWeek(
        weekStart: LocalDate,
        scheduleMap: Map<String, WorkScheduleEntity>
    ): List<DaySchedule> {
        return (0L until 7L).map { offset ->
            val date = weekStart.plusDays(offset)
            val dateStr = date.toString()
            val schedule = scheduleMap[dateStr]
            DaySchedule(
                date = dateStr,
                dayOfWeek = date.dayOfWeek.name.take(3),
                isWorking = schedule?.isWorking != 0,
                isPast = date.isBefore(LocalDate.now()),
                isToday = date.isEqual(LocalDate.now())
            )
        }
    }
}

data class WorkScheduleData(
    val thisWeek: List<DaySchedule>,
    val nextWeek: List<DaySchedule>,
    val thisWeekStart: String,
    val thisWeekEnd: String,
    val nextWeekStart: String,
    val nextWeekEnd: String,
    val workingDaysThisWeek: Int,
    val offDaysThisWeek: Int,
    val workingDaysNextWeek: Int,
    val offDaysNextWeek: Int
)

data class DaySchedule(
    val date: String,
    val dayOfWeek: String,
    val isWorking: Boolean,
    val isPast: Boolean,
    val isToday: Boolean
)
