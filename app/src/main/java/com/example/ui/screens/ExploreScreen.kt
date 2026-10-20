package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.model.MediaUpdate
import com.example.data.model.ReleaseStatus
import com.example.ui.components.LiveCountdownSpotlight
import com.example.ui.components.MediaPosterCard
import com.example.ui.components.PulseDot
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceBorder
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.HotCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExploreScreen(
    mediaList: List<MediaItem>,
    spotlightMedia: MediaItem?,
    latestUpdates: List<MediaUpdate>,
    selectedType: MediaType?,
    selectedGenre: String?,
    selectedStatus: ReleaseStatus?,
    genres: List<String>,
    isMediaInWatchlist: (String) -> Boolean,
    onSelectType: (MediaType?) -> Unit,
    onSelectGenre: (String?) -> Unit,
    onSelectStatus: (ReleaseStatus?) -> Unit,
    onMediaClick: (MediaItem) -> Unit,
    onWatchlistClick: (MediaItem) -> Unit,
    onShareClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Spotlight Live Countdown
        if (spotlightMedia != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    LiveCountdownSpotlight(
                        media = spotlightMedia,
                        onViewDetail = { onMediaClick(spotlightMedia) }
                    )
                }
            }
        }

        // Breaking Updates / Trailer Drops Bar
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulseDot()
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LATEST BREAKING UPDATES",
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Instant Ticker",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(latestUpdates) { update ->
                        UpdateTickerCard(
                            update = update,
                            onClick = {
                                val item = mediaList.find { it.id == update.mediaId }
                                if (item != null) onMediaClick(item)
                            }
                        )
                    }
                }
            }
        }

        // Category Filter Chips (All, Movies, TV Series, Premiering Soon)
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedType == null && selectedStatus == null,
                        onClick = {
                            onSelectType(null)
                            onSelectStatus(null)
                        },
                        label = { Text("⚡ All Releases") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = CinemaDarkBackground,
                            containerColor = CinemaSurface
                        )
                    )

                    FilterChip(
                        selected = selectedType == MediaType.MOVIE,
                        onClick = { onSelectType(if (selectedType == MediaType.MOVIE) null else MediaType.MOVIE) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("Movies") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = CinemaDarkBackground,
                            containerColor = CinemaSurface
                        )
                    )

                    FilterChip(
                        selected = selectedType == MediaType.SERIES,
                        onClick = { onSelectType(if (selectedType == MediaType.SERIES) null else MediaType.SERIES) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("TV Series") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = CinemaDarkBackground,
                            containerColor = CinemaSurface
                        )
                    )

                    FilterChip(
                        selected = selectedStatus == ReleaseStatus.PREMIERING_SOON,
                        onClick = {
                            onSelectStatus(if (selectedStatus == ReleaseStatus.PREMIERING_SOON) null else ReleaseStatus.PREMIERING_SOON)
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = HotCoral, modifier = Modifier.size(16.dp))
                        },
                        label = { Text("Premiering Soon") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HotCoral,
                            selectedLabelColor = Color.White,
                            containerColor = CinemaSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Genres row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres) { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { onSelectGenre(genre) },
                            label = { Text(genre, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CinemaGold.copy(alpha = 0.2f),
                                selectedLabelColor = CinemaGold,
                                containerColor = CinemaSurfaceElevated
                            )
                        )
                    }
                }
            }
        }

        // Section Title: Releases Feed
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Releases (${mediaList.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sorted by Date",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        // Movie / Series Cards
        if (mediaList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No releases found matching your filters.\nTry resetting filters or searching another title.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            items(mediaList, key = { it.id }) { media ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    MediaPosterCard(
                        media = media,
                        isInWatchlist = isMediaInWatchlist(media.id),
                        onCardClick = { onMediaClick(media) },
                        onWatchlistClick = { onWatchlistClick(media) },
                        onShareClick = { onShareClick(media) }
                    )
                }
            }
        }
    }
}

@Composable
fun UpdateTickerCard(
    update: MediaUpdate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CinemaSurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (update.isHot) HotCoral.copy(alpha = 0.5f) else CinemaSurfaceBorder),
        modifier = modifier
            .width(280.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (update.isHot) HotCoral.copy(alpha = 0.15f) else NeonCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = update.updateType.uppercase(),
                        color = if (update.isHot) HotCoral else NeonCyan,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${update.timestampDaysAgo}d ago",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = update.mediaTitle,
                style = MaterialTheme.typography.labelMedium,
                color = CinemaGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = update.headline,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
}
