package com.example.rickandmortyapp.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharactersPaged(
        name: String?,
        status: String?,
        species: String?
    ): Flow<PagingData<Character>>

    suspend fun getCharacter(id: Int): Character

    fun getFavorites(): Flow<List<Character>>
    suspend fun toggleFavorite(id: Int)
    suspend fun isFavorite(id: Int): Boolean

    fun episodesSeen(id: Int): Flow<List<String>>
    suspend fun markEpisodeSeen(id: Int, episode: String)
}