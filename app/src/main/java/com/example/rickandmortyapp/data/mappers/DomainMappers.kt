package com.example.rickandmortyapp.data.mappers

import com.example.rickandmortyapp.data.local.CharacterEntity
import com.example.rickandmortyapp.domain.Character

fun CharacterEntity.toDomain(episodes: List<String> = emptyList()): Character =
    Character(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image,
        location = location,
        episodes = episodes
    )

