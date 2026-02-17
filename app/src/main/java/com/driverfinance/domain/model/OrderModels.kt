package com.driverfinance.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Domain models for Order tab (F001 + F002).
 * Covers shift management, trip grouping, and order details.
 */

// ==================== SHIFT ====================

/** Current shift state — driver mulai/akhiri narik. */
enum class ShiftStatus {
    NOT_STARTED,
    ACTIVE,
    ENDED
}

data class ShiftState(
    val status: ShiftStatus = ShiftStatus.NOT_STARTED,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val duration: String = "0j 0m",
    val date: LocalDate = LocalDate.now()
) {
    val isActive: Boolean get() = status == ShiftStatus.ACTIVE
    val hasEnded: Boolean get() = status == ShiftStatus.ENDED

    val statusText: String
        get() = when (status) {
            ShiftStatus.NOT_STARTED -> "Belum mulai narik"
            ShiftStatus.ACTIVE -> "Sedang narik \u2022 $duration"
            ShiftStatus.ENDED -> "Selesai narik \u2022 $duration"
        }

    val buttonText: String
        get() = when (status) {
            ShiftStatus.NOT_STARTED -> "\uD83D\uDE97 Mulai Narik"
            ShiftStatus.ACTIVE -> "\uD83D\uDED1 Akhiri Shift"
            ShiftStatus.ENDED -> "\uD83D\uDD04 Mulai Shift Baru"
        }
}

// ==================== CAPTURE STATUS ====================

/** Accessibility Service capture status. */
enum class CaptureStatus {
    ACTIVE,
    LEARNING,
    INACTIVE,
    PERMISSION_NEEDED
}

data class CaptureState(
    val status: CaptureStatus = CaptureStatus.PERMISSION_NEEDED,
    val learningProgress: Int = 0,
    val learningTotal: Int = 30,
    val todayOrderCount: Int = 0,
    val todayTripCount: Int = 0
) {
    val statusEmoji: String
        get() = when (status) {
            CaptureStatus.ACTIVE -> "\uD83D\uDFE2"
            CaptureStatus.LEARNING -> "\uD83D\uDFE1"
            CaptureStatus.INACTIVE -> "\uD83D\uDD34"
            CaptureStatus.PERMISSION_NEEDED -> "\u26A0\uFE0F"
        }

    val statusText: String
        get() = when (status) {
            CaptureStatus.ACTIVE -> "Auto Capture aktif"
            CaptureStatus.LEARNING -> "Belajar pola... ($learningProgress/$learningTotal)"
            CaptureStatus.INACTIVE -> "Auto Capture mati"
            CaptureStatus.PERMISSION_NEEDED -> "Perlu izin membaca layar"
        }
}

// ==================== SERVICE TYPE ====================

enum class ServiceType(val displayName: String, val emoji: String) {
    SPX_INSTANT("SPX Instant", "\u26A1"),
    SPX_SAMEDAY("SPX Sameday", "\uD83D\uDCE6"),
    SHOPEEFOOD("ShopeeFood", "\uD83C\uDF5C"),
    UNKNOWN("Unknown", "\u2753")
}

// ==================== TRIP & ORDER ====================

/** A single trip containing 1-5 orders. F001 spec: trip grouping logic. */
data class TripItem(
    val id: String,
    val tripNumber: Int,
    val tripCode: String?,
    val serviceType: ServiceType,
    val startTime: LocalTime,
    val completedTime: LocalTime? = null,
    val orders: List<OrderItem>,
    val totalEarning: Long? = null,
    val bonusPoints: Int? = null,
    val hasFullDetail: Boolean = false
) {
    val orderCount: Int get() = orders.size
    val isCombined: Boolean get() = orders.size > 1
    val allCaptured: Boolean get() = orders.all { it.isCaptured }

    val earningText: String?
        get() = totalEarning?.let {
            "Rp${java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(it)}"
        }

    val captureStatusText: String
        get() = when {
            allCaptured && hasFullDetail -> "\u2705 Lengkap"
            allCaptured -> "\u2705 Order ter-capture"
            else -> "\u26A0\uFE0F ${orders.count { !it.isCaptured }} pesanan belum ter-capture"
        }
}

/** A single order within a trip. */
data class OrderItem(
    val id: String,
    val orderSn: String?,
    val pickupAddress: String?,
    val pickupArea: String?,
    val deliveryAddress: String?,
    val deliveryArea: String?,
    val sellerName: String?,
    val paymentMethod: PaymentMethod?,
    val paymentAmount: Long?,
    val parcelWeight: String?,
    val isCaptured: Boolean = true,
    val parseConfidence: Float = 1f
)

enum class PaymentMethod(val displayName: String) {
    COD("COD"),
    NON_COD("Non-COD"),
    UNKNOWN("Unknown")
}

// ==================== HISTORY CAPTURE STATUS ====================

/** Riwayat capture status per trip. F002 spec. */
enum class HistoryCaptureStatus {
    COMPLETE,
    DETAIL_MISSING,
    NOT_CAPTURED
}

// ==================== SCREEN STATE ====================

data class OrderScreenState(
    val isLoading: Boolean = true,
    val date: LocalDate = LocalDate.now(),

    // Shift
    val shift: ShiftState = ShiftState(),
    val showEndShiftConfirm: Boolean = false,

    // Capture
    val capture: CaptureState = CaptureState(),

    // Trips & orders
    val trips: List<TripItem> = emptyList(),
    val selectedTripId: String? = null,

    // Summary
    val todayEarning: Long = 0,
    val todayOrders: Int = 0,
    val todayTrips: Int = 0,

    // History capture status (F002)
    val tripsWithoutDetail: Int = 0,
    val showHistoryReminder: Boolean = false
) {
    val hasTodayData: Boolean get() = trips.isNotEmpty()
    val selectedTrip: TripItem? get() = trips.find { it.id == selectedTripId }
}
