package com.tecsup.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.UbicacionUi

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier
) {
    val defaultPos = LatLng(-12.0464, -77.0428)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPos, 12f)
    }

    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val userPos = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(userPos, 16f)
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        if (estado is UbicacionUi.Lista) {
            val userPos = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            Marker(
                state = MarkerState(position = userPos),
                title = "Estás aquí"
            )
        }

        lugares.forEach { item ->
            val lugar = item.lugar
            key(lugar.id) {
                Marker(
                    state = MarkerState(position = LatLng(lugar.latitud, lugar.longitud)),
                    title = lugar.nombre,
                    snippet = lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
    }
}
