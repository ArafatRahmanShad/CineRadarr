package com.example.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MediaDetailSheet
import com.example.ui.components.PulseDot
import com.example.ui.components.WatchlistConfigDialog
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.screens.WatchlistScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineRadarApp(
    viewModel: CineRadarViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()
    val allMedia by viewModel.allMedia.collectAsState()
    val filteredMedia by viewModel.filteredMedia.collectAsState()
    val watchlistItems by viewModel.watchlistItems.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isGlobalAlertsEnabled by viewModel.isGlobalAlertsEnabled.collectAsState()
    val globalReminderLead by viewModel.globalReminderLead.collectAsState()
    val armedReleasesCount by viewModel.armedReleasesCount.collectAsState()
    val isTmdbSyncing by viewModel.isTmdbSyncing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Feedback message handler
    LaunchedEffect(uiState.testAlertSentMessage) {
        uiState.testAlertSentMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Predictive back handling
    BackHandler(enabled = uiState.selectedMediaDetail != null || uiState.watchlistConfigTarget != null || uiState.isSearchOpen || uiState.currentTab != AppNavTab.EXPLORE) {
        when {
            uiState.selectedMediaDetail != null -> viewModel.openMediaDetail(null)
            uiState.watchlistConfigTarget != null -> viewModel.openWatchlistConfig(null)
            uiState.isSearchOpen -> viewModel.toggleSearchOpen(false)
            uiState.currentTab != AppNavTab.EXPLORE -> viewModel.setNavTab(AppNavTab.EXPLORE)
        }
    }

    Scaffold(
        containerColor = CinemaDarkBackground,
        contentColor = TextPrimary,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = CinemaSurfaceElevated,
                    contentColor = TextPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Text(data.visuals.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        topBar = {
            Surface(
                color = CinemaSurface,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, CinemaSurfaceBorder),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ) {
                if (uiState.isSearchOpen) {
                    // Search Bar Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleSearchOpen(false) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Close Search",
                                tint = TextPrimary
                            )
                        }

                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search upcoming movies, series, cast, genre...") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = NeonCyan
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            modifier = Modifier.weight(1f)
                        )

                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
                } else {
                    // Standard Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = NeonCyan.copy(alpha = 0.15f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Radar,
                                        contentDescription = "CineRadar Logo",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Cine",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    )
                                    Text(
                                        text = "Radar",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    )
                                }
                                Text(
                                    text = "Release Updates & Watchlist",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.toggleSearchOpen(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Releases",
                                    tint = TextPrimary
                                )
                            }

                            IconButton(onClick = { viewModel.setNavTab(AppNavTab.ALERTS) }) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge(
                                                containerColor = HotCoral,
                                                contentColor = Color.White
                                            ) {
                                                Text("$unreadCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Release Alerts",
                                        tint = if (uiState.currentTab == AppNavTab.ALERTS) NeonCyan else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CinemaSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.EXPLORE,
                    onClick = { viewModel.setNavTab(AppNavTab.EXPLORE) },
                    icon = {
                        Icon(imageVector = Icons.Default.Explore, contentDescription = "Explore")
                    },
                    label = { Text("Explore") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaDarkBackground,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.WATCHLIST,
                    onClick = { viewModel.setNavTab(AppNavTab.WATCHLIST) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (watchlistItems.isNotEmpty()) {
                                    Badge(containerColor = CinemaGold, contentColor = CinemaDarkBackground) {
                                        Text("${watchlistItems.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Watchlist")
                        }
                    },
                    label = { Text("Watchlist") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaDarkBackground,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.TIMELINE,
                    onClick = { viewModel.setNavTab(AppNavTab.TIMELINE) },
                    icon = {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Timeline")
                    },
                    label = { Text("Timeline") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaDarkBackground,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = uiState.currentTab == AppNavTab.ALERTS,
                    onClick = { viewModel.setNavTab(AppNavTab.ALERTS) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge(containerColor = HotCoral, contentColor = Color.White) {
                                        Text("$unreadCount")
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Alarm, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Radar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CinemaDarkBackground,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppNavTab.EXPLORE -> {
                    ExploreScreen(
                        mediaList = filteredMedia,
                        spotlightMedia = viewModel.spotlightItem,
                        latestUpdates = viewModel.latestUpdates,
                        selectedType = uiState.selectedType,
                        selectedGenre = uiState.selectedGenre,
                        selectedStatus = uiState.selectedStatus,
                        genres = viewModel.genres,
                        isMediaInWatchlist = { viewModel.isMediaInWatchlist(it) },
                        isTmdbSyncing = isTmdbSyncing,
                        onSyncTmdb = { viewModel.syncTmdbLive() },
                        onSelectType = { viewModel.setTypeFilter(it) },
                        onSelectGenre = { viewModel.setGenreFilter(it) },
                        onSelectStatus = { viewModel.setStatusFilter(it) },
                        onMediaClick = { viewModel.openMediaDetail(it) },
                        onWatchlistClick = { media ->
                            if (viewModel.isMediaInWatchlist(media.id)) {
                                viewModel.openMediaDetail(media)
                            } else {
                                viewModel.openWatchlistConfig(media)
                            }
                        },
                        onShareClick = { media ->
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Upcoming: ${media.title}")
                                putExtra(Intent.EXTRA_TEXT, media.generateTelegramShareText())
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Release Update via Telegram / Apps"))
                        }
                    )
                }

                AppNavTab.WATCHLIST -> {
                    WatchlistScreen(
                        watchlistItems = watchlistItems,
                        allMedia = allMedia,
                        selectedStatus = uiState.selectedWatchlistStatus,
                        onSelectStatus = { viewModel.setWatchlistStatusTab(it) },
                        onItemClick = { viewModel.openMediaDetail(it) },
                        onToggleNotification = { id, enabled, days ->
                            viewModel.toggleWatchlistNotification(id, enabled, days)
                        },
                        onUpdateStatus = { id, newStatus ->
                            viewModel.updateWatchlistStatus(id, newStatus)
                        },
                        onRemoveItem = { id ->
                            viewModel.removeFromWatchlist(id)
                        },
                        onNavigateExplore = {
                            viewModel.setNavTab(AppNavTab.EXPLORE)
                        }
                    )
                }

                AppNavTab.TIMELINE -> {
                    TimelineScreen(
                        allMedia = allMedia,
                        watchlistItems = watchlistItems,
                        onItemClick = { viewModel.openMediaDetail(it) },
                        onWatchlistToggle = { media ->
                            if (viewModel.isMediaInWatchlist(media.id)) {
                                viewModel.removeFromWatchlist(media.id)
                            } else {
                                viewModel.openWatchlistConfig(media)
                            }
                        }
                    )
                }

                AppNavTab.ALERTS -> {
                    AlertsScreen(
                        alerts = alerts,
                        unreadCount = unreadCount,
                        isGlobalAlertsEnabled = isGlobalAlertsEnabled,
                        globalReminderLead = globalReminderLead,
                        armedReleasesCount = armedReleasesCount,
                        onToggleGlobalAlerts = { viewModel.setGlobalAlerts(it) },
                        onSetGlobalReminderLead = { viewModel.setGlobalReminderLead(it) },
                        onArmAllReleases = { viewModel.armAllUpcomingReleasesManually() },
                        onSendTestNotification = { viewModel.sendTestAlert() },
                        onMarkAsRead = { viewModel.markAlertAsRead(it) },
                        onMarkAllAsRead = { viewModel.markAllAlertsAsRead() },
                        onDeleteAlert = { viewModel.deleteAlert(it) },
                        onClearAll = { viewModel.clearAllAlerts() }
                    )
                }
            }
        }
    }

    // Detail Bottom Sheet
    uiState.selectedMediaDetail?.let { media ->
        val watchlistEntry = viewModel.getWatchlistEntity(media.id)
        MediaDetailSheet(
            media = media,
            watchlistEntry = watchlistEntry,
            onDismiss = { viewModel.openMediaDetail(null) },
            onAddToWatchlist = { status, notifyEnabled, notifyDaysBefore, notes ->
                viewModel.addToWatchlist(media, status, notifyEnabled, notifyDaysBefore, notes)
            },
            onRemoveFromWatchlist = {
                viewModel.removeFromWatchlist(media.id)
            },
            onUpdateNotificationSettings = { enabled, daysBefore ->
                viewModel.toggleWatchlistNotification(media.id, !enabled, daysBefore)
            },
            onUpdateNotesAndRating = { rating, notes ->
                viewModel.updateWatchlistRatingAndNotes(media.id, rating, notes)
            }
        )
    }

    // Quick Watchlist Config Dialog
    uiState.watchlistConfigTarget?.let { media ->
        val existingEntry = viewModel.getWatchlistEntity(media.id)
        WatchlistConfigDialog(
            media = media,
            existingEntity = existingEntry,
            onDismiss = { viewModel.openWatchlistConfig(null) },
            onSave = { status, notifyEnabled, notifyDaysBefore, notes ->
                viewModel.addToWatchlist(media, status, notifyEnabled, notifyDaysBefore, notes)
            }
        )
    }
}
