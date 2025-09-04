package com.example.rickandmortyapp.domain

data class Character(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val location: String,
    val episodes: List<String>
)