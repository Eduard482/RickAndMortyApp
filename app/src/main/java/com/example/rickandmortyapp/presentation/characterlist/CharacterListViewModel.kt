package com.example.rickandmortyapp.presentation.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.rickandmortyapp.domain.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

data class CharacterFilters(
    val name: String? = null,
    val status: String? = null,
    val species: String? = null
)

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val repo: CharacterRepository
) : ViewModel() {

    private val filters = MutableStateFlow(CharacterFilters())

    val paging = filters.flatMapLatest { f ->
        repo.getCharactersPaged(f.name, f.status, f.species)
    }.cachedIn(viewModelScope)

    fun setFilters(name: String?, status: String?, species: String?) {
        filters.value = CharacterFilters(name, status, species)
    }
}