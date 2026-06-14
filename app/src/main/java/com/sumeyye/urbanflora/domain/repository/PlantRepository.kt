package com.sumeyye.urbanflora.domain.repository

import com.sumeyye.urbanflora.domain.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getAllDiscoveredPlants(): Flow<List<Plant>>
    suspend fun saveDiscoveredPlant(plant: Plant): Long
    suspend fun getPlantById(id: Long): Plant?
}
