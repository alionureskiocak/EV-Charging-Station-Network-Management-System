package com.example.fse_project

import android.R
import android.R.attr.icon
import android.R.attr.onClick
import android.R.attr.title
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center.position
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.AdvancedMarker
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PinConfig
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.jar.Manifest

@Composable
fun MainScreen() {

    val izmir = LatLng(38.4237, 27.1428)
    val istanbul = LatLng(41.0082, 28.9784)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(izmir, 12f)
    }
    val context = LocalContext.current
    // 1. İzin durumunu tutan bir state
    var hasLocationPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
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


    }
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {

    MapsInitializer.initialize(context)

    // 1. Vektör dosyasını çekiyoruz
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    // 2. Boş bir bitmap oluşturup üzerine çizdiriyoruz
    val bm = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)

    // 3. Google Maps'in anlayacağı formata çeviriyoruz
    return BitmapDescriptorFactory.fromBitmap(bm)
}