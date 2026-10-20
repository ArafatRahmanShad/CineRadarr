package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.NotificationAlertEntity
import com.example.notification.NotificationHelper
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
fun AlertsScreen(
    alerts: List<NotificationAlertEntity>,
    unreadCount: Int,
    isGlobalAlertsEnabled: Boolean,
    globalReminderLead: String,
    armedReleasesCount: Int,
    onToggleGlobalAlerts: (Boolean) -> Unit,
    onSetGlobalReminderLead: (String) -> Unit,
    onArmAllReleases: () -> Unit,
    onSendTestNotification: () -> Unit,
    onMarkAsRead: (Long) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteAlert: (Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    val timeFormat = SimpleDateFormat("MMM d, h:mm a", Locale.US)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        // Master Push Notification Settings for ALL Upcoming Releases
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulseDot()
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Push Reminders (All Releases)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Remind before release time for all movies & series",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isGlobalAlertsEnabled,
                        onCheckedChange = { onToggleGlobalAlerts(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = CinemaSurfaceBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Armed Status Tag
                Surface(
                    color = if (isGlobalAlertsEnabled) CinemaGreen.copy(alpha = 0.15f) else CinemaSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isGlobalAlertsEnabled) CinemaGreen.copy(alpha = 0.4f) else CinemaSurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isGlobalAlertsEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (isGlobalAlertsEnabled) CinemaGreen else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isGlobalAlertsEnabled)
                                    "Status: $armedReleasesCount upcoming releases armed with alarms"
                                else
                                    "Status: Push notifications paused for all releases",
                                color = if (isGlobalAlertsEnabled) CinemaGreen else TextSecondary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (isGlobalAlertsEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Remind Me Before Release:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Lead time selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            NotificationHelper.LEAD_2_HOURS_BEFORE to "2 Hours Before",
                            NotificationHelper.LEAD_1_DAY_BEFORE to "1 Day Before",
                            NotificationHelper.LEAD_2_DAYS_BEFORE to "2 Days Before",
                            NotificationHelper.LEAD_1_WEEK_BEFORE to "1 Week Before",
                            NotificationHelper.LEAD_RELEASE_MORNING to "On Release Morning"
                        ).forEach { (leadKey, label) ->
                            FilterChip(
                                selected = globalReminderLead == leadKey,
                                onClick = { onSetGlobalReminderLead(leadKey) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CinemaGold.copy(alpha = 0.25f),
                                    selectedLabelColor = CinemaGold,
                                    containerColor = CinemaSurfaceElevated
                                )
                            )
                        }
                    }
                }

                // Android 13+ Permission banner if needed
                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = HotCoral.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "System Permission Needed",
                                    color = HotCoral,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Enable notification permission to show status bar alerts.",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HotCoral),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Allow", color = TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Arm All + Test Notification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onArmAllReleases,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Re-Arm All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSendTestNotification,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = CinemaDarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Alert", color = CinemaDarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // APK Download & Location Information Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, CinemaSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NeonCyan.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "APK",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "APK Generated & Ready",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "app/build/outputs/apk/debug/app-debug.apk",
                        style = MaterialTheme.typography.bodySmall,
                        color = CinemaGold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Export via Settings > Download APK or from file explorer.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Alerts History (${alerts.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Row {
                if (unreadCount > 0) {
                    IconButton(onClick = onMarkAllAsRead) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark all as read",
                            tint = NeonCyan
                        )
                    }
                }

                if (alerts.isNotEmpty()) {
                    IconButton(onClick = onClearAll) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "Clear all",
                            tint = TextMuted
                        )
                    }
                }
            }
        }

        // Alerts List
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "All Upcoming Reminders Armed",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Push notification alarms are scheduled for all upcoming movies & series. When the release time approaches, your phone will alert you.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(alerts, key = { it.id }) { alert ->
                    AlertItemCard(
                        alert = alert,
                        formattedTime = timeFormat.format(Date(alert.deliveredEpoch.takeIf { it > 0 } ?: alert.scheduledEpoch)),
                        onMarkRead = { onMarkAsRead(alert.id) },
                        onDelete = { onDeleteAlert(alert.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertItemCard(
    alert: NotificationAlertEntity,
    formattedTime: String,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead) CinemaSurfaceElevated else CinemaSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!alert.isRead) NeonCyan.copy(alpha = 0.5f) else CinemaSurfaceBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMarkRead() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = when (alert.alertType) {
                    "RELEASE_DAY" -> HotCoral.copy(alpha = 0.2f)
                    "TRAILER_DROP" -> CinemaGold.copy(alpha = 0.2f)
                    else -> NeonCyan.copy(alpha = 0.2f)
                },
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (alert.alertType) {
                            "RELEASE_DAY" -> Icons.Default.LocalFireDepartment
                            "TRAILER_DROP" -> Icons.Default.PlayArrow
                            else -> Icons.Default.Alarm
                        },
                        contentDescription = null,
                        tint = when (alert.alertType) {
                            "RELEASE_DAY" -> HotCoral
                            "TRAILER_DROP" -> CinemaGold
                            else -> NeonCyan
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alert.mediaTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = if (!alert.isRead) FontWeight.Bold else FontWeight.SemiBold
                    )

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = alert.alertMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
