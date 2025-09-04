package com.example.rickandmortyapp.presentation.favorites

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rickandmortyapp.presentation.components.CharacterRow

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    vm: FavoritesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var allowed by remember { mutableStateOf(false) }
    var promptLaunched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!promptLaunched) {
            promptLaunched = true
            val activity = context as? FragmentActivity
            if (activity != null) {
                showBiometricPrompt(activity) { success ->
                    allowed = success
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { padding ->
        if (!allowed) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Autenticación requerida para ver favoritos")
            }
        } else {
            val favs = vm.favorites.collectAsState(initial = emptyList()).value
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(favs.size) { idx ->
                    val ch = favs[idx]
                    CharacterRow(ch, onClick = { onCharacterClick(ch.id) }, onMapClick = {})
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
private fun showBiometricPrompt(activity: FragmentActivity, onResult: (Boolean) -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)

    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(true)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onResult(false)
            }

            override fun onAuthenticationFailed() {
                onResult(false)
            }
        })

    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticación requerida")
        .setSubtitle("Usa tu huella o rostro para acceder a Favoritos")
        .setNegativeButtonText("Cancelar")
        .build()

    prompt.authenticate(info)
}
