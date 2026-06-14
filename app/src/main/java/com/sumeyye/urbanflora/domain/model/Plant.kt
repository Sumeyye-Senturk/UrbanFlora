package com.sumeyye.urbanflora.domain.model

data class Plant(
    val id: Long = 0,
    val name: String,
    val scientificName: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRare: Boolean = false
)
