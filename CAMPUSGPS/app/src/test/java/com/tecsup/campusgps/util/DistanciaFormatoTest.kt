package com.tecsup.campusgps.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DistanciaFormatoTest {
    @Test
    fun formatearDistanciaM_retornaMetrosEnteros() {
        val resultado = formatearDistancia(350f)
        assertEquals("350 m", resultado)
    }

    @Test
    fun formatearDistanciaKm_retornaKilometrosConUnDecimal() {
        val resultado = formatearDistancia(1500f)
        assertEquals("1.5 km", resultado)
    }
}
