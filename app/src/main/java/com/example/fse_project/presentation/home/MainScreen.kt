package com.example.fse_project.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChargingStation
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fse_project.R
import com.example.fse_project.data.local.database.entities.ChargerStatus
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.data.local.database.entities.Report
import com.example.fse_project.domain.model.Reservation
import com.example.fse_project.domain.model.Station
import com.example.fse_project.domain.model.StationStatus
import com.example.fse_project.domain.model.Vehicle
import com.example.fse_project.presentation.navigation.Screen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
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
    val isStationsFavorite = state.isStationsFavorite
    val stations = state.displayedStations
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
    val consumedKwh = state.currentKwh
    val showReceipt = state.showReceipt

    var showReportDialog by remember { mutableStateOf(false) }

    val closestAvailableStations = state.closestAvailableStations

    LaunchedEffect(closestAvailableStations) {
        println(closestAvailableStations)
    }

    val timer = viewModel.timerFlow.collectAsState()

    var hasLocationPermission by remember { mutableStateOf(false) }
    CheckPermission { hasLocationPermission = it }

    LaunchedEffect(stations) {
        println("stations: $stations")
    }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var isSheetOpen by remember { mutableStateOf(false) }
    var showCarDialog by remember { mutableStateOf(false) }
    var showCarAddDialog by remember { mutableStateOf(false) }
    var isFilterMenuOpen by remember { mutableStateOf(false) }

    var isNearbyListOpen by remember { mutableStateOf(false) }

    var searchText by remember { mutableStateOf("") }

    val izmir = LatLng(38.4237, 27.1428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 10f)
    }

    val pathPoints = remember(routePolyline) {
        routePolyline?.let { PolyUtil.decode(it) } ?: emptyList()
    }

    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
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
        if (currentReservation == null) viewModel.clearRoute()
    }

    // Harita ayarları
    val properties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                // mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
            )
        )
    }

    if (state.showToast){
        Toast.makeText(context,state.toastMsg,Toast.LENGTH_SHORT).show()
        viewModel.closeToast()
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
                TextButton(onClick = {
                    viewModel.changeCancelDialogStatus()
                    //
                }) { Text("Vazgeç") }
            }
        )
    }

    if (showReportDialog){
        StationReportDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { report , description ->
            viewModel.reportStation(report,description)
                showReportDialog = false
                viewModel.showToast("İstasyon Rapor Edildi.")
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
                uiSettings = uiSettings,
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 80.dp,
                    bottom = paddingValues.calculateBottomPadding() + 80.dp
                ),
                onMapClick = { isFilterMenuOpen = false } // Haritaya tıklanınca filtre menüsünü kapat
            ) {
                if (pathPoints.isNotEmpty()) {
                    Polyline(
                        points = pathPoints,
                        color = MaterialTheme.colorScheme.primary,
                        width = 14f,
                        geodesic = true
                    )
                }

                if (state.showReceipt && state.lastCompletedReservation != null) {
                    ReceiptDialog(
                        reservation = state.lastCompletedReservation!!,
                        onClose = { viewModel.closeReceiptScreen() }
                    )
                }

                stations.forEach { st ->
                    val hue = if (vehicle != null) {
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
                        icon = BitmapDescriptorFactory.defaultMarker(hue),
                        onClick = {
                            viewModel.setCurrentStation(st.id)
                            isSheetOpen = true
                            true
                        }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(bottom = 100.dp) // Alttaki filtre butonlarına çarpmaması için
            ) {
                NearbyStationsPanel(
                    isVisible = isNearbyListOpen,
                    stations = state.closestAvailableStations, // 1. adımda oluşturduğumuz sıralı liste
                    onToggle = { isNearbyListOpen = !isNearbyListOpen },
                    onStationClick = { stationId ->
                        viewModel.setCurrentStation(stationId)
                        isSheetOpen = true
                        isNearbyListOpen = false
                    }
                )
            }
            // 2. KATMAN: YÜZEN ARAYÜZ (FLOATING UI) ELEMANLARI
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

            // 🔹 ÜST KISIM: Arama Çubuğu veya Rezervasyon Kartı
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding() + 16.dp)
                    .padding(horizontal = 16.dp)
            ) {
                if (currentReservation != null) {
                    ActiveReservationCard(
                        currentReservation = currentReservation,
                        timerValue = timer.value,
                        routeDistance = routeDistance,
                        routeDuration = routeDuration,
                        vehicle = vehicle,
                        onCancelOrStopClick = { viewModel.changeCancelDialogStatus() },
                        consumedKwh = consumedKwh,

                    )
                } else {
                    FloatingSearchBar(
                        searchText = searchText,
                        onSearchTextChanged = {
                            searchText = it
                            viewModel.onStationSearch(searchText)
                        },
                        onProfileClick = { navController.navigate(Screen.ProfileScreen.route) }
                    )
                }
            }


            // 🔹 ALT ORTA KISIM: Favoriler ve Filtre Menüsü
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = paddingValues.calculateBottomPadding() + 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Yukarı doğru açılan filtre barı
                AnimatedVisibility(
                    visible = isFilterMenuOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilterOptionsBar(
                            onFilterSelect = { filter ->
                                // TODO: Seçilen filtreyi ViewModel'e gönder
                                viewModel.onFilterSelected(filter)
                            },
                            selectedFilter = state.selectedFilter
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                FloatingMapActions(
                    onFavoritesClick = { viewModel.onFavoritesClick() },
                    onFilterClick = { isFilterMenuOpen = !isFilterMenuOpen },
                    isStationsFavorite = isStationsFavorite
                )
            }
        }
    }

    // 3. KATMAN: BOTTOM SHEET
    if (isSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        var showChargersForAnimation by remember { mutableStateOf(true) }

        val isFavorite = viewModel.isStationFavorite(station!!.id)

        val chargerItems = remember(station, vehicle, currentReservation, reservations) {
            station?.chargers?.map { ch ->
                val clickable = vehicle != null && ch.connectorType == vehicle.connectorType && ch.chargerStatus != ChargerStatus.OFFLINE && currentReservation == null

                val (text, color) = when {
                    ch.chargerStatus == ChargerStatus.OFFLINE -> "Çevrimdışı" to Color.Gray
                    vehicle == null -> "Araç seç" to Color.Gray
                    ch.connectorType != vehicle.connectorType -> "Uyumsuz Soket" to Color.Gray
                    ch.chargerStatus == ChargerStatus.FULL -> "Dolu" to Color(0xFFF44336)
                    ch.chargerStatus == ChargerStatus.OCCUPIED -> "İleri tarihli rezerve" to Color(0xFFFF9800)
                    else -> "Uygun" to Color(0xFF4CAF50)
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
                            isFavorite = isFavorite,
                            onFavoriteToggle = {
                                viewModel.toggleFavorite()
                            },
                            onChargerClick = { chargerId ->
                                viewModel.setCurrentCharger(chargerId)
                                viewModel.getReservationTimeSlots(chargerId = chargerId)
                                showChargersForAnimation = false
                            },
                            onVehicleAdd = { showCarAddDialog = true },
                            onVehicleSelect = {
                                viewModel.setCurrentVehicle(it)
                                showCarDialog = false
                            },
                            onReportClick = {showReportDialog = true}
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

@Composable
fun FloatingSearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(6.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Ara",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))

            // TextField Alanı
            Box(modifier = Modifier.weight(1f)) {
                if (searchText.isEmpty()) {
                    Text(
                        text = "Şarj istasyonu ara...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = searchText,
                    onValueChange = onSearchTextChanged,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profil",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingMapActions(
    onFavoritesClick: () -> Unit,
    onFilterClick: () -> Unit,
    isStationsFavorite : Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onFavoritesClick,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
            ) {
                Icon(
                    imageVector = if (isStationsFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favoriler",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Favoriler",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )

            TextButton(
                onClick = onFilterClick,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtrele",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filtrele",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FilterOptionsBar(
    selectedFilter : FilterChoice?,
    onFilterSelect: (FilterChoice) -> Unit
) {
    val filters = FilterChoice.values()

    Surface(
        modifier = Modifier.shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(filters) { filter ->
                    val isSelected = selectedFilter == filter

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable {
                                onFilterSelect(filter)
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter.choice,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )

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
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onChargerClick: (Long) -> Unit,
    onVehicleAdd: () -> Unit,
    onVehicleSelect: (Vehicle) -> Unit,
    onReportClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // 🔹 Başlık ve Favori Butonu Alanı
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                station?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = { onReportClick() },
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Report,
                    contentDescription = "Rapor",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onFavoriteToggle,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favoriye Ekle",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
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
                    val alpha = if (item.clickable) 1f else 0.6f

                    ElevatedCard(
                        onClick = { onChargerClick(item.charger.id) },
                        enabled = item.clickable,
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = if (item.clickable) 6.dp else 0.dp
                        ),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (item.clickable) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(alpha)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 🟢 Durum Noktası
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        //.background(item.color)
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
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = item.charger.connectorType.name.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // ⚡ Teknik Detaylar (Güç ve Fiyat)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ChargerDetailRow(
                                    icon = Icons.Default.Bolt,
                                    label = "${item.charger.powerOutput.name.replace("KW_", "")} kW"
                                )
                                ChargerDetailRow(
                                    icon = Icons.Default.Payments,
                                    label = "₺${item.charger.pricePerKwh}/kWh"
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Araç seçilmemişse gösterilecek boş durum
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.charger), // Varsa bir placeholder ikon
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).alpha(0.2f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Lütfen önce bir araç seçin.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ChargerDetailRow(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
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
                    label = { Text("Marka", color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = modelText,
                    onValueChange = { modelText = it },
                    label = { Text("Model", color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it },
                    label = { Text("Batarya Kapasitesi (kWh)", color = Color.LightGray) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = licensePlateText,
                    onValueChange = { licensePlateText = it },
                    label = { Text("Plaka", color = Color.LightGray) },
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
                        label = { Text("Soket Tipi", color = Color.LightGray) },
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
    consumedKwh: Double,
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
                        InfoChip(
                            Icons.Default.ChargingStation,
                            it.connectorType.name.replace("_", " ")
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
                        Text("Tüketilen Enerji", style = MaterialTheme.typography.labelMedium)
                        // String format ile 2 basamaklı gösterim: "0.45 kWh"
                        Text("${String.format("%.2f", consumedKwh)} kWh", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.height(40.dp).width(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Toplam Tutar", style = MaterialTheme.typography.labelMedium)
                        // Gerçek tutar hesabı
                        val cost = consumedKwh * currentReservation.pricePerKwh
                        Text("₺${String.format("%.2f", cost)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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

@Composable
fun ReceiptDialog(
    reservation: Reservation, // ViewModel'den gelen son tamamlanan rezervasyon
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        confirmButton = {
            TextButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kapat", fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text(
                text = "İşlem Özeti",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            // 🔹 Fiş Kağıdı Efekti
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9F9F9)) // Kağıt beyazı
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // İstasyon Bilgisi
                Text(
                    text = reservation.station.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = reservation.station.address,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))
                DashedDivider() // Kesikli Çizgi
                Spacer(modifier = Modifier.height(12.dp))

                // Detaylar (Fiş formatı: Sol etiket, sağ değer)
                ReceiptRow("Tarih", reservation.endTime.toLocalDate().toString())
                ReceiptRow("Bitiş Saati", reservation.endTime.toLocalTime().toString().take(5))
                ReceiptRow("Araç Plaka", reservation.vehicle.licensePlate)
                ReceiptRow("Cihaz", reservation.charger.chargerName)

                Spacer(modifier = Modifier.height(8.dp))

                // Enerji ve Birim Fiyat
                ReceiptRow("Birim Fiyat", "₺${"%.2f".format(reservation.pricePerKwh)}")
                ReceiptRow("Tüketim", "${"%.2f".format(reservation.actualKwh)} kWh")

                Spacer(modifier = Modifier.height(12.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // 💰 TOPLAM TUTAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOPLAM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "₺${"%.2f".format(reservation.totalAmount)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Barkod Efekti (Opsiyonel görsel dokunuş)
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).alpha(0.3f)
                )
                Text(
                    text = "Teşekkür Ederiz",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.alpha(0.5f).padding(top = 8.dp)
                )
            }
        }
    )
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val dashWidth = 10f
        val dashGap = 10f

        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 2f, // 🔹 style yerine direkt strokeWidth
            pathEffect = PathEffect.dashPathEffect( // 🔹 style yerine direkt pathEffect
                intervals = floatArrayOf(dashWidth, dashGap),
                phase = 0f
            )
        )
    }
}

@Composable
fun NearbyStationsPanel(
    isVisible: Boolean,
    stations: List<Pair<Station, Float>>,
    onStationClick: (Long) -> Unit,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tetikleyici Buton (Yan duran küçük bir bar veya ikon)
        Surface(
            onClick = onToggle,
            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.shadow(4.dp, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        ) {
            Icon(
                imageVector = if (isVisible) Icons.Default.KeyboardArrowRight else Icons.Default.FormatListBulleted,
                contentDescription = null,
                modifier = Modifier.padding(8.dp).size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Animasyonlu Liste
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(0.6f) // Ekranın %60'ını kaplasın
                    .shadow(8.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "En Yakın İstasyonlar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(stations) { (st, distance) ->
                            StationListMember(
                                name = st.name,
                                distance = distance,
                                onClick = { onStationClick(st.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StationListMember(name: String, distance: Float, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    text = if (distance > 1000) "%.1f km".format(distance / 1000) else "%.0f m".format(distance),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (com.example.fse_project.data.local.database.entities.Report, String) -> Unit
) {
    var selectedCat by remember { mutableStateOf(Report.CABLE_DAMAGED) }
    var desc by remember { mutableStateOf("") }
    val categories = Report.values()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sorun Bildir", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Lütfen bir kategori seçin:", style = MaterialTheme.typography.bodySmall)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCat == cat,
                            onClick = { selectedCat = cat },
                            label = { Text(cat.text) }
                        )
                    }
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Açıklama") },
                    placeholder = { Text("Sorunu detaylandırın...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedCat, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Gönder") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

enum class FilterChoice(val choice : String){
    ALL("Tümü"),
    AVAILABLE("Sadece Uygunlar"),
    _11KW("11KW"),
    _22KW("22KW"),
    _50KW("50KW"),
    _150KW("150KW"),
    _300KW("300KW")
}