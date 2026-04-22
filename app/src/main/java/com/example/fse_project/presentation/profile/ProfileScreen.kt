package com.example.fse_project.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fse_project.presentation.home.CheckPermission
import com.example.fse_project.presentation.home.MainViewModel


@Composable
fun ProfileScreen(
    viewModel: MainViewModel
) {
    val state by viewModel.state.collectAsState()

    val user = state.currentUser
    val reservations = state.usersReservations
    val currentReservation = state.currentReservation
    val showResCancelDialog = state.showResCancelDialog

    LaunchedEffect(currentReservation) {
        println("res: $currentReservation")
    }

    if (showResCancelDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.changeCancelDialogStatus() },
            title = {
                Text("Rezervasyon İptali")
            },
            text = {
                Text("Rezervasyonunuzu iptal etmek istediğinize emin misiniz?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReservation(currentReservation!!.id)

                    }
                ) {
                    Text("İptal Et")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        viewModel.changeCancelDialogStatus()
                    }
                ) {
                    Text("Vazgeç")
                }
            }

        )
    }

    LaunchedEffect(user) {
        println("$user")
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (reservations.isEmpty()) {
            Text("Boş bura")
        } else {
            LazyColumn {

                items(reservations) { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            // 🔹 İstasyon adı
                            Text(
                                text = res.station.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 🔹 Mesafe + Süre
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    //Text(
                                    //    routeDistance ?: "Konum aç",
                                    //    style = MaterialTheme.typography.bodySmall
                                    //)
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Text(
                                    //     routeDuration ?: "Konum aç",
                                    //     style = MaterialTheme.typography.bodySmall
                                    // )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 🔹 Charger bilgisi
                            Text(
                                text = "Şarj: ${res.charger.chargerType}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // 🔹 Saat bilgisi
                            Text(
                                text = "${res.startTime.toLocalTime()} - ${res.endTime.toLocalTime()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))


                            Button(
                                onClick = {
                                    viewModel.changeCancelDialogStatus()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Rezervasyonu İptal Et")
                            }
                        }
                    }
                }
            }
        }

    }
}
