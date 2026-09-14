package com.example.demosih11

import android.app.Application
import com.cloudinary.android.MediaManager

class KabadiwalaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        MediaManager.init(this, mapOf(
            "cloud_name" to "lq36fx6t"
        ))
    }
}