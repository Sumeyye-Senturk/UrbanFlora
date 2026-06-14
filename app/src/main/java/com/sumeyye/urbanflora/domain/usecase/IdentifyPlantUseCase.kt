package com.sumeyye.urbanflora.domain.usecase

import com.sumeyye.urbanflora.domain.model.Plant
import com.sumeyye.urbanflora.domain.repository.PlantRepository

class IdentifyPlantUseCase(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(name: String, scientificName: String, description: String, latitude: Double, longitude: Double): Long {
        val plant = Plant(
            name = name,
            scientificName = scientificName,
            description = description,
            latitude = latitude,
            longitude = longitude
        )
        return plantRepository.saveDiscoveredPlant(plant)
    }
}
