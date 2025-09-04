package com.example.rickandmortyapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.rickandmortyapp.domain.Character

@Composable
fun CharacterRow(
    c: Character,
    onClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(c.image),
                contentDescription = c.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(c.name, style = MaterialTheme.typography.titleMedium)
                Text("${c.species} • ${c.status}", style = MaterialTheme.typography.bodyMedium)
                Text("Ubicación: ${c.location}", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onMapClick) { Text("Mapa") }
        }
    }
}
