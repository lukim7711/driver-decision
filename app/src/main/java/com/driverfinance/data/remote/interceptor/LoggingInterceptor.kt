package com.driverfinance.data.remote.interceptor

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * HTTP logging interceptor for debug builds.
 * Logs request/response body via Timber.
 */
object LoggingInterceptor {

    fun create(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.tag("GroqAPI").d(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
