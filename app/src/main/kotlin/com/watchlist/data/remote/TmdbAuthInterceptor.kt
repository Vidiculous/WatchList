package com.watchlist.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class TmdbAuthInterceptor(private val apiKeyProvider: com.watchlist.data.preferences.ApiKeyProvider) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${apiKeyProvider.key}")
            .build()
        return chain.proceed(request)
    }
}
