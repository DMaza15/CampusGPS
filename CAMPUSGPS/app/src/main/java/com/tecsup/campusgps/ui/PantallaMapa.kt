package com.tecsup.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.util.formatearDistancia
import com.tecsup.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    viewModel: MapaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val estado by viewModel.estado.collectAsState()
    val lugares by viewModel.lugaresFiltrados.collectAsState()
    val seleccionado by viewModel.seleccionado.collectAsState()
    val masCercano by viewModel.masCercano.collectAsState()
    val categoriaFiltro by viewModel.categoriaFiltro.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            viewModel.buscar()
        } else {
            viewModel.permisoDenegado()
        }
    }

    fun ejecutarAccion() {
        val fineCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
            viewModel.buscar()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FiltroCategorias(
                categoriaSeleccionada = categoriaFiltro,
                onCategoriaSeleccionada = { cat -> viewModel.seleccionarCategoria(cat) }
            )

            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { ejecutarAccion() },
                        enabled = estado !is UbicacionUi.Buscando,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (estado is UbicacionUi.Buscando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buscando...")
                        } else {
                            Text("Obtener mi ubicación")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            masCercano?.let { viewModel.seleccionar(it.id) }
                        },
                        enabled = masCercano != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ir al más cercano")
                    }
                }

                masCercano?.let { lugar ->
                    val lugarConDist = lugares.find { it.lugar.id == lugar.id }
                    val distTexto = lugarConDist?.metros?.let { formatearDistancia(it) } ?: ""
                    Text(
                        text = "Más cercano: ${lugar.nombre} ($distTexto)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                when (val actualState = estado) {
                    is UbicacionUi.Inicial -> {
                        Text(
                            text = "Presiona el botón para obtener tu ubicación.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    is UbicacionUi.Buscando -> {
                        Text(
                            text = "Obteniendo posición GPS...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    is UbicacionUi.Lista -> {
                        Text(
                            text = "Lat: ${actualState.posicion.latitud}, Lng: ${actualState.posicion.longitud}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    is UbicacionUi.Error -> {
                        MensajeError(
                            error = actualState,
                            onReintentar = { ejecutarAccion() }
                        )
                    }
                }
            }

            ListaLugares(
                lugares = lugares,
                seleccionadoId = seleccionado,
                onClick = { id -> viewModel.seleccionar(id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }
    }
}
