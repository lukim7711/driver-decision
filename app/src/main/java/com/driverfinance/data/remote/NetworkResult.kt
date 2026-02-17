package com.driverfinance.data.remote

/**
 * Sealed class for wrapping API call results.
 * Used across all remote data operations.
 *
 * Ref: ARCHITECTURE.md Section 6.1 (Error Handling)
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>()
}
