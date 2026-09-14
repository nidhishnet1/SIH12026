package com.example.demosih11.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import java.util.Locale

object LocationHelper {

    fun checkLocationInIndia(context: Context, onResult: (isInIndia: Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onResult(false)
            return
        }

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude

                if (lat in 8.0..37.0 && lon in 68.0..97.0) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(lat, lon, 1)

                        if (addresses != null && addresses.isNotEmpty()) {
                            val country = addresses[0].countryName
                            onResult(country == "India")
                        } else {
                            onResult(true)
                        }
                    } catch (e: Exception) {
                        onResult(true)
                    }
                } else {
                    onResult(false)
                }
            } else {
                onResult(false)
            }
        }.addOnFailureListener {
            onResult(false)
        }
    }
}