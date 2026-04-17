package com.example.fse_project

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object{
        const val BASE_URL = "https://maps.googleapis.com/"
    }
}

