package com.tecsup.campusgps.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FiltroRadio(
    radioSeleccionado: Int?,
    onRadioSeleccionado: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val radios = listOf(
        Pair("Sin radio", null),
        Pair("100 m", 100),
        Pair("200 m", 200),
        Pair("500 m", 500)
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        radios.forEach { (label, valor) ->
            FilterChip(
                selected = radioSeleccionado == valor,
                onClick = { onRadioSeleccionado(valor) },
                label = { Text(label) }
            )
        }
    }
}
