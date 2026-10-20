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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.WatchlistEntity
import com.example.data.model.MediaItem
import com.example.data.model.WatchlistStatus
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

@Composable
fun WatchlistScreen(
    watchlistItems: List<WatchlistEntity>,
    allMedia: List<MediaItem>,
    selectedStatus: WatchlistStatus,
    onSelectStatus: (WatchlistStatus) -> Unit,
    onItemClick: (MediaItem) -> Unit,
    onToggleNotification: (mediaId: String, currentEnabled: Boolean, daysBefore: Int) -> Unit,
    onUpdateStatus: (mediaId: String, newStatus: WatchlistStatus) -> Unit,
    onRemoveItem: (mediaId: String) -> Unit,
    onNavigateExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredList = watchlistItems.filter { it.status == selectedStatus.name }
    val activeAlertsCount = watchlistItems.count { it.notifyEnabled }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        // Summary Card Stats
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${watchlistItems.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Tracked",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(CinemaSurfaceBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CinemaGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$activeAlertsCount",
                            style = MaterialTheme.typography.headlineMedium,
                            color = CinemaGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Release Alerts",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(CinemaSurfaceBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val nextUpcoming = watchlistItems
                        .filter { it.releaseDateEpoch >= System.currentTimeMillis() }
                        .minByOrNull { it.releaseDateEpoch }
                    val days = nextUpcoming?.let {
                        ((it.releaseDateEpoch - System.currentTimeMillis()) / 86400000L).coerceAtLeast(0)
                    }

                    Text(
                        text = if (days != null) "${days}d" else "--",
                        style = MaterialTheme.typography.headlineMedium,
                        color = HotCoral,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Next Premiere",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Status Tabs: Want to Watch, Watching, Watched
        TabRow(
            selectedTabIndex = selectedStatus.ordinal,
            containerColor = CinemaDarkBackground,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedStatus.ordinal]),
                    color = NeonCyan
                )
            },
            divider = {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CinemaSurfaceBorder))
            }
        ) {
            WatchlistStatus.values().forEach { status ->
                val count = watchlistItems.count { it.status == status.name }
                Tab(
                    selected = selectedStatus == status,
                    onClick = { onSelectStatus(status) },
                    text = {
                        Text(
                            text = "${status.label} ($count)",
                            fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedStatus == status) NeonCyan else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Content
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No titles in '${selectedStatus.label}'",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Browse upcoming movies & series and tap bookmark to add them to your automated release radar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateExplore,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = CinemaDarkBackground)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Explore Releases", color = CinemaDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.mediaId }) { item ->
                    val media = allMedia.find { it.id == item.mediaId }
                    WatchlistItemCard(
                        item = item,
                        media = media,
                        onCardClick = {
                            if (media != null) onItemClick(media)
                        },
                        onToggleNotification = {
                            onToggleNotification(item.mediaId, item.notifyEnabled, item.notifyDaysBefore)
                        },
                        onStatusChange = { newStatus ->
                            onUpdateStatus(item.mediaId, newStatus)
                        },
                        onDeleteClick = {
                            onRemoveItem(item.mediaId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistItemCard(
    item: WatchlistEntity,
    media: MediaItem?,
    onCardClick: () -> Unit,
    onToggleNotification: () -> Unit,
    onStatusChange: (WatchlistStatus) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSurfaceBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 65.dp, height = 90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CinemaSurfaceElevated)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = "${item.platform} • ${item.releaseDateDisplay}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCyan
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val countdown = media?.getReleaseCountdownString()
                    if (countdown != null) {
                        Surface(
                            color = HotCoral.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = HotCoral,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = countdown,
                                    color = HotCoral,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notification Radar row
            Surface(
                color = CinemaSurfaceElevated,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (item.notifyEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (item.notifyEnabled) CinemaGold else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (item.notifyEnabled) {
                                val timing = if (item.notifyDaysBefore == 0) "day of release" else "${item.notifyDaysBefore}d before"
                                "Release Alert: Active ($timing)"
                            } else {
                                "Release Alert: Paused"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (item.notifyEnabled) TextPrimary else TextMuted
                        )
                    }

                    Switch(
                        checked = item.notifyEnabled,
                        onCheckedChange = { onToggleNotification() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CinemaGold,
                            checkedTrackColor = CinemaSurfaceBorder
                        ),
                        modifier = Modifier.scale(0.85f)
                    )
                }
            }
        }
    }
}
