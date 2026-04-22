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
import com.example.fse_project.UserMarker
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
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import co.yml.charts.common.extensions.isNotNull
import coil.compose.AsyncImage
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.R
import com.example.fse_project.presentation.navigation.BottomNavigationBarItem
import com.example.fse_project.presentation.navigation.Screen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Queue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    navController : NavHostController
) {
    val state by viewModel.state.collectAsState()

    val currentUser = state.currentUser
    val reservations = state.usersReservations
    val usersVehicles = state.usersVehicles
    val stations = state.allStations
    val timeSlots = if (state.restrictedTimeSlots == emptyList<TimeSlot>()) state.timeSlots else state.restrictedTimeSlots
    val station = state.currentStation
    val vehicle = state.currentVehicle
    val currentReservation = state.currentReservation
    val userLocation = state.userLocation

    val routePolyline = state.routePolyline
    val routeDistance = state.routeDistance
    val routeDuration = state.routeDuration
    val isLoadingRoute = state.isLoadingRoute

    val showResCancelDialog = state.showResCancelDialog

    LaunchedEffect(currentReservation) {
        println("res: $currentReservation")
    }

    var hasLocationPermission by remember { mutableStateOf(false) }
    CheckPermission {
        hasLocationPermission = it
    }

    if (showResCancelDialog){
        AlertDialog(
            onDismissRequest = {viewModel.changeCancelDialogStatus()},
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

    val chargerItems = remember(station, vehicle,currentReservation,reservations) {
        station?.chargers?.map { charger ->
            val clickable = vehicle != null &&
                    charger.connectorType == vehicle.connectorType &&
                    charger.chargerStatus != ChargerStatus.OFFLINE
                    && currentReservation == null

            val text = when {
                charger.chargerStatus == ChargerStatus.OFFLINE -> "Çevrimdışı"
                vehicle == null -> "Araç seç"
                charger.connectorType != vehicle.connectorType -> "Uyumsuz Soket"
                charger.chargerStatus == ChargerStatus.FULL -> "Dolu"
                charger.chargerStatus == ChargerStatus.OCCUPIED -> "İleri tarih için rezervasyon var"
                else -> "Uygun"
            }

            val color = when (charger.chargerStatus) {
                ChargerStatus.AVAILABLE -> Color(0xFF76FF03)
                ChargerStatus.OCCUPIED -> Color(0xFFFF9100)
                ChargerStatus.FULL -> Color.Yellow
                else -> Color.Red
            }

            ChargerItem(
                charger = charger,
                clickable = clickable,
                clickableText = text,
                statusColor = color
            )
        } ?: emptyList()
    }

    var currentStation = state.currentStation
    var showCarDialog by remember { mutableStateOf(false) }
    var showCarAddDialog by remember { mutableStateOf(false) }
    var isSheetOpen by remember { mutableStateOf(false) }


    if (isLoadingRoute) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }


    val izmir = LatLng(38.4237, 27.1428)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 10f)
    }

    val pathPoints = remember(routePolyline) {
        routePolyline?.let { PolyUtil.decode(it) } ?: emptyList()
    }

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



    val context = LocalContext.current

    LaunchedEffect(currentUser, usersVehicles) {
        if (currentUser != null && vehicle == null) {
            showCarDialog = true
        }
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
 //  fusedLocationClient.lastLocation
 //      .addOnSuccessListener { location ->
 //          if (location!=null && station!=null){
 //              val lat = location.latitude
 //              val long = location.longitude
 //              viewModel.setUserLocation(lat,long)
 //          }
 //      }
    //LaunchedEffect(fusedLocationClient.lastLocation) {
    //    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
    //        location?.let {
    //            viewModel.setUserLocation(it.latitude, it.longitude)
    //        }
    //    }
    //}

    LaunchedEffect(currentReservation?.id) {
        println(currentReservation?.id)
    }

    LaunchedEffect(Unit)  {
        if (hasLocationPermission){
            while (true) {
                fusedLocationClient.lastLocation.await()?.let {
                    viewModel.setUserLocation(it.latitude, it.longitude)
                }
                delay(5000)
            }
        }

    }

    if (showCarDialog) {
        CarDialog(
            usersVehicles = usersVehicles,
            onDismiss = { showCarDialog = false },
            onVehicleAdd = { showCarAddDialog = true },
            onVehicleSelect = {
                viewModel.setCurrentVehicle(it)
                showCarDialog = false
            },
        )
    }

    if (showCarAddDialog) {
        CarAddDialog(
            onDismiss = { showCarAddDialog = false },
            currentUserId = currentUser?.id,
            onCarAdd = {
                viewModel.addVehicle(it)
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
            }
        )
    }



    /*LaunchedEffect(currentReservation, userLocation) {
        if (currentReservation != null && userLocation!= null) {
            if (currentStation!= null){
                viewModel.fetchDirections(
                    userLocation.latitude,
                    userLocation.longitude,
                    currentStation.latitude,
                    currentStation.longitude
                )
            }

        }
    }*/

    LaunchedEffect(currentReservation, userLocation) {
        if (currentReservation != null && userLocation != null) {
            currentStation?.let {
                val destination = LatLng(it.latitude, it.longitude)
                val distanceMoved = userLocation.let { loc ->
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        loc.latitude, loc.longitude,
                        destination.latitude, destination.longitude,
                        results
                    )
                    results[0]
                }
                // Hedefe 50 metreden fazlaysa güncelle
                if (distanceMoved > 50f) {
                    viewModel.fetchDirections(
                        userLocation.latitude,
                        userLocation.longitude,
                        it.latitude,
                        it.longitude
                    )
                }
            }
        }
    }

    val properties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission
            )
        )
    }

    LaunchedEffect(routeDistance,routeDuration) {
        println("distance: $routeDistance duration: $routeDuration")
    }


    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentReservation!=null) {
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
                            text = currentReservation.station.name,
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
                                Text(
                                    routeDistance ?: "Konum aç",
                                    style = MaterialTheme.typography.bodySmall
                                )
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
                                Text(
                                    routeDuration ?: "Konum aç",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 🔹 Charger bilgisi
                        Text(
                            text = "Şarj: ${currentReservation.charger.chargerType}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // 🔹 Saat bilgisi
                        Text(
                            text = "${currentReservation.startTime.toLocalTime()} - ${currentReservation.endTime.toLocalTime()}",
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
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                cameraPositionState = cameraPositionState,
                properties = properties,
                onMapClick = { latLng ->
                    currentUser?.let {
                        println(currentUser.name)
                    }
                }
            ) {
                if (pathPoints.isNotEmpty()) {
                    Polyline(
                        points = pathPoints,
                        color = Color(0xFF2196F3),
                        width = 12f,
                        geodesic = true
                    )
                }




                if (pathPoints.isNotEmpty()) {
                    Polyline(
                        points = pathPoints,
                        color = Color(0xFF2196F3),
                        width = 12f,
                        geodesic = true
                    )
                }

                stations.forEach { station ->
                    val color = if (vehicle != null) {
                        val compatibleChargers = station.chargers.filter { it.connectorType == vehicle.connectorType }
                        when {
                            compatibleChargers.isEmpty() || compatibleChargers.all { it.chargerStatus == ChargerStatus.OFFLINE } -> BitmapDescriptorFactory.HUE_RED
                            compatibleChargers.any { it.chargerStatus == ChargerStatus.AVAILABLE } -> BitmapDescriptorFactory.HUE_GREEN
                            compatibleChargers.all { it.chargerStatus == ChargerStatus.FULL } -> BitmapDescriptorFactory.HUE_YELLOW
                            else -> BitmapDescriptorFactory.HUE_ORANGE
                        }
                    } else {
                        when (station.status) {
                            StationStatus.AVAILABLE -> BitmapDescriptorFactory.HUE_GREEN
                            StationStatus.OCCUPIED -> BitmapDescriptorFactory.HUE_ORANGE
                            StationStatus.FULL -> BitmapDescriptorFactory.HUE_YELLOW
                            else -> BitmapDescriptorFactory.HUE_RED
                        }
                    }
                    Marker(
                        state = MarkerState(LatLng(station.latitude, station.longitude)),
                        title = station.name,
                        icon = BitmapDescriptorFactory.defaultMarker(color),
                        onClick = {
                            viewModel.setCurrentStation(station.id)
                            isSheetOpen = true
                            true
                        }
                    )
                }
            }

            Box {
                var showChargersForAnimation by remember { mutableStateOf(true) }
                val sheetState = rememberModalBottomSheetState()

                if (isSheetOpen) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            isSheetOpen = false
                            showChargersForAnimation = true
                            viewModel.clearSelectedTimes()
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxHeight(0.45f)) {
                            AnimatedContent(
                                targetState = showChargersForAnimation,
                                transitionSpec = {
                                    slideInHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            easing = FastOutSlowInEasing
                                        ),
                                        initialOffsetX = { if (targetState) -600 else 600 }
                                    ) + fadeIn() togetherWith slideOutHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            easing = FastOutSlowInEasing
                                        ),
                                        targetOffsetX = { if (targetState) 600 else -600 }
                                    ) + fadeOut()
                                }
                            ) {
                                if (it) {
                                    ChargerChoiceScreen(
                                        chargers = chargerItems,
                                        onChargerClick = { chargerId ->
                                            viewModel.setCurrentCharger(chargerId)
                                            viewModel.getReservationTimeSlots(chargerId = chargerId)
                                            showChargersForAnimation = false
                                        },
                                        station = currentStation!!
                                    )
                                } else {
                                    ChargerTimeSlotsScreen(
                                        timeSlots = timeSlots,
                                        selectedStartIndex = state.selectedStartIndex,
                                        selectedEndIndex = state.selectedEndIndex,
                                        onTimeSlotSelected = { viewModel.selectTimeSlot(it) },
                                        onReservationConfirm = { str, end ->
                                            viewModel.createReservation()
                                            //viewModel.fetchDirections()
                                        }
                                    )
                                }
                            }
                        }
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
    station: Station,
    onChargerClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = station.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(chargers) { item ->
                val alpha = if (item.clickable) 1f else 0.4f
                ElevatedCard(
                    onClick = { onChargerClick(item.charger.id) },
                    enabled = item.clickable,
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alpha)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = item.charger.chargerName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.charger.connectorType.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = item.charger.powerOutput.name.replace("KW_", "") + " kW",
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
    }
}

@Composable
fun ChargerTimeSlotsScreen(
    timeSlots: List<TimeSlot>,
    selectedStartIndex: Int?,
    selectedEndIndex: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    onReservationConfirm: (Int, Int) -> Unit
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

@Composable
fun InfoChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    MapsInitializer.initialize(context)
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}