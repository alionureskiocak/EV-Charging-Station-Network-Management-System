package com.example.fse_project.presentation.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.ReportError
import com.example.fse_project.presentation.home.MainViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
) {
    val state by viewModel.state.collectAsState()

    val user = state.currentUser
    val wallet = user?.wallet
    val reservations = state.usersReservations
    val reports = state.reports
    val allStations = state.allStations
    val currentReservation = state.currentReservation
    val isChargingNow = state.isChargingNow
    val showResCancelDialog = state.showResCancelDialog

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Rezervasyonlar", "Raporlarım")
    var showWalletDialog by remember { mutableStateOf(false) }

    // --- Dialogs ---
    if (showWalletDialog) {
        WalletDialog(
            onClick = { viewModel.updateWallet(wallet!!.balance + it) },
            onDismiss = { showWalletDialog = false }
        )
    }

    if (showResCancelDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            onDismissRequest = { viewModel.changeCancelDialogStatus() },
            title = { Text("Rezervasyon İptali") },
            text = { Text("Rezervasyonunuzu iptal etmek istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        currentReservation?.let { viewModel.deleteReservation(it.id) }
                        viewModel.changeCancelDialogStatus()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("İptal Et") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.changeCancelDialogStatus() }) { Text("Vazgeç") }
            }
        )
    }

    // --- Main Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Profilim",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        wallet?.let {
            WalletCard(balance = it.balance) { showWalletDialog = true }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Tab Selector
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // 🔹 Content Area
        Box(modifier = Modifier.weight(1f)) {
            if (selectedTabIndex == 0) {
                if (reservations.isEmpty()) {
                    EmptyStateView(modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        currentReservation?.let { activeRes ->
                            item {
                                SectionTitle("Aktif Rezervasyon")
                                ReservationCard(activeRes, isChargingNow, currentReservation) {
                                    viewModel.changeCancelDialogStatus()
                                }
                            }
                        }

                        val pastRes = reservations.filter { it.status != ReservationStatus.ACTIVE }.reversed()
                        if (pastRes.isNotEmpty()) {
                            item { SectionTitle("Geçmiş Rezervasyonlar") }
                            items(pastRes) { res ->
                                ReservationCard(res, isChargingNow, currentReservation) {
                                    viewModel.changeCancelDialogStatus()
                                }
                            }
                        }
                    }
                }
            } else {
                if (reports.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Report, null, Modifier.size(64.dp), MaterialTheme.colorScheme.outline)
                        Text("Henüz bir sorun bildirmediniz.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(reports.reversed()) { report ->
                            val stationName = allStations.find { it.id == report.stationId }?.name ?: "Bilinmeyen İstasyon"
                            ReportCard(report, stationName)
                        }
                    }
                }
            }
        }

        // 🔹 Logout Button
        OutlinedButton(
            onClick = { viewModel.logOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(MaterialTheme.colorScheme.error))
        ) {
            Icon(Icons.Default.Logout, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Çıkış Yap")
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ReportCard(report: ReportError, stationName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Başlık ve Hata Türü Etiketi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = report.report.text,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (report.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = report.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun WalletCard(balance: Double, onTopUpClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Mevcut Bakiye", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                Text("₺${"%.2f".format(balance)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onTopUpClick, shape = RoundedCornerShape(12.dp)) { Text("Yükle") }
        }
    }
}

@Composable
fun ReservationCard(res: Reservation, isChargingNow: Boolean, currentRes: Reservation?, onCancel: () -> Unit) {
    val (containerColor, statusColor, statusText) = when (res.status) {
        ReservationStatus.ACTIVE -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Aktif")
        ReservationStatus.COMPLETED -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Tamamlandı")
        ReservationStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "İptal Edildi")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Bilinmiyor")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(res.station.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(Modifier.clip(RoundedCornerShape(8.dp)).background(statusColor.copy(alpha = 0.1f)).padding(8.dp, 4.dp)) {
                    Text(statusText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = statusColor)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(Icons.Default.ChargingStation, res.charger.chargerName)
                val timeFmt = DateTimeFormatter.ofPattern("HH:mm")
                InfoChip(Icons.Default.AccessTime, "${res.startTime.format(timeFmt)} - ${res.endTime.format(timeFmt)}")
            }
            if (res.status == ReservationStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(Icons.Default.Bolt, "${"%.2f".format(res.actualKwh)} kWh", MaterialTheme.colorScheme.primary.copy(0.1f), MaterialTheme.colorScheme.primary)
                    InfoChip(Icons.Default.Payments, "₺${"%.2f".format(res.totalAmount)}", MaterialTheme.colorScheme.secondary.copy(0.1f), MaterialTheme.colorScheme.secondary)
                }
            }
            if (!isChargingNow && currentRes?.id == res.id && res.status == ReservationStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                ) {
                    Text("Rezervasyonu İptal Et", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun InfoChip(icon: ImageVector, text: String, containerColor: Color = Color.Black.copy(0.05f), contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(containerColor).padding(8.dp, 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(16.dp), tint = contentColor)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.EventBusy, null, Modifier.size(80.dp), MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Henüz bir rezervasyonunuz yok.", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun WalletDialog(onClick: (Long) -> Unit, onDismiss: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bakiye Yükle", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.all { c -> c.isDigit() }) amount = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Tutar (₺)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(onClick = { if (amount.isNotEmpty()) { onClick(amount.toLong()); onDismiss() } }) { Text("Yükle") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Vazgeç") }
        }
    )
}