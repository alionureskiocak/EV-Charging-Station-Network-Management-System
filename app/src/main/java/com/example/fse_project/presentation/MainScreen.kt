package com.example.fse_project.presentation

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fse_project.UserMarker
import com.example.fse_project.data.local.database.entities.ChargerStatus
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

@Composable
fun MainScreen(
     viewModel: MainViewModel = hiltViewModel()
) {

    val stations by viewModel.stations.collectAsState()
    println(stations)
    if(stations.isNotEmpty()) {
        val firstChargers = stations.first().chargers
        firstChargers.forEach {
            viewModel.updateChargerStatus(it.id, ChargerStatus.OCCUPIED)
        }
    }



    val izmir = LatLng(38.4237, 27.1428)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 12f)
    }
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    val properties by remember(hasLocationPermission) {
        mutableStateOf(MapProperties(
            isMyLocationEnabled = true
        ))
    }

    var list = remember { mutableStateListOf<UserMarker>() }
    stations.forEach { station ->
        list.add(UserMarker(
            latLng = LatLng(station.latitude, station.longitude),
            title = station.name,
            color = if (station.status == StationStatus.AVAILABLE)
               BitmapDescriptorFactory.HUE_GREEN
            else if (station.status == StationStatus.OCCUPIED)
                BitmapDescriptorFactory.HUE_YELLOW
            else BitmapDescriptorFactory.HUE_RED
        ))
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

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        onMapClick = { latLng ->
        }
    ){
       // if (pathPoints.isNotEmpty()) {
       //     Polyline(
       //         points = pathPoints,
       //         color = Color(0xFF2196F3), // Klasik Google Maps mavisi
       //         width = 12f,
       //         geodesic = true // Dünyanın eğriliğine göre hesapla
       //     )
       // }

        list.forEach { userMarker ->
            Marker(
                state = MarkerState(userMarker.latLng),
                title = userMarker.title,
                icon = BitmapDescriptorFactory.defaultMarker(userMarker.color),
                onClick = {
                   // it.showInfoWindow()
                   true // tıklanan yeri merkeze al
                    list.remove(userMarker)
                    true
                },


            )
        }
        println(stations)


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