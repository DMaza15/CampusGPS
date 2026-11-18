package com.tecsup.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.campusgps.data.GpsDisabledException
import com.tecsup.campusgps.data.LugaresRepository
import com.tecsup.campusgps.data.UbicacionRepository
import com.tecsup.campusgps.model.Lugar
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.Posicion
import com.tecsup.campusgps.model.TipoError
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow<List<LugarConDistancia>>(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _categoriaFiltro = MutableStateFlow("Todos")
    val categoriaFiltro: StateFlow<String> = _categoriaFiltro.asStateFlow()

    val lugaresFiltrados: StateFlow<List<LugarConDistancia>> = combine(_lugares, _categoriaFiltro) { lista, cat ->
        if (cat == "Todos") {
            lista
        } else {
            lista.filter { it.lugar.categoria.equals(cat, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = _lugares.value
    )

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    val masCercano: StateFlow<Lugar?> = lugaresFiltrados.map { list ->
        list.firstOrNull { it.metros != null }?.lugar
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    fun buscar() {
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                mensaje = "Este dispositivo no tiene Google Play Services",
                tipo = TipoError.SIN_PLAY_SERVICES
            )
            return
        }

        viewModelScope.launch {
            _estado.value = UbicacionUi.Buscando
            val res = ubicacionRepo.obtenerPosicion()
            res.fold(
                onSuccess = { pos ->
                    actualizarDistancias(pos)
                    _estado.value = UbicacionUi.Lista(pos)
                },
                onFailure = { err ->
                    val tipo = if (err is GpsDisabledException) {
                        TipoError.GPS_APAGADO
                    } else {
                        TipoError.OTRO
                    }
                    _estado.value = UbicacionUi.Error(
                        mensaje = err.message ?: "Error al obtener ubicación",
                        tipo = tipo
                    )
                }
            )
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        val lugaresActualizados = lugaresRepo.obtenerLugares().map { lugar ->
            val dist = distanciaMetros(pos, lugar)
            LugarConDistancia(lugar, dist)
        }.sortedBy { it.metros }
        _lugares.value = lugaresActualizados
    }

    fun seleccionarCategoria(categoria: String) {
        _categoriaFiltro.value = categoria
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            mensaje = "Sin permiso de ubicación no puedo mostrar dónde estás",
            tipo = TipoError.SIN_PERMISO
        )
    }
}
