package com.example.fse_project.presentation.home

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fse_project.domain.model.StationStatus
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.location.Location
import android.os.Build
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChargingStation
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.R
import com.example.fse_project.domain.model.Reservation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()

    val currentUser = state.currentUser
    val reservations = state.usersReservations
    val usersVehicles = state.usersVehicles
    val stations = state.allStations
    val timeSlots = if (state.restrictedTimeSlots.isEmpty()) state.timeSlots else state.restrictedTimeSlots
    val station = state.currentStation
    val vehicle = state.currentVehicle
    val currentReservation = state.currentReservation
    val userLocation = state.userLocation

    val charger = state.currentCharger
    val routePolyline = state.routePolyline
    val routeDistance = state.routeDistance
    val routeDuration = state.routeDuration
    val isLoadingRoute = state.isLoadingRoute
    val showResCancelDialog = state.showResCancelDialog

    val timer = viewModel.timerFlow.collectAsState()

    var hasLocationPermission by remember { mutableStateOf(false) }
    CheckPermission { hasLocationPermission = it }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var isSheetOpen by remember { mutableStateOf(false) }
    var showCarDialog by remember { mutableStateOf(false) }
    var showCarAddDialog by remember { mutableStateOf(false) }

    val izmir = LatLng(38.4237, 27.1428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 10f)
    }

    val pathPoints = remember(routePolyline) {
        routePolyline?.let { PolyUtil.decode(it) } ?: emptyList()
    }

    // Harita animasyonu
    LaunchedEffect(pathPoints) {
        if (pathPoints.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            pathPoints.forEach { builder.include(it) }
            val bounds = builder.build()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(state.currentStation) {
        state.currentStation?.chargers?.forEach {
           println("${it.chargerName}: ${it.chargerStatus}")
        }
    }

    // Kullanıcı ilk girdiğinde araç seçimi
    LaunchedEffect(currentUser, usersVehicles) {
        if (currentUser != null && vehicle == null) {
            showCarDialog = true
        }
    }

    // Konum Güncelleme Döngüsü
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            while (true) {
                fusedLocationClient.lastLocation.await()?.let {
                    viewModel.setUserLocation(it.latitude, it.longitude)
                }
                delay(5000)
            }
        }
    }

    // Rota Hesaplama Tetikleyicisi
    LaunchedEffect(currentReservation, userLocation) {
        if (currentReservation != null && userLocation != null) {
            station?.let {
                val destination = LatLng(it.latitude, it.longitude)
                val distanceMoved = FloatArray(1).apply {
                    Location.distanceBetween(
                        userLocation.latitude, userLocation.longitude,
                        destination.latitude, destination.longitude,
                        this
                    )
                }[0]
                if (distanceMoved > 50f) {
                    viewModel.fetchDirections(
                        userLocation.latitude, userLocation.longitude,
                        it.latitude, it.longitude
                    )
                }
            }
        }
    }

    val properties by remember(hasLocationPermission) {
        mutableStateOf(MapProperties(isMyLocationEnabled = hasLocationPermission))
    }

    // İptal / Durdurma Dialogu
    if (showResCancelDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            onDismissRequest = { viewModel.changeCancelDialogStatus() },
            title = { Text(if (timer.value == 0) "Rezervasyon İptali" else "Şarjı Bitirme İşlemi") },
            text = { Text(if (timer.value == 0) "Rezervasyonunuzu iptal etmek istediğinize emin misiniz?" else "Şarj işlemini bitirmek istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (timer.value == 0) viewModel.deleteReservation(currentReservation!!.id)
                        else viewModel.completeReservation()
                        viewModel.changeCancelDialogStatus()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(if (timer.value == 0) "İptal Et" else "Durdur") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.changeCancelDialogStatus() }) { Text("Vazgeç") }
            }
        )
    }

    // Araç Dialogları
    if (showCarDialog) {
        CarDialog(
            usersVehicles = usersVehicles,
            onDismiss = { showCarDialog = false },
            onVehicleAdd = { showCarAddDialog = true },
            onVehicleSelect = {
                viewModel.setCurrentVehicle(it)
                showCarDialog = false
            }
        )
    }

    if (showCarAddDialog) {
        CarAddDialog(
            currentUserId = currentUser?.id,
            onDismiss = { showCarAddDialog = false },
            onCarAdd = {
                viewModel.addVehicle(it)
                Toast.makeText(context, "Araç eklendi", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // ANA EKRAN ÇİZİMİ
    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // 1. KATMAN: HARİTA
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = properties,
                contentPadding = paddingValues,
                onMapClick = { /* Boşluk tıklandığında */ }
            ) {
                if (pathPoints.isNotEmpty()) {
                    Polyline(
                        points = pathPoints,
                        color = MaterialTheme.colorScheme.primary,
                        width = 14f,
                        geodesic = true
                    )
                }

                stations.forEach { st ->
                    val color = if (vehicle != null) {
                        val compatible = st.chargers.filter { it.connectorType == vehicle.connectorType }
                        when {
                            compatible.isEmpty() || compatible.all { it.chargerStatus == ChargerStatus.OFFLINE } -> BitmapDescriptorFactory.HUE_RED
                            compatible.any { it.chargerStatus == ChargerStatus.AVAILABLE } -> BitmapDescriptorFactory.HUE_GREEN
                            compatible.all { it.chargerStatus == ChargerStatus.FULL } -> BitmapDescriptorFactory.HUE_YELLOW
                            else -> BitmapDescriptorFactory.HUE_ORANGE
                        }
                    } else {
                        when (st.status) {
                            StationStatus.AVAILABLE -> BitmapDescriptorFactory.HUE_GREEN
                            StationStatus.OCCUPIED -> BitmapDescriptorFactory.HUE_ORANGE
                            StationStatus.FULL -> BitmapDescriptorFactory.HUE_YELLOW
                            else -> BitmapDescriptorFactory.HUE_RED
                        }
                    }
                    Marker(
                        state = MarkerState(LatLng(st.latitude, st.longitude)),
                        title = st.name,
                        icon = BitmapDescriptorFactory.defaultMarker(color),
                        onClick = {
                            viewModel.setCurrentStation(st.id)
                            isSheetOpen = true
                            true
                        }
                    )
                }
            }

            // 2. KATMAN: FLOATING (YÜZEN) ARAYÜZ ELEMANLARI
            if (isLoadingRoute) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                        .padding(24.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            currentReservation?.let { reservation ->
                ActiveReservationCard(
                    currentReservation = reservation,
                    timerValue = timer.value,
                    routeDistance = routeDistance,
                    routeDuration = routeDuration,
                    vehicle = vehicle,
                    onCancelOrStopClick = { viewModel.changeCancelDialogStatus() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .align(Alignment.TopCenter)
                )
            }
        }
    }

    // 3. KATMAN: BOTTOM SHEET
    if (isSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var showChargersForAnimation by remember { mutableStateOf(true) }

        val chargerItems = remember(station, vehicle, currentReservation, reservations) {
            station?.chargers?.map { ch ->
                val clickable = vehicle != null && ch.connectorType == vehicle.connectorType && ch.chargerStatus != ChargerStatus.OFFLINE && currentReservation == null

                // Metin ve Renk atamasını aynı anda yapıyoruz ki birbiriyle çelişmesin
                val (text, color) = when {
                    ch.chargerStatus == ChargerStatus.OFFLINE -> "Çevrimdışı" to Color.Gray
                    vehicle == null -> "Araç seç" to Color.Gray
                    ch.connectorType != vehicle.connectorType -> "Uyumsuz Soket" to Color.Gray
                    ch.chargerStatus == ChargerStatus.OCCUPIED -> "İleri tarihli rezerve" to Color(0xFFFF9800)
                    ch.chargerStatus == ChargerStatus.FULL -> "Dolu" to Color(0xFFF44336) // Kırmızı
                    else -> "Uygun" to Color(0xFF4CAF50) // Yeşil
                }

                ChargerItem(ch, clickable, text, color)
            } ?: emptyList()
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                isSheetOpen = false
                showChargersForAnimation = true
                viewModel.clearSelectedTimes()
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Box(modifier = Modifier.fillMaxHeight(0.55f)) {
                AnimatedContent(
                    targetState = showChargersForAnimation,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = tween(400, easing = FastOutSlowInEasing), initialOffsetX = { if (targetState) -it else it }) + fadeIn() togetherWith
                                slideOutHorizontally(animationSpec = tween(400, easing = FastOutSlowInEasing), targetOffsetX = { if (targetState) it else -it }) + fadeOut()
                    }, label = "BottomSheetAnimation"
                ) { isChargerScreen ->
                    if (isChargerScreen) {
                        ChargerChoiceScreen(
                            chargers = chargerItems,
                            station = station,
                            vehicle = vehicle,
                            usersVehicles = usersVehicles,
                            onChargerClick = { chargerId ->
                                viewModel.setCurrentCharger(chargerId)
                                viewModel.getReservationTimeSlots(chargerId = chargerId)
                                showChargersForAnimation = false
                            },
                            onVehicleAdd = { showCarAddDialog = true },
                            onVehicleSelect = {
                                viewModel.setCurrentVehicle(it)
                                showCarDialog = false
                            }
                        )
                    } else {
                        ChargerTimeSlotsScreen(
                            timeSlots = timeSlots,
                            selectedStartIndex = state.selectedStartIndex,
                            selectedEndIndex = state.selectedEndIndex,
                            onTimeSlotSelected = { viewModel.selectTimeSlot(it) },
                            onReservationConfirm = { _, _ ->
                                viewModel.createReservation()
                                isSheetOpen = false
                            }
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargerChoiceScreen(
    chargers: List<ChargerItem>,
    station: Station?,
    vehicle: Vehicle?,
    usersVehicles: List<Vehicle>,
    onChargerClick: (Long) -> Unit,
    onVehicleAdd: () -> Unit,
    onVehicleSelect: (Vehicle) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            station?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (vehicle != null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chargers) { item ->
                    val alpha = if (item.clickable) 1f else 0.5f
                    ElevatedCard(
                        onClick = { onChargerClick(item.charger.id) },
                        enabled = item.clickable,
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (item.clickable) 4.dp else 0.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (item.clickable) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth().alpha(alpha)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(item.statusColor)
                                )
                            }

                            Image(
                                painter = painterResource(R.drawable.charger),
                                contentDescription = null,
                                modifier = Modifier.size(56.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = item.charger.chargerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.charger.connectorType.name.replace("_"," "),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "${item.charger.powerOutput.name.replace("KW_", "")} kW",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.clickableText!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = item.statusColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Rezervasyon için araba seçmeniz gerekmektedir. Arabalarınızdan birini seçebilir veya yeni araba oluşturabilirsiniz.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (usersVehicles.isNotEmpty()) {
                    usersVehicles.forEach { v ->
                        OutlinedButton(
                            onClick = { onVehicleSelect(v) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text(
                                text = "${v.brand} ${v.model}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                FilledTonalButton(
                    onClick = onVehicleAdd,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Yeni Araba Ekle")
                }
            }
        }
    }
}@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
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
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun ChargerTimeSlotsScreen(
    timeSlots: List<TimeSlot>,
    selectedStartIndex: Int?,
    selectedEndIndex: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    onReservationConfirm: (Int, Int) -> Unit,

    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(timeSlots) { timeSlot ->
                val isSelected = selectedStartIndex != null && (
                        timeSlot.index == selectedStartIndex ||
                                (selectedEndIndex != null && timeSlot.index in selectedStartIndex..selectedEndIndex)
                        )

                val containerColor = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    timeSlot.isAvailable -> MaterialTheme.colorScheme.surface
                    else -> Color.LightGray
                }

                val contentColor = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    timeSlot.isAvailable -> MaterialTheme.colorScheme.primary
                    else -> Color.Gray
                }

                OutlinedButton(
                    onClick = { onTimeSlotSelected(timeSlot.index) },
                    enabled = timeSlot.isAvailable,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                        disabledContainerColor = Color(0xFFF5F5F5),
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = timeSlot.timeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }


        Button(
            onClick = {
                if (selectedStartIndex != null) {
                    val finalEndIndex = selectedEndIndex ?: selectedStartIndex
                    onReservationConfirm(selectedStartIndex, finalEndIndex)
                }
            },
            enabled = selectedStartIndex != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Rezervasyonu Onayla")
        }
    }
}

@Composable
fun CarDialog(
    usersVehicles: List<Vehicle>,
    onDismiss: () -> Unit,
    onVehicleAdd: () -> Unit,
    onVehicleSelect: (Vehicle) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Araç Seçimi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Rezervasyon için araba seçmeniz gerekmektedir. Arabalarınızdan birini seçebilir veya yeni araba oluşturabilirsiniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (usersVehicles.isNotEmpty()) {
                        usersVehicles.forEach { vehicle ->
                            OutlinedButton(
                                onClick = { onVehicleSelect(vehicle) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text(
                                    text = "${vehicle.brand} ${vehicle.model}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = onVehicleAdd,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Yeni Araba Ekle")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarAddDialog(
    currentUserId: Long?,
    onDismiss: () -> Unit,
    onCarAdd: (Vehicle) -> Unit
) {
    var brandText by remember { mutableStateOf("") }
    var modelText by remember { mutableStateOf("") }
    var capacityText by remember { mutableStateOf("") }
    var licensePlateText by remember { mutableStateOf("") }
    val items = listOf(ConnectorType.TYPE_2, ConnectorType.CCS, ConnectorType.CHADEMO)
    var selectedConnector by remember { mutableStateOf(items[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Yeni Araç Ekle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = brandText,
                    onValueChange = { brandText = it },
                    label = { Text("Marka",color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = modelText,
                    onValueChange = { modelText = it },
                    label = { Text("Model",color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    label = { Text("Batarya Kapasitesi (kWh)",color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = licensePlateText,
                    onValueChange = { licensePlateText = it },
                    label = { Text("Plaka",color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedConnector.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Soket Tipi",color = Color.LightGray) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        items.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    selectedConnector = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (brandText.isNotBlank() && modelText.isNotBlank() &&
                        capacityText.isNotBlank() && licensePlateText.isNotBlank()
                    ) {
                        currentUserId?.let {
                            val vehicle = Vehicle(
                                id = 0,
                                ownerId = it,
                                brand = brandText,
                                model = modelText,
                                capacity = capacityText.toIntOrNull() ?: 0,
                                connectorType = selectedConnector,
                                licensePlate = licensePlateText,
                            )
                            onCarAdd(vehicle)
                            onDismiss()
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun CheckPermission(onPermissionGranted: (Boolean) -> Unit) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    LaunchedEffect(hasLocationPermission) {
        onPermissionGranted(hasLocationPermission)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActiveReservationCard(
    currentReservation: Reservation,
    timerValue: Int,
    routeDistance: String?,
    routeDuration: String?,
    vehicle: Vehicle?,
    onCancelOrStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCharging = timerValue > 0

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCharging) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Başlık ve Statü
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentReservation.station.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCharging) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )
                if (isCharging) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Şarj Oluyor",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bilgi Çipleri
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!isCharging) {
                    InfoChip(Icons.Default.LocationOn, routeDistance ?: "Hesaplanıyor..")
                    InfoChip(Icons.Default.AccessTime, routeDuration ?: "--")
                } else {
                    vehicle?.let {
                        InfoChip(Icons.Default.ElectricCar, "${it.brand} ${it.model}")
                        InfoChip(Icons.Default.ChargingStation,
                            it.connectorType.name.replace("_"," ")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detaylar ve Dashboard
            if (!isCharging) {
                Text(
                    text = "Şarj Hızı: ${currentReservation.charger.chargerType}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${currentReservation.startTime.toLocalTime()} - ${currentReservation.endTime.toLocalTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Geçen Süre", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${timerValue / 60}dk ${timerValue % 60}sn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Box(modifier = Modifier.height(40.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Toplam Tutar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₺${timerValue / 60 * currentReservation.pricePerKwh}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCancelOrStopClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = if (isCharging) "Şarj İşlemini Durdur" else "Rezervasyonu İptal Et",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}