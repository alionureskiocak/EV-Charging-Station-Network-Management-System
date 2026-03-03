package com.example.fse_project.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fse_project.domain.model.Station
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun MainScreen(
     viewModel: MainViewModel = hiltViewModel()
) {
    /*
    val izmir = LatLng(38.4237, 27.1428)
    val istanbul = LatLng(41.0082, 28.9784)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 12f)
    }
    val context = LocalContext.current
    // 1. İzin durumunu tutan bir state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2. İzin istemek için launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    // 3. Eğer izin yoksa, ekran açıldığında izin isteyelim
    LaunchedEffect(key1 = true) {
        if (!hasLocationPermission) {
            launcher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }


    val properties by remember(hasLocationPermission) {
        mutableStateOf(MapProperties(
            isMyLocationEnabled = true
        ))
    }




    var list = remember { mutableStateListOf<UserMarker>() }

    val customIcon = remember {
        bitmapDescriptorFromVector(context,R.drawable.ic_delete)
    }

    val routePoints = listOf(istanbul, izmir)

    val testPolyline = "euuiF_veeDtFJItH@dKBdFAbDAlHAr@M|@A@A?A@CDAD?LDHLZBd@AjEAhHBnD?xH@xCR~BXzDSp@s@FaIrA}@Li@LuAh@IB@JBfDFzC@rDABCH@R@B@@KxAMdAM\\m@z@]XsBjBIBEFAB_@Jk@?]SOaAGOMMa@K_@B[RMJCPETEj@D`@FTj@hD`@~CPpAx@|F|@lE|@zDTt@bBtGv@`CvApFbB`G`@xAhEpOnAxE_C`B{A|@}ApAl@n@OP[ZQJ"

    val pathPoints = remember(testPolyline) {
        PolyUtil.decode(testPolyline)
    }

    val scope = rememberCoroutineScope()

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

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        onMapClick = { latLng ->
            list.add(UserMarker(latLng))
        },

    ){



        if (pathPoints.isNotEmpty()) {
            Polyline(
                points = pathPoints,
                color = Color(0xFF2196F3), // Klasik Google Maps mavisi
                width = 12f,
                geodesic = true // Dünyanın eğriliğine göre hesapla
            )
        }

        list.forEach { userMarker ->
            Marker(
                state = MarkerState(userMarker.latLng),
                title = userMarker.title,
                icon = customIcon,
                onClick = {
                   // it.showInfoWindow()
                   // false // tıklanan yeri merkeze al
                    list.remove(userMarker)
                    true
                },


            )
        }


    }*/

    val stations by viewModel.stations.collectAsState()
    val station : Station? = stations.firstOrNull()
    station?.let {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow {
                items(station.chargers) { charger ->
                    Button(onClick = {}) {
                        Text("${charger.chargerStatus}")
                    }
                }
            }
        }
    }


}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {

    MapsInitializer.initialize(context)

    // 1. Vektör dosyasını çekiyoruz
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    // 2. Boş bir bitmap oluşturup üzerine çizdiriyoruz
    val bm = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    drawable.draw(canvas)

    // 3. Google Maps'in anlayacağı formata çeviriyoruz
    return BitmapDescriptorFactory.fromBitmap(bm)
}