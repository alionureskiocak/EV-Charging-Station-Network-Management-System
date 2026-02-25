package com.example.fse_project

import com.google.android.gms.maps.model.LatLng

data class UserMarker(
    val latLng: LatLng,
    val title : String = "asdasd"
)
