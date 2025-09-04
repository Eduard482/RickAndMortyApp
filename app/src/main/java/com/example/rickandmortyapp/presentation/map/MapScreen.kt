package com.example.rickandmortyapp.presentation.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBack: () -> Unit) {
    // Simulación simple de coordenadas a partir del id
    val id = 1 // placeholder: obtén el id del NavBackStack si lo necesitas
    val coords = remember(id) { simulateCoords(id) }
    val cameraPositionState = rememberCameraPositionState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { padding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(padding),
            cameraPositionState = cameraPositionState
        ) {
            Marker(title = "Última ubicación simulada")
        }
    }
}

private fun simulateCoords(id: Int): LatLng {
    val lat = (id * 13 % 90) + sin(id.toDouble()) * 0.5
    val lng = (id * 29 % 180) + sin(id.toDouble()) * 0.5
    return LatLng(lat, lng)
}