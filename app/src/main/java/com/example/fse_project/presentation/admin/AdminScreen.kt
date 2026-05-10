package com.example.fse_project.presentation.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.StationStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel = hiltViewModel(), navController: NavController) {
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
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
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
            RevenueSection(state)
            HorizontalDivider()

            DensitySection(
                title = "Tüm Zamanların Saatlik Yoğunluğu",
                icon = Icons.Default.Timeline,
                data = state.peakHoursList,
                color = MaterialTheme.colorScheme.primary
            )
            DensitySection(
                title = "Mevcut (Şu Anki) Yoğunluk",
                icon = Icons.Default.EvStation,
                data = state.currentDensity,
                color = MaterialTheme.colorScheme.tertiary
            )
            HorizontalDivider()

            StationRevenuesSection(state.stationsAndRevenuesList)
            HorizontalDivider()

            StationPeakHoursSection(state.stationsAndPeakHoursList)
            HorizontalDivider()


        }
    }
}

@Composable
fun RevenueSection(state: UiState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionTitle(title = "Gelir Özeti", icon = Icons.Default.AttachMoney)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RevenueCard(modifier = Modifier.weight(1f), title = "Günlük", amount = state.dailyRevenue)
            RevenueCard(modifier = Modifier.weight(1f), title = "Haftalık", amount = state.weeklyRevenue)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RevenueCard(modifier = Modifier.weight(1f), title = "Aylık", amount = state.monthlyRevenue)
            RevenueCard(modifier = Modifier.weight(1f), title = "Yıllık", amount = state.annualRevenue)
        }
    }
}

@Composable
fun RevenueCard(modifier: Modifier = Modifier, title: String, amount: Double) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.2f ₺", amount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DensitySection(title: String, icon: ImageVector, data: Map<Int, Int>, color: Color) {
    Column {
        SectionTitle(title = title, icon = icon)
        Spacer(modifier = Modifier.height(16.dp))
        if (data.isEmpty()) {
            Text("Yeterli veri yok.", color = Color.Gray)
        } else {
            val fullData = (0..23).associateWith { data[it] ?: 0 }
            CustomBarChart(
                data = fullData,
                barColor = color,
                xAxisLabelFormatter = { "${it}:00" }
            )
        }
    }
}

@Composable
fun StationRevenuesSection(stationRevenues: List<StationRevenue>) {
    Column {
        SectionTitle(title = "İstasyon Gelirleri", icon = Icons.Default.AttachMoney)
        Spacer(modifier = Modifier.height(16.dp))
        if (stationRevenues.isEmpty()) {
            Text("Gelir verisi bulunamadı.", color = Color.Gray)
        } else {
            val chartData = stationRevenues.filter { it.revenue > 0 }
                .associate { it.station.id to it.revenue.toInt() }
            val nameMap = stationRevenues.filter { it.revenue > 0 }
                .associate { it.station.id to it.station.name }
            CustomBarChart(
                data = chartData,
                barColor = MaterialTheme.colorScheme.secondary,
                xAxisLabelFormatter = { id ->
                    (nameMap[id] ?: "ID:$id")
                        .replace(" Şarj İstasyonu", "")
                        .replace(" Charging Station", "")
                }
            )
        }
    }
}

@Composable
fun StationPeakHoursSection(stationPeakHoursList: List<StationPeakHours>) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column {
        SectionTitle(title = "İstasyon Bazlı Yoğunluk", icon = Icons.Default.Timeline)
        Spacer(modifier = Modifier.height(16.dp))
        if (stationPeakHoursList.isEmpty()) {
            Text("İstasyon yoğunluk verisi bulunamadı.", color = Color.Gray)
        } else {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                stationPeakHoursList.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = item.station.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val selectedData = stationPeakHoursList.getOrNull(selectedTabIndex)?.peakHours ?: emptyMap()
            val fullData = (0..23).associateWith { selectedData[it] ?: 0 }
            CustomBarChart(
                data = fullData,
                barColor = MaterialTheme.colorScheme.primary,
                xAxisLabelFormatter = { "${it}:00" }
            )
        }
    }
}



@Composable
fun StationDialog(
    station: Station,
    onDismiss: () -> Unit,
    changeStatus: (Boolean) -> Unit
) {
    val isOnline = station.status != StationStatus.OFFLINE

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "İstasyon Yönetimi", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = station.name,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isOnline) "Durum: Çevrimiçi" else "Durum: Çevrimdışı",
                        color = if (isOnline) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = isOnline,
                        onCheckedChange = changeStatus
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun <T> CustomBarChart(
    data: Map<T, Int>,
    barColor: Color,
    xAxisLabelFormatter: (T) -> String
) {
    val maxValue = data.values.maxOrNull()?.coerceAtLeast(1) ?: 1
    val chartHeight = 150.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (key, value) ->
            val heightFraction = value.toFloat() / maxValue.toFloat()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.width(68.dp)
            ) {
                if (value > 0) {
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Box(
                    modifier = Modifier
                        .height(chartHeight * heightFraction)
                        .width(32.dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(if (value > 0) barColor else Color.Transparent)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = xAxisLabelFormatter(key),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}