package com.tecsup.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.campusgps.data.LugaresRepositoryLocal
import com.tecsup.campusgps.data.UbicacionRepositoryGoogle
import com.tecsup.campusgps.ui.PantallaMapa
import com.tecsup.campusgps.ui.theme.CampusGpsTheme
import com.tecsup.campusgps.viewmodel.CampusViewModelFactory
import com.tecsup.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val ubicacionRepo = UbicacionRepositoryGoogle(applicationContext)
        val lugaresRepo = LugaresRepositoryLocal()
        val factory = CampusViewModelFactory(ubicacionRepo, lugaresRepo)

        setContent {
            CampusGpsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MapaViewModel = viewModel(factory = factory)
                    PantallaMapa(viewModel = viewModel)
                }
            }
        }
    }
}
