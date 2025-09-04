package com.example.rickandmortyapp.presentation.characterdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    id: Int,
    onBack: () -> Unit,
    vm: CharacterDetailViewModel = hiltViewModel()
) {
    val state = vm.state.collectAsState().value

    LaunchedEffect(id) {
        vm.load(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        when (state) {
            is UiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is UiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.message}")
            }

            is UiState.Ready -> {
                val ch = state.ch
                val isFav = state.isFavorite

                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // Imagen con bordes redondeados y sombra
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(ch.image),
                                contentDescription = ch.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    item {
                        Text(
                            text = ch.name,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text(ch.status) }
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text(ch.species) }
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text(ch.gender) }
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Última ubicación",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(ch.location, style = MaterialTheme.typography.bodyLarge)
                    }

                    item {
                        Button(
                            onClick = { vm.toggleFavorite(ch.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (isFav) "Quitar de favoritos" else "Agregar a favoritos")
                        }
                    }

                    if (ch.episodes.isNotEmpty()) {
                        item {
                            Text(
                                text = "Episodios",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }

                        items(ch.episodes.size) { index ->
                            val ep = ch.episodes[index]
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Episodio $ep")
                                    TextButton(onClick = { vm.markEpisodeSeen(ch.id, ep) }) {
                                        Text("Marcar visto")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
