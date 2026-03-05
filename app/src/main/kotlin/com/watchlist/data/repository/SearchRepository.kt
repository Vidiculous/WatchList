package com.watchlist.data.repository

import com.watchlist.data.model.MediaType
import com.watchlist.data.model.SearchResult
import com.watchlist.data.remote.TmdbApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val api: TmdbApi) {

    suspend fun search(query: String, type: MediaType): List<SearchResult> {
        return try {
            val response = when (type) {
                MediaType.MOVIE -> api.searchMovies(query)
                MediaType.TV -> api.searchTv(query)
            }
            response.results.mapNotNull { result ->
                val title = result.title?.takeIf { it.isNotBlank() }
                    ?: result.name?.takeIf { it.isNotBlank() }
                    ?: return@mapNotNull null
                SearchResult(
                    tmdbId = result.id,
                    title = title,
                    type = type,
                    posterPath = result.posterPath
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
