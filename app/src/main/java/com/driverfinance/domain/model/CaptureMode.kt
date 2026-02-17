package com.driverfinance.domain.model

sealed interface CaptureMode {

    data class Discovery(
        val samplesCollected: Int,
        val samplesNeeded: Int
    ) : CaptureMode

    data object Parsing : CaptureMode
}
