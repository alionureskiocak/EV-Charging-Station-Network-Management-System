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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fse_project.data.local.database.entities.ReservationStatus
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.presentation.home.MainViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
) {
    val state by viewModel.state.collectAsState()

    val isChargingNow = state.isChargingNow
    val user = state.currentUser
    val wallet = user?.wallet
    val reservations = state.usersReservations
    val currentReservation = state.currentReservation
    val showResCancelDialog = state.showResCancelDialog

    var showWalletDialog by remember { mutableStateOf(false) }

    if (showWalletDialog) {
        WalletDialog(
            onClick = { viewModel.updateWallet(wallet!!.balance + it) },
            onDismiss = { showWalletDialog = false }
        )
    }

    if (showResCancelDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            onDismissRequest = { viewModel.changeCancelDialogStatus() },
            title = { Text("Rezervasyon İptali") },
            text = { Text("Rezervasyonunuzu iptal etmek istediğinize emin misiniz? Bu işlem geri alınamaz.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteReservation(currentReservation!!.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("İptal Et")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.changeCancelDialogStatus() }) {
                    Text("Vazgeç")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Başlık
        Text(
            text = "Profilim",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Cüzdan Kartı
        wallet?.let {
            WalletCard(balance = it.balance) {
                showWalletDialog = true
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 Rezervasyonlar Listesi
        if (reservations.isEmpty()) {
            EmptyStateView()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Aktif Rezervasyon
                currentReservation?.let { activeRes ->
                    item {
                        Text(
                            text = "Aktif Rezervasyon",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                        ReservationCard(
                            res = activeRes,
                            isChargingNow = isChargingNow,
                            currentReservation = currentReservation
                        ) {
                            viewModel.changeCancelDialogStatus()
                        }
                    }
                }

                val otherRes = reservations.filter { it.status != ReservationStatus.ACTIVE }
                if (otherRes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Geçmiş Rezervasyonlar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                        )
                    }
                    items(otherRes) { res ->
                        ReservationCard(
                            res = res,
                            isChargingNow = isChargingNow,
                            currentReservation = currentReservation
                        ) {
                            viewModel.changeCancelDialogStatus()
                        }
                    }
                }
            }
        }

        // 🔹 Çıkış Yap Butonu
        OutlinedButton(
            onClick = { viewModel.logOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error))
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Çıkış", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Çıkış Yap")
        }
    }
}

@Composable
fun WalletCard(balance: Double, onTopUpClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "Cüzdan",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Mevcut Bakiye",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "₺$balance", // Para birimi sembolünü kendi projenize göre ayarlayabilirsiniz
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Button(
                onClick = onTopUpClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Yükle")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationCard(
    res: Reservation,
    isChargingNow: Boolean,
    currentReservation: Reservation?,
    onCancelClick: () -> Unit
) {
    // Statüye göre modern renk ve metin belirleme
    val (containerColor, statusColor, statusText) = when (res.status) {
        ReservationStatus.ACTIVE -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Aktif") // Soft Yeşil
        ReservationStatus.COMPLETED -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Tamamlandı")
        ReservationStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "İptal Edildi") // Soft Kırmızı
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Bilinmiyor")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🔹 Üst Kısım: İstasyon Adı ve Statü Etiketi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = res.station.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Statü Chip'i
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Bilgi Satırları
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip(icon = Icons.Default.ChargingStation, text = "Şarj: ${res.charger.chargerType}")
                InfoChip(icon = Icons.Default.AccessTime, text = "${res.startTime.toLocalTime()} - ${res.endTime.toLocalTime()}")
            }

            // 🔹 İptal Butonu (Sadece aktif rezervasyon ise ve şarj başlamadıysa)
            if (!isChargingNow && currentReservation == res && res.status == ReservationStatus.ACTIVE) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCancelClick,
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
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.05f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.EventBusy,
            contentDescription = "Boş",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Henüz bir rezervasyonunuz yok.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Yeni bir şarj istasyonu rezerve ederek başlayabilirsiniz.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun WalletDialog(onClick: (Long) -> Unit, onDismiss: () -> Unit) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bakiye Yükle", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Cüzdanınıza yüklemek istediğiniz tutarı giriniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Tutar (₺)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (amount.isNotEmpty()) {
                        onClick(amount.toLong())
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Yükle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}