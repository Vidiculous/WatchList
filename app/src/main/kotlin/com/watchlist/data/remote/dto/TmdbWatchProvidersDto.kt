package com.watchlist.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbWatchProvidersResponse(
    val results: Map<String, TmdbCountryProviders>
)

@JsonClass(generateAdapter = true)
data class TmdbCountryProviders(
    val flatrate: List<TmdbProvider>?,
    val rent: List<TmdbProvider>?,
    val buy: List<TmdbProvider>?
)

@JsonClass(generateAdapter = true)
data class TmdbProvider(
    @Json(name = "provider_name") val providerName: String,
    @Json(name = "logo_path") val logoPath: String?,
    @Json(name = "provider_id") val providerId: Int
)
