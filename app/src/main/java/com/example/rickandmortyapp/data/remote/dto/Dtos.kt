package com.example.rickandmortyapp.data.remote.dto

data class CharacterResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String,
    val url: String,
    val episode: List<String>,
    val location: LocationDto
)

data class LocationDto(
    val name: String,
    val url: String
)