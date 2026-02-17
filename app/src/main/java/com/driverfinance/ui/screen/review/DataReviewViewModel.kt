package com.driverfinance.ui.screen.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.DataReviewEntity
import com.driverfinance.domain.usecase.extraction.GetPendingReviewsUseCase
import com.driverfinance.domain.usecase.extraction.SubmitCorrectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DataReviewUiState {
    data object Loading : DataReviewUiState
    data class Success(val reviews: List<DataReviewEntity>) : DataReviewUiState
    data class Error(val message: String) : DataReviewUiState
}

@HiltViewModel
class DataReviewViewModel @Inject constructor(
    private val getPendingReviewsUseCase: GetPendingReviewsUseCase,
    private val submitCorrectionUseCase: SubmitCorrectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DataReviewUiState>(DataReviewUiState.Loading)
    val uiState: StateFlow<DataReviewUiState> = _uiState.asStateFlow()

    init {
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            try {
                getPendingReviewsUseCase().collectLatest { reviews ->
                    _uiState.value = DataReviewUiState.Success(reviews = reviews)
                }
            } catch (e: Exception) {
                _uiState.value = DataReviewUiState.Error(
                    message = e.message ?: "Gagal memuat data review"
                )
            }
        }
    }

    fun confirmSuggestion(reviewId: String) {
        viewModelScope.launch {
            val review = (_uiState.value as? DataReviewUiState.Success)
                ?.reviews?.find { it.id == reviewId }
            submitCorrectionUseCase(
                reviewId = reviewId,
                correctedValue = review?.suggestedValue,
                action = SubmitCorrectionUseCase.CorrectionAction.CONFIRM
            )
        }
    }

    fun dismiss(reviewId: String) {
        viewModelScope.launch {
            submitCorrectionUseCase(
                reviewId = reviewId,
                correctedValue = null,
                action = SubmitCorrectionUseCase.CorrectionAction.DISMISS
            )
        }
    }

    fun submitCorrection(reviewId: String, correctedValue: String) {
        viewModelScope.launch {
            submitCorrectionUseCase(
                reviewId = reviewId,
                correctedValue = correctedValue,
                action = SubmitCorrectionUseCase.CorrectionAction.CORRECT
            )
        }
    }
}
