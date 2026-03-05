package com.watchlist.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TmdbVoteDto(
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "release_date") val releaseDate: String?,    // movies
    @Json(name = "first_air_date") val firstAirDate: String?  // TV
)
