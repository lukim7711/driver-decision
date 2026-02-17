package com.driverfinance.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.CaptureState
import com.driverfinance.domain.model.CaptureStatus
import com.driverfinance.domain.model.OrderItem
import com.driverfinance.domain.model.OrderScreenState
import com.driverfinance.domain.model.PaymentMethod
import com.driverfinance.domain.model.ServiceType
import com.driverfinance.domain.model.ShiftState
import com.driverfinance.domain.model.ShiftStatus
import com.driverfinance.domain.model.TripItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel for Order tab (F001 + F002 UI layer).
 *
 * Manages:
 * - Shift lifecycle (start/stop)
 * - Trip + order list from F001 captured_orders + trips
 * - Capture service status
 * - History capture reminders (F002)
 *
 * TODO: Inject real repositories:
 *   CaptureRepository, TripDao, CapturedOrderDao
 */
@HiltViewModel
class OrderViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(OrderScreenState())
    val screenState: StateFlow<OrderScreenState> = _screenState.asStateFlow()

    private val timeFmt = DateTimeFormatter.ofPattern("HH:mm")

    init {
        loadTodayData()
    }

    // ==================== SHIFT MANAGEMENT ====================

    fun startShift() {
        val now = LocalTime.now()
        Timber.d("Shift started at %s", now.format(timeFmt))

        _screenState.update { state ->
            state.copy(
                shift = ShiftState(
                    status = ShiftStatus.ACTIVE,
                    startTime = now,
                    date = LocalDate.now(),
                    duration = "0j 0m"
                )
            )
        }

        // Start duration ticker
        startDurationTicker()

        // TODO: Activate CaptureAccessibilityService
        // TODO: Start foreground service notification
    }

    fun requestEndShift() {
        _screenState.update { it.copy(showEndShiftConfirm = true) }
    }

    fun dismissEndShiftConfirm() {
        _screenState.update { it.copy(showEndShiftConfirm = false) }
    }

    fun confirmEndShift() {
        val now = LocalTime.now()
        val start = _screenState.value.shift.startTime ?: now
        val duration = formatDuration(start, now)

        Timber.d("Shift ended at %s, duration: %s", now.format(timeFmt), duration)

        _screenState.update { state ->
            state.copy(
                shift = state.shift.copy(
                    status = ShiftStatus.ENDED,
                    endTime = now,
                    duration = duration
                ),
                showEndShiftConfirm = false
            )
        }

        // Check if history reminders needed
        checkHistoryReminder()

        // TODO: Stop foreground service
    }

    fun startNewShift() {
        startShift()
    }

    // ==================== TRIP ACTIONS ====================

    fun selectTrip(tripId: String) {
        _screenState.update { it.copy(selectedTripId = tripId) }
    }

    fun clearSelectedTrip() {
        _screenState.update { it.copy(selectedTripId = null) }
    }

    fun refresh() {
        loadTodayData()
    }

    // ==================== PRIVATE HELPERS ====================

    private fun loadTodayData() {
        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            delay(300)

            // TODO: Load from TripDao + CapturedOrderDao for today
            val trips = getPlaceholderTrips()
            val todayEarning = trips.mapNotNull { it.totalEarning }.sum()

            _screenState.update { state ->
                state.copy(
                    isLoading = false,
                    trips = trips,
                    todayEarning = todayEarning,
                    todayOrders = trips.sumOf { it.orderCount },
                    todayTrips = trips.size,
                    capture = CaptureState(
                        status = CaptureStatus.ACTIVE,
                        todayOrderCount = trips.sumOf { it.orderCount },
                        todayTripCount = trips.size
                    ),
                    tripsWithoutDetail = trips.count { !it.hasFullDetail }
                )
            }
        }
    }

    private fun startDurationTicker() {
        viewModelScope.launch {
            while (_screenState.value.shift.isActive) {
                delay(60_000) // Update every minute
                val start = _screenState.value.shift.startTime ?: break
                val duration = formatDuration(start, LocalTime.now())
                _screenState.update { state ->
                    state.copy(
                        shift = state.shift.copy(duration = duration)
                    )
                }
            }
        }
    }

    private fun formatDuration(start: LocalTime, end: LocalTime): String {
        val duration = Duration.between(start, end)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        return "${hours}j ${minutes}m"
    }

    private fun checkHistoryReminder() {
        val tripsWithoutDetail = _screenState.value.trips.count { !it.hasFullDetail }
        if (tripsWithoutDetail > 0) {
            _screenState.update {
                it.copy(
                    showHistoryReminder = true,
                    tripsWithoutDetail = tripsWithoutDetail
                )
            }
        }
    }

    // ==================== PLACEHOLDER DATA ====================

    private fun getPlaceholderTrips(): List<TripItem> = listOf(
        TripItem(
            id = "trip-1",
            tripNumber = 1,
            tripCode = "#6VUS",
            serviceType = ServiceType.SPX_INSTANT,
            startTime = LocalTime.of(16, 42),
            completedTime = LocalTime.of(18, 13),
            totalEarning = 39_200,
            bonusPoints = 250,
            hasFullDetail = true,
            orders = listOf(
                OrderItem(
                    id = "order-1",
                    orderSn = "260210BVSAY2V2",
                    pickupAddress = "Taman Duta Mas, Grogol Petamburan",
                    pickupArea = "Grogol Petamburan",
                    deliveryAddress = "Jl. Kampung Norogtok, Tangerang",
                    deliveryArea = "Nerogtog, Tangerang",
                    sellerName = "Toko Elektronik Jaya",
                    paymentMethod = PaymentMethod.COD,
                    paymentAmount = 125_000,
                    parcelWeight = "1.2 kg"
                ),
                OrderItem(
                    id = "order-2",
                    orderSn = "260210BA78D4F9",
                    pickupAddress = "Jl. Kusuma IV, Grogol Petamburan",
                    pickupArea = "Grogol Petamburan",
                    deliveryAddress = "Jl. Agus Salim, Tanah Abang",
                    deliveryArea = "Tanah Abang",
                    sellerName = "Fashion Store ID",
                    paymentMethod = PaymentMethod.NON_COD,
                    paymentAmount = null,
                    parcelWeight = "0.8 kg"
                ),
                OrderItem(
                    id = "order-3",
                    orderSn = "260210C0XGW4H7",
                    pickupAddress = "Jl. H Agus Salim, Tanah Abang",
                    pickupArea = "Tanah Abang",
                    deliveryAddress = "Jl. Pejompongan, Kebayoran Baru",
                    deliveryArea = "Kebayoran Baru",
                    sellerName = "Aksesoris HP",
                    paymentMethod = PaymentMethod.NON_COD,
                    paymentAmount = null,
                    parcelWeight = "2.1 kg"
                )
            )
        ),
        TripItem(
            id = "trip-2",
            tripNumber = 2,
            tripCode = "#8KPL",
            serviceType = ServiceType.SHOPEEFOOD,
            startTime = LocalTime.of(20, 43),
            completedTime = LocalTime.of(21, 15),
            totalEarning = 19_200,
            bonusPoints = 150,
            hasFullDetail = true,
            orders = listOf(
                OrderItem(
                    id = "order-4",
                    orderSn = "260210FOOD01",
                    pickupAddress = "McDonald\u2019s Puri Indah",
                    pickupArea = "Kembangan",
                    deliveryAddress = "Jl. Kemanggisan, Palmerah",
                    deliveryArea = "Palmerah",
                    sellerName = "McDonald\u2019s Puri Indah",
                    paymentMethod = PaymentMethod.NON_COD,
                    paymentAmount = null,
                    parcelWeight = null
                ),
                OrderItem(
                    id = "order-5",
                    orderSn = "260210FOOD02",
                    pickupAddress = "KFC Puri Indah Mall",
                    pickupArea = "Kembangan",
                    deliveryAddress = "Jl. Rawa Belong, Palmerah",
                    deliveryArea = "Palmerah",
                    sellerName = "KFC Puri Indah Mall",
                    paymentMethod = PaymentMethod.NON_COD,
                    paymentAmount = null,
                    parcelWeight = null
                )
            )
        ),
        TripItem(
            id = "trip-3",
            tripNumber = 3,
            tripCode = "#2WQR",
            serviceType = ServiceType.SPX_SAMEDAY,
            startTime = LocalTime.of(14, 20),
            completedTime = null,
            totalEarning = 52_000,
            bonusPoints = null,
            hasFullDetail = false,
            orders = listOf(
                OrderItem(
                    id = "order-6",
                    orderSn = "260210SAMEDAY1",
                    pickupAddress = "Tangerang City Mall",
                    pickupArea = "Tangerang",
                    deliveryAddress = "Jl. Daan Mogot, Tangerang",
                    deliveryArea = "Tangerang",
                    sellerName = "Toko Buku Gramedia",
                    paymentMethod = PaymentMethod.NON_COD,
                    paymentAmount = null,
                    parcelWeight = "3.5 kg"
                )
            )
        )
    )
}
