package com.sumeyye.urbanflora.domain.usecase

import com.sumeyye.urbanflora.domain.model.Plant
import com.sumeyye.urbanflora.domain.repository.PlantRepository

class IdentifyPlantUseCase(
    private val plantRepository: PlantRepository
) {
    suspend operator fun invoke(name: String, scientificName: String, latitude: Double, longitude: Double, imageUrl: String, isRare: Boolean): Long {
        val plant = Plant(
            name = name,
            scientificName = scientificName,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl,
            isRare = isRare
        )
        return plantRepository.saveDiscoveredPlant(plant)
    }
}
