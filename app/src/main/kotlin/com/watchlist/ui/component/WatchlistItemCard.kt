package com.watchlist.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.watchlist.data.model.MediaType
import com.watchlist.data.model.StreamingAvailability
import com.watchlist.data.model.WatchlistItem
import com.watchlist.data.remote.TmdbApi
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WatchlistItemCard(
    item: WatchlistItem,
    services: List<StreamingAvailability> = emptyList(),
    onDelete: () -> Unit,
    onMarkWatched: (Boolean) -> Unit,
    onSetRating: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val distinctServices = services.distinctBy { it.serviceName }

    Card(modifier = modifier.fillMaxWidth()) {
        Column {
            // Main row — always clickable to toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.posterPath != null) {
                    AsyncImage(
                        model = TmdbApi.IMAGE_BASE_URL + item.posterPath,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp, 90.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                } else {
                    Icon(
                        imageVector = if (item.type == MediaType.MOVIE) Icons.Default.Movie else Icons.Default.Tv,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp, 90.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (item.type == MediaType.MOVIE) "Movie" else "TV Series",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Score block
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (item.voteAverage != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f", item.voteAverage),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "TMDB",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (item.userRating != null) {
                        Spacer(Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = "${item.userRating}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Mine",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expanded section
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(start = 80.dp, end = 8.dp, bottom = 4.dp)) {
                    HorizontalDivider()

                    item.releaseDate?.let { date ->
                        val year = date.take(4)
                        Text(
                            text = year,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    distinctServices.forEach { service ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (service.serviceLogoPath != null) {
                                AsyncImage(
                                    model = TmdbApi.LOGO_BASE_URL + service.serviceLogoPath,
                                    contentDescription = service.serviceName,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = service.serviceName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Watched/rating controls
                    if (!item.watched) {
                        TextButton(onClick = { onMarkWatched(true) }) {
                            Text("Mark as watched")
                        }
                    } else {
                        Text(
                            text = "Your rating",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = (item.userRating ?: 0).toFloat(),
                                onValueChange = { onSetRating(it.roundToInt()) },
                                valueRange = 0f..10f,
                                steps = 9,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${item.userRating ?: 0}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        TextButton(onClick = { onMarkWatched(false) }) {
                            Text("Move back to watchlist")
                        }
                    }

                    // Delete at the bottom of expanded section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove from watchlist",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
