package com.sumeyye.urbanflora.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumeyye.urbanflora.data.local.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM discovered_plants ORDER BY timestamp DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscoveredPlant(plant: PlantEntity): Long

    @Query("SELECT * FROM discovered_plants WHERE id = :id")
    suspend fun getPlantById(id: Long): PlantEntity?
}
