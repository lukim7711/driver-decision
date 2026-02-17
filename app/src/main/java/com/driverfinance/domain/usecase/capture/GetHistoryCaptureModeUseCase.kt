package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.repository.CaptureRepository
import com.driverfinance.domain.model.CaptureMode
import javax.inject.Inject

class GetHistoryCaptureModeUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Determines capture mode for history screens (HISTORY_LIST + HISTORY_DETAIL).
     * Requires accurate patterns for both screen types.
     */
    suspend operator fun invoke(): CaptureMode {
        val listPatterns = repository.getActivePatternsByScreenType(SCREEN_HISTORY_LIST)
        val detailPatterns = repository.getActivePatternsByScreenType(SCREEN_HISTORY_DETAIL)

        val listReady = listPatterns.any { it.accuracy >= ACCURACY_THRESHOLD }
        val detailReady = detailPatterns.any { it.accuracy >= ACCURACY_THRESHOLD }

        if (listReady && detailReady) {
            return CaptureMode.Parsing
        }

        val listSamples = repository.getSnapshotCountByType(SCREEN_HISTORY_LIST)
        val detailSamples = repository.getSnapshotCountByType(SCREEN_HISTORY_DETAIL)
        val totalSamples = listSamples + detailSamples
        val totalNeeded = MINIMUM_SAMPLES * 2

        return CaptureMode.Discovery(
            samplesCollected = totalSamples,
            samplesNeeded = totalNeeded
        )
    }

    companion object {
        const val SCREEN_HISTORY_LIST = "HISTORY_LIST"
        const val SCREEN_HISTORY_DETAIL = "HISTORY_DETAIL"
        const val MINIMUM_SAMPLES = 10
        const val ACCURACY_THRESHOLD = 0.85f
    }
}
