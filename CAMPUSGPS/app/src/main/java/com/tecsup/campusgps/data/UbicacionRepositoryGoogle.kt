package com.tecsup.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.tecsup.campusgps.model.Posicion
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(private val context: Context) : UbicacionRepository {
    private val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    override fun playServicesDisponible(): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val result = availability.isGooglePlayServicesAvailable(context)
        return result == ConnectionResult.SUCCESS
    }

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {
        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location = client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                Result.success(Posicion(location.latitude, location.longitude))
            } else {
                Result.failure(GpsDisabledException("Activa el GPS e intenta de nuevo."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GpsDisabledException(message: String) : Exception(message)
