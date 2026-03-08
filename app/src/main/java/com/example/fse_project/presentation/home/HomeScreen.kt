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
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.example.fse_project.data.local.database.entities.ConnectorType
import com.example.fse_project.domain.model.Vehicle
import com.example.ibanla.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
     viewModel: MainViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    val currentUser = state.currentUser
    val reservations = state.usersReservations
    val vehicle = state.currentVehicle
    val usersVehicles = state.usersVehicles
    val stations = state.allStations
    val chargerItems = state.chargerItems
    val timeSlots = state.timeSlots

    //var showChargers by remember { mutableStateOf(false) }
    var showCarDialog by remember { mutableStateOf(false) }
    var showCarAddDialog by remember { mutableStateOf(false) }
    var isSheetOpen by remember { mutableStateOf(false) }

    val izmir = LatLng(38.4237, 27.1428)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 10f)
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (vehicle == null){
            showCarDialog = true
        }
    }

    LaunchedEffect(vehicle) {
        println(vehicle)
    }

    //if (showChargers){
//
    //}

    if (showCarDialog){
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

    if (showCarAddDialog){
        CarAddDialog(
            onDismiss = { showCarAddDialog = false },
            currentUserId = currentUser?.id,
            onCarAdd = {
                viewModel.addVehicle(it)
                Toast.makeText(context,"",Toast.LENGTH_SHORT).show()
            }
        )
    }

    var hasLocationPermission by remember { mutableStateOf(false) }
    CheckPermission{
        hasLocationPermission = it
    }

    val properties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = if (hasLocationPermission) true else false
            )
        )
    }


    val list = remember(stations) {
        stations.map { station ->
            UserMarker(
                latLng = LatLng(station.latitude, station.longitude),
                title = station.name,
                color = when (station.status) {
                    StationStatus.AVAILABLE -> BitmapDescriptorFactory.HUE_GREEN
                    StationStatus.OCCUPIED -> BitmapDescriptorFactory.HUE_ORANGE
                    else -> BitmapDescriptorFactory.HUE_RED
                }
            )
        }
    }

    // val routePoints = listOf(izmir)
    // val testPolyline = "euuiF_veeDtFJItH@dKBdFAbDAlHAr@M|@A@A?A@CDAD?LDHLZBd@AjEAhHBnD?xH@xCR~BXzDSp@s@FaIrA}@Li@LuAh@IB@JBfDFzC@rDABCH@R@B@@KxAMdAM\\m@z@]XsBjBIBEFAB_@Jk@?]SOaAGOMMa@K_@B[RMJCPETEj@D`@FTj@hD`@~CPpAx@|F|@lE|@zDTt@bBtGv@`CvApFbB`G`@xAhEpOnAxE_C`B{A|@}ApAl@n@OP[ZQJ"
    //
    // val pathPoints = remember(testPolyline) {
    //     PolyUtil.decode(testPolyline)
    // }

    //val scope = rememberCoroutineScope()
//
    //LaunchedEffect(pathPoints) {
    //    if (pathPoints.isNotEmpty()) {
    //        val builder = LatLngBounds.Builder()
    //        pathPoints.forEach { builder.include(it) }
    //        val bounds = builder.build()
//
    //        cameraPositionState.animate(
    //            update = CameraUpdateFactory.newLatLngBounds(bounds, 100),
    //            durationMs = 1000
    //        )
    //    }
    //}

    Column(modifier = Modifier.fillMaxSize()){
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.55f),
            cameraPositionState = cameraPositionState,
            properties = properties,
            onMapClick = { latLng ->
                currentUser?.let {
                    println(currentUser.name)
                }

            }
        ) {
            // if (pathPoints.isNotEmpty()) {
            //     Polyline(
            //         points = pathPoints,
            //         color = Color(0xFF2196F3), // Klasik Google Maps mavisi
            //         width = 12f,
            //         geodesic = true // Dünyanın eğriliğine göre hesapla
            //     )
            // }

            stations.forEach { station ->

                val color = when (station.status) {
                    StationStatus.AVAILABLE -> BitmapDescriptorFactory.HUE_GREEN
                    StationStatus.OCCUPIED -> BitmapDescriptorFactory.HUE_ORANGE
                    else -> BitmapDescriptorFactory.HUE_RED
                }

                Marker(
                    state = MarkerState(LatLng(station.latitude,station.longitude)),
                    title = station.name,

                    icon = BitmapDescriptorFactory.defaultMarker(color),
                    onClick = {
                        viewModel.getChargersForStation(station.id)
                        //showChargers = true
                        isSheetOpen = true
                        true
                    })
            }
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(0.45f),
            ){

            var showChargersForAnimation by remember { mutableStateOf(true) }
            val sheetState = rememberModalBottomSheetState()

            if (isSheetOpen){
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = {
                        isSheetOpen = false
                        showChargersForAnimation = true
                    }){
                    Box(modifier  =Modifier.fillMaxHeight(0.45f)){
                        AnimatedContent(
                            targetState = showChargersForAnimation,
                            transitionSpec = {
                                slideInHorizontally(
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        easing = FastOutSlowInEasing
                                    ),
                                    initialOffsetX = {if (targetState) -600 else 600}
                                ) + fadeIn() togetherWith slideOutHorizontally(
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        easing = FastOutSlowInEasing
                                    ),
                                    targetOffsetX = {if (targetState) 600 else -600}
                                ) + fadeOut()

                            }
                        ) {
                            if (it) {
                                ChargerChoiceScreen(
                                    chargers = chargerItems,
                                    onChargerClick = { chargerId ->
                                        viewModel.getReservationTimeSlots(
                                            chargerId = chargerId
                                        )
                                        showChargersForAnimation = false
                                    }
                                )
                            } else {
                                ChargerTimeSlotsScreen()
                            }
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun ChargerChoiceScreen(chargers : List<ChargerItem>,onChargerClick : (Long) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(chargers) { charger ->
            Column {
                Card(
                    onClick = {
                        onChargerClick(charger.charger.id)
                    },
                    enabled = charger.clickable
                ) {
                    Image(painter = painterResource(R.drawable.charger), contentDescription = null)
                    Text(text = "${charger.charger.connectorType}")
                }

            }
        }
    }
}

@Composable
fun ChargerTimeSlotsScreen() {
    Text("Deneme")
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

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    MapsInitializer.initialize(context)
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}