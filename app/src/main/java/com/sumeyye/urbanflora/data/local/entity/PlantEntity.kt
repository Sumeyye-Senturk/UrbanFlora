package com.sumeyye.urbanflora.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sumeyye.urbanflora.domain.model.Plant

@Entity(tableName = "discovered_plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val scientificName: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val timestamp: Long,
    val isRare: Boolean
) {
    fun toDomain(): Plant {
        return Plant(
            id = id,
            name = name,
            scientificName = scientificName,
            latitude = latitude,
            longitude = longitude,
            imageUrl = imageUrl,
            timestamp = timestamp,
            isRare = isRare
        )
    }

    companion object {
        fun fromDomain(plant: Plant): PlantEntity {
            return PlantEntity(
                id = plant.id,
                name = plant.name,
                scientificName = plant.scientificName,
                latitude = plant.latitude,
                longitude = plant.longitude,
                imageUrl = plant.imageUrl,
                timestamp = plant.timestamp,
                isRare = plant.isRare
            )
        }
    }
}
