package com.sumeyye.urbanflora.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sumeyye.urbanflora.data.analyzer.AnalysisResult
import com.sumeyye.urbanflora.data.analyzer.PlantAnalyzer
import com.sumeyye.urbanflora.data.local.db.UrbanFloraDatabase
import com.sumeyye.urbanflora.data.repository.PlantRepositoryImpl
import com.sumeyye.urbanflora.data.repository.UserProgressRepositoryImpl
import com.sumeyye.urbanflora.domain.model.Plant
import com.sumeyye.urbanflora.domain.model.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PlantScanViewModel(application: Application) : AndroidViewModel(application) {

    private val db = UrbanFloraDatabase.getDatabase(application)
    private val plantRepository = PlantRepositoryImpl(db.plantDao())
    private val userProgressRepository = UserProgressRepositoryImpl(db.userProgressDao())
    private val plantAnalyzer = PlantAnalyzer(application.applicationContext)

    private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

    private var currentBitmap: Bitmap? = null

    fun analyzeImage(bitmap: Bitmap) {
        currentBitmap = bitmap
        viewModelScope.launch {
            _scanState.value = ScanUiState.Analyzing
            try {
                when (val result = plantAnalyzer.analyzeImage(bitmap)) {
                    is AnalysisResult.Success -> {
                        _scanState.value = ScanUiState.Success(
                            name = result.name,
                            scientificName = result.scientificName,
                            description = result.description,
                            isRare = result.isRare,
                            confidence = result.confidence
                        )
                    }
                    is AnalysisResult.NoPlantFound -> {
                        _scanState.value = ScanUiState.NoPlantFound
                    }
                    is AnalysisResult.Failure -> {
                        _scanState.value = ScanUiState.Error(result.throwable.message ?: "Analiz başarısız")
                    }
                }
            } catch (e: Exception) {
                _scanState.value = ScanUiState.Error(e.message ?: "Bilinmeyen hata")
            }
        }
    }

    private suspend fun saveBitmapToInternalStorage(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val filename = "plant_${UUID.randomUUID()}.jpg"
        val file = File(getApplication<Application>().filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        file.absolutePath
    }

    fun saveDiscovery(
        name: String,
        scientificName: String,
        description: String,
        isRare: Boolean,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _scanState.value = ScanUiState.Saving
            try {
                val imagePath = currentBitmap?.let { saveBitmapToInternalStorage(it) } ?: ""
                
                val newPlant = Plant(
                    name = name,
                    scientificName = scientificName,
                    latitude = latitude,
                    longitude = longitude,
                    imageUrl = imagePath,
                    timestamp = System.currentTimeMillis(),
                    isRare = isRare
                )
                plantRepository.saveDiscoveredPlant(newPlant)

                val currentProgress = userProgressRepository.getUserProgress().firstOrNull() ?: UserProgress()
                val newCount = currentProgress.discoveredCount + 1
                val scoreIncrement = if (isRare) 50 else 10
                val newScore = currentProgress.totalScore + scoreIncrement

                val newBadges = currentProgress.unlockedBadges.toMutableList()
                if (newCount >= 1 && !newBadges.contains("Explorer Badge")) {
                    newBadges.add("Explorer Badge")
                }
                if (isRare && !newBadges.contains("Rare Finder")) {
                    newBadges.add("Rare Finder")
                }

                val updatedProgress = currentProgress.copy(
                    discoveredCount = newCount,
                    totalScore = newScore,
                    unlockedBadges = newBadges
                )

                userProgressRepository.saveUserProgress(updatedProgress)

                _scanState.value = ScanUiState.DiscoverySaved(scoreIncrement, newBadges.size > currentProgress.unlockedBadges.size)
            } catch (e: Exception) {
                _scanState.value = ScanUiState.Error(e.message ?: "Kaydetme başarısız")
            }
        }
    }

    fun resetState() {
        _scanState.value = ScanUiState.Idle
        currentBitmap = null
    }

    override fun onCleared() {
        super.onCleared()
        plantAnalyzer.close()
    }
}

sealed interface ScanUiState {
    object Idle : ScanUiState
    object Analyzing : ScanUiState
    data class Success(
        val name: String,
        val scientificName: String,
        val description: String,
        val isRare: Boolean,
        val confidence: Float
    ) : ScanUiState
    object NoPlantFound : ScanUiState
    data class Error(val message: String) : ScanUiState
    object Saving : ScanUiState
    data class DiscoverySaved(val scoreEarned: Int, val badgeUnlocked: Boolean) : ScanUiState
}
