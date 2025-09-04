package com.example.rickandmortyapp.presentation.characterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.domain.Character
import com.example.rickandmortyapp.domain.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val repo: CharacterRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    fun load(id: Int) {
        viewModelScope.launch {
            try {
                val ch = repo.getCharacter(id)
                _state.value = UiState.Ready(
                    ch = ch,
                    isFavorite = repo.isFavorite(id)
                )
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun toggleFavorite(id: Int) = viewModelScope.launch {
        repo.toggleFavorite(id)
        load(id)
    }

    fun markEpisodeSeen(id: Int, ep: String) = viewModelScope.launch {
        repo.markEpisodeSeen(id, ep)
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Ready(val ch: Character, val isFavorite: Boolean) : UiState()
    data class Error(val message: String) : UiState()
}