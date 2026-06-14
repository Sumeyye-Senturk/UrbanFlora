package com.sumeyye.urbanflora.data.repository

import com.sumeyye.urbanflora.data.local.dao.PlantDao
import com.sumeyye.urbanflora.data.local.entity.PlantEntity
import com.sumeyye.urbanflora.domain.model.Plant
import com.sumeyye.urbanflora.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlantRepositoryImpl(
    private val plantDao: PlantDao
) : PlantRepository {

    override fun getAllDiscoveredPlants(): Flow<List<Plant>> {
        return plantDao.getAllPlants().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveDiscoveredPlant(plant: Plant): Long {
        return plantDao.insertDiscoveredPlant(PlantEntity.fromDomain(plant))
    }

    override suspend fun getPlantById(id: Long): Plant? {
        return plantDao.getPlantById(id)?.toDomain()
    }
}
