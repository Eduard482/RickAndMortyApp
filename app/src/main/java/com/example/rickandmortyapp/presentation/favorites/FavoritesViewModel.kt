package com.example.rickandmortyapp.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.domain.Character
import com.example.rickandmortyapp.domain.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: CharacterRepository
) : ViewModel() {

    val favorites: StateFlow<List<Character>> =
        repo.getFavorites() // Flow<List<Character>>
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
