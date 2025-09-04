package com.example.rickandmortyapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmortyapp.presentation.characterdetail.CharacterDetailScreen
import com.example.rickandmortyapp.presentation.characterlist.CharacterListScreen
import com.example.rickandmortyapp.presentation.favorites.FavoritesScreen
import com.example.rickandmortyapp.presentation.map.MapScreen
import com.example.rickandmortyapp.ui.theme.RickAndMortyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RickAndMortyAppTheme {
                App()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun App() {
    val navController = rememberNavController()
    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") {
                CharacterListScreen(
                    onCharacterClick = { id -> navController.navigate("detail/$id") },
                    onFavoritesClick = { navController.navigate("favorites") },
                    onMapClick = { id -> navController.navigate("map/$id") }
                )
            }
            composable(
                "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 1
                CharacterDetailScreen(
                    id = id,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("favorites") {
                FavoritesScreen(onBack = { navController.popBackStack() }, onCharacterClick = { id ->
                    navController.navigate("detail/$id")
                })
            }
            composable(
                "map/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                MapScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}