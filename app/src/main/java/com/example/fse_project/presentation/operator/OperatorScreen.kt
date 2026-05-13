package com.example.fse_project.presentation.operator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fse_project.domain.model.ReportError
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.StationStatus
import com.example.fse_project.presentation.admin.AdminViewModel
import com.example.fse_project.presentation.admin.SectionTitle
import com.example.fse_project.presentation.admin.StationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()

    var showStationDialog by remember { mutableStateOf(false) }
    var currentStationId by remember { mutableStateOf<Long?>(null) }
    val updatedStation = state.allStations.find { it.id == currentStationId }

    LaunchedEffect(updatedStation) {
        if (updatedStation == null) showStationDialog = false
    }

    if (showStationDialog && updatedStation != null) {
        StationDialog(
            station = updatedStation,
            onDismiss = { showStationDialog = false },
            changeStatus = { goOnline ->
                if (goOnline) viewModel.takeStationOnline(updatedStation)
                else viewModel.takeStationOffline(updatedStation)
                showStationDialog = false
            }
        )
    }

    BackHandler {
        viewModel.logOut()
        navController.navigate("auth") { popUpTo(0) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Operatör Paneli", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        viewModel.logOut()
                        navController.navigate("auth") { popUpTo(0) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Çıkış Yap", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OperatorStationListSection(
                stations = state.allStations,
                totalCompleted = state.completedReservations.size,
                onManageClick = { station ->
                    currentStationId = station.id
                    showStationDialog = true
                }
            )

            HorizontalDivider()

            OperatorReportsSection(reports = state.reports)
        }
    }
}

@Composable
fun OperatorStationListSection(
    stations: List<Station>,
    totalCompleted: Int,
    onManageClick: (Station) -> Unit
) {
    Column {
        SectionTitle(title = "Yönetilen İstasyonlar", icon = Icons.Default.EvStation)
        Text(
            text = "Yönetilen toplam ${stations.size} istasyon " +
                    "ve tamamlanmış $totalCompleted rezervasyon var.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(stations) { station ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = station.name, fontWeight = FontWeight.Bold)
                            Text(
                                text = when (station.status) {
                                    StationStatus.AVAILABLE -> "Çevrimiçi – Müsait"
                                    StationStatus.OCCUPIED  -> "Çevrimiçi – Meşgul"
                                    StationStatus.FULL      -> "Çevrimiçi – Dolu"
                                    StationStatus.OFFLINE   -> "Çevrimdışı"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = when (station.status) {
                                    StationStatus.OFFLINE -> MaterialTheme.colorScheme.error
                                    else                  -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                        OutlinedButton(onClick = { onManageClick(station) }) {
                            Text("Yönet")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun OperatorReportsSection(reports: List<ReportError>) {
    Column {
        SectionTitle(title = "Kullanıcı Raporları", icon = Icons.Default.Report)
        Spacer(modifier = Modifier.height(8.dp))

        if (reports.isEmpty()) {
            Text("Henüz rapor yok.", color = Color.Gray)
        } else {
            reports.forEach { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = report.stationName.ifBlank { "İstasyon ID: ${report.stationId}" },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = report.report.text,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                            if (report.description.isNotBlank()) {
                                Text(
                                    text = report.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Raporlayan: ${report.userName.ifBlank { "Kullanıcı #${report.userId}" }} · ${
                                    java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                                        .format(java.util.Date(report.createdAt))
                                }",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}