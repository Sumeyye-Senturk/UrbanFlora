package com.sumeyye.urbanflora.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyye.urbanflora.data.local.db.UrbanFloraDatabase
import com.sumeyye.urbanflora.data.repository.PlantRepositoryImpl
import com.sumeyye.urbanflora.domain.model.Plant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val db = UrbanFloraDatabase.getDatabase(application)
    private val plantRepository = PlantRepositoryImpl(db.plantDao())

    val discoveredPlants: StateFlow<List<Plant>> = plantRepository
        .getAllDiscoveredPlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
