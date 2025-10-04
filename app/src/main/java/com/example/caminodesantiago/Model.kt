package com.example.caminodesantiago

import kotlinx.serialization.Serializable

@Serializable
data class Monastery(
    val id: String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val imageResName: String = "",
    val description: String = ""
)

@Serializable
data class Route(
    val id: String,
    val name: String,
    val color: String = "#FF0000", // hex color string
    val pointIds: List<String> = emptyList()
)
