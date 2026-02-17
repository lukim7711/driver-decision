package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.repository.CaptureRepository
import com.driverfinance.domain.model.CaptureMode
import javax.inject.Inject

class GetCaptureModeUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Determines current capture mode based on parsing pattern accuracy.
     * Discovery Mode: collecting samples, patterns not yet accurate enough.
     * Parsing Mode: patterns exist with accuracy >= 85% for ORDER_DETAIL screens.
     */
    suspend operator fun invoke(): CaptureMode {
        val patterns = repository.getActivePatternsByScreenType(SCREEN_TYPE_ORDER_DETAIL)
        val hasAccuratePatterns = patterns.any { it.accuracy >= ACCURACY_THRESHOLD }

        if (hasAccuratePatterns) {
            return CaptureMode.Parsing
        }

        val samplesCollected = repository.getSnapshotCountByType(SCREEN_TYPE_ORDER_DETAIL)
        return CaptureMode.Discovery(
            samplesCollected = samplesCollected,
            samplesNeeded = MINIMUM_SAMPLES
        )
    }

    companion object {
        const val SCREEN_TYPE_ORDER_DETAIL = "ORDER_DETAIL"
        const val MINIMUM_SAMPLES = 10
        const val ACCURACY_THRESHOLD = 0.85f
    }
}
