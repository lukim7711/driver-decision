package com.driverfinance.data.remote.interceptor

import com.driverfinance.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that adds Groq API key to every request.
 * Key is stored in BuildConfig.GROQ_API_KEY (from local.properties).
 *
 * Header: Authorization: Bearer <GROQ_API_KEY>
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
