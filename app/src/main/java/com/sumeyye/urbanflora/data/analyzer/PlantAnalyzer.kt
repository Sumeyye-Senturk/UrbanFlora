package com.sumeyye.urbanflora.data.analyzer

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.MappedByteBuffer
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantAnalyzer(context: Context) {

    private var interpreter: Interpreter? = null
    private var labels: List<String>? = null
    
    private val modelPath = "flowers_model.tflite"
    private val labelsPath = "labels.txt"
    private val inputImageSize = 224 
    
    private val plantDescriptions = mapOf(
        "rose" to "Aşk ve güzelliğin sembolü, hoş kokulu ve dikenli bir çiçek türü.",
        "daisy" to "Sadeliğin simgesi, beyaz yapraklı ve sarı merkezli şifalı bitki.",
        "dandelion" to "Sarı çiçekli, tohumları rüzgarla yayılan dayanıklı kır bitkisi.",
        "sunflower" to "Güneşi takip eden, büyük sarı çiçekli ve çekirdek veren bitki.",
        "tulip" to "Zarafeti temsil eden, soğanlı ve ilkbaharda açan renkli bir çiçek.",
        "orchid" to "Nadir ve egzotik görünümlü, zarafet timsali değerli bir çiçek.",
        "iris" to "Gökkuşağı adını taşıyan, zarif yapraklı ve renkli bir çiçek türü."
    )

    init {
        try {
            val tfliteModel: MappedByteBuffer = FileUtil.loadMappedFile(context, modelPath)
            interpreter = Interpreter(tfliteModel, Interpreter.Options())
            labels = FileUtil.loadLabels(context, labelsPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult = withContext(Dispatchers.Default) {
        val currInterpreter = interpreter
        val currLabels = labels
        if (currInterpreter == null || currLabels == null) {
            return@withContext AnalysisResult.Failure(Exception("Model yüklenemedi."))
        }

        try {
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(inputImageSize, inputImageSize, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f)) 
                .build()

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            val probabilityBuffer = TensorBuffer.createFixedSize(
                currInterpreter.getOutputTensor(0).shape(),
                currInterpreter.getOutputTensor(0).dataType()
            )

            currInterpreter.run(tensorImage.buffer, probabilityBuffer.buffer)

            val labelsMap = TensorLabel(currLabels, probabilityBuffer).mapWithFloatValue
            val bestEntry = labelsMap.maxByOrNull { it.value }

            if (bestEntry != null && bestEntry.value > 0.35f) { 
                val label = bestEntry.key.lowercase(Locale.ROOT).trim()
                val confidence = bestEntry.value
                
                val turkishName = translateToTurkish(label)
                val description = plantDescriptions[label] ?: "Şehir ekosisteminde keşfedilen özel bir bitki türü."
                val isRare = label in listOf("orchid", "iris")

                AnalysisResult.Success(
                    name = turkishName,
                    scientificName = label.replaceFirstChar { it.uppercase() },
                    description = description,
                    isRare = isRare,
                    confidence = confidence
                )
            } else {
                AnalysisResult.NoPlantFound
            }
        } catch (e: Exception) {
            AnalysisResult.Failure(e)
        }
    }

    private fun translateToTurkish(label: String): String {
        return when (label) {
            "rose" -> "Gül"
            "daisy" -> "Papatya"
            "dandelion" -> "Karahindiba"
            "sunflower" -> "Ayçiçeği"
            "tulip" -> "Lale"
            "orchid" -> "Orkide"
            "iris" -> "İris"
            else -> label.replaceFirstChar { it.uppercase() }
        }
    }

    fun close() {
        interpreter?.close()
    }
}

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
