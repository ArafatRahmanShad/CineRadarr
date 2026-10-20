package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.ui.components.CountdownBadge
import com.example.ui.components.PulseDot
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaGreen
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceBorder
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.HotCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineScreen(
    allMedia: List<MediaItem>,
    watchlistItems: List<WatchlistEntity>,
    onItemClick: (MediaItem) -> Unit,
    onWatchlistToggle: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterWatchlistOnly by remember { mutableStateOf(false) }

    val displayedItems = remember(allMedia, watchlistItems, filterWatchlistOnly) {
        val base = if (filterWatchlistOnly) {
            val ids = watchlistItems.map { it.mediaId }.toSet()
            allMedia.filter { ids.contains(it.id) }
        } else {
            allMedia
        }
        base.sortedBy { it.releaseDateEpoch }
    }

    // Group by Month Year
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
    val grouped = remember(displayedItems) {
        displayedItems.groupBy {
            monthFormat.format(Date(it.releaseDateEpoch))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        // Timeline Header & Toggle Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Release Radar Timeline",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Chronological roadmap of big drops",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            FilterChip(
                selected = filterWatchlistOnly,
                onClick = { filterWatchlistOnly = !filterWatchlistOnly },
                label = { Text(if (filterWatchlistOnly) "Watchlist Only" else "All Releases", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = if (filterWatchlistOnly) Icons.Default.Bookmark else Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonCyan,
                    selectedLabelColor = CinemaDarkBackground,
                    containerColor = CinemaSurface
                )
            )
        }

        if (displayedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No releases in this timeline view.\nAdd titles to your watchlist to see them here!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                grouped.forEach { (monthName, itemsInMonth) ->
                    item {
                        TimelineMonthHeader(monthName = monthName, itemCount = itemsInMonth.size)
                    }

                    items(itemsInMonth, key = { it.id }) { item ->
                        val isBookmarked = watchlistItems.any { it.mediaId == item.id }
                        TimelineItemCard(
                            item = item,
                            isBookmarked = isBookmarked,
                            onCardClick = { onItemClick(item) },
                            onBookmarkClick = { onWatchlistToggle(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineMonthHeader(monthName: String, itemCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = CinemaGold.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaGold.copy(alpha = 0.5f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = CinemaGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = monthName.uppercase(),
                    color = CinemaGold,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(CinemaSurfaceBorder)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$itemCount titles",
            color = TextMuted,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun TimelineItemCard(
    item: MediaItem,
    isBookmarked: Boolean,
    onCardClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical Timeline node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (isBookmarked) NeonCyan else CinemaSurfaceBorder)
                    .border(2.dp, CinemaDarkBackground, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Card Content
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isBookmarked) NeonCyan.copy(alpha = 0.4f) else CinemaSurfaceBorder),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 55.dp, height = 75.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CinemaSurfaceElevated)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${item.platform} • ${item.releaseDateDisplay}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CountdownBadge(
                            countdownText = item.getReleaseCountdownString(),
                            status = item.status
                        )

                        IconButton(
                            onClick = onBookmarkClick,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) NeonCyan else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
