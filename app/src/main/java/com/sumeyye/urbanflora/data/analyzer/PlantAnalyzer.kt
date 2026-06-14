package com.sumeyye.urbanflora.data.analyzer

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCancellableCoroutine

class PlantAnalyzer {
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val plantLabel = labels.firstOrNull { label ->
                    val text = label.text.lowercase()
                    text.contains("plant") || text.contains("flower") || text.contains("rose") ||
                            text.contains("tree") || text.contains("leaf") || text.contains("botany") ||
                            text.contains("grass") || text.contains("garden")
                }

                if (plantLabel != null) {
                    val labelText = plantLabel.text
                    val confidence = plantLabel.confidence

                    val (plantName, scientificName, description, isRare) = when {
                        labelText.lowercase().contains("rose") -> {
                            Quadruple("Rosa / Japon Gülü", "Rosa chinensis", "Narin ve parlak renkli çiçekleriyle bilinen, süs bitkisi olarak popüler bir gül türü.", true)
                        }
                        labelText.lowercase().contains("flower") -> {
                            Quadruple("Papatya", "Matricaria chamomilla", "Tıbbi ve aromatik özellikleri olan, beyaz yapraklı sarı merkezli kır çiçeği.", false)
                        }
                        labelText.lowercase().contains("tree") -> {
                            Quadruple("Çınar Ağacı", "Platanus orientalis", "Yüzyıllarca yaşayabilen, büyük yapraklı gölge veren anıtsal ağaç türü.", false)
                        }
                        else -> {
                            Quadruple("Yabani Ot", "Herba silvestris", "Şehir kaldırımlarında ve yeşil alanlarda kendiliğinden yetişen otsu bitki.", false)
                        }
                    }
                    continuation.resume(AnalysisResult.Success(plantName, scientificName, description, isRare, confidence))
                } else {
                    continuation.resume(AnalysisResult.NoPlantFound)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resume(AnalysisResult.Failure(exception))
            }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

sealed interface AnalysisResult {
    data class Success(
        val name: String,
        val scientificName: String,
        val description: String,
        val isRare: Boolean,
        val confidence: Float
    ) : AnalysisResult
    object NoPlantFound : AnalysisResult
    data class Failure(val throwable: Throwable) : AnalysisResult
}
