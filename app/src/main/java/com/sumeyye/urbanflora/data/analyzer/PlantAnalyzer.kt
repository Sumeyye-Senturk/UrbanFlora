package com.sumeyye.urbanflora.data.analyzer

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class PlantAnalyzer {
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    suspend fun analyzeImage(bitmap: Bitmap): AnalysisResult = suspendCancellableCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                // Check all labels for the most specific match
                val labelStrings = labels.map { it.text.lowercase() }
                
                val isPlantRelated = labelStrings.any { 
                    it.contains("plant") || it.contains("flower") || it.contains("tree") || 
                    it.contains("leaf") || it.contains("botany") || it.contains("vegetation")
                }

                if (isPlantRelated) {
                    val confidence = labels.firstOrNull()?.confidence ?: 0f
                    
                    val result = when {
                        labelStrings.any { it.contains("rose") } -> {
                            Quadruple("Gül (Rosa)", "Rosa", "Aşk ve güzelliğin sembolü, hoş kokulu ve dikenli bir çiçek türü.", false)
                        }
                        labelStrings.any { it.contains("tulip") } -> {
                            Quadruple("Lale (Tulipa)", "Tulipa", "Zarafeti temsil eden, soğanlı ve ilkbaharda açan renkli bir çiçek.", false)
                        }
                        labelStrings.any { it.contains("daisy") } -> {
                            Quadruple("Papatya", "Matricaria chamomilla", "Sadeliğin simgesi, beyaz yapraklı ve sarı merkezli şifalı bitki.", false)
                        }
                        labelStrings.any { it.contains("sunflower") } -> {
                            Quadruple("Ayçiçeği", "Helianthus annuus", "Güneşi takip eden, büyük sarı çiçekli ve çekirdek veren bitki.", false)
                        }
                        labelStrings.any { it.contains("orchid") } -> {
                            Quadruple("Orkide", "Orchidaceae", "Nadir ve egzotik görünümlü, zarafet timsali değerli bir çiçek.", true)
                        }
                        labelStrings.any { it.contains("lavender") } -> {
                            Quadruple("Lavanta", "Lavandula", "Rahatlatıcı kokusuyla bilinen, mor çiçekli ve aromatik bir çalı.", false)
                        }
                        labelStrings.any { it.contains("cactus") || it.contains("succulent") } -> {
                            Quadruple("Kaktüs / Sukulent", "Cactaceae", "Su depolayabilen, dayanıklı ve genellikle dikenli çöl bitkisi.", false)
                        }
                        labelStrings.any { it.contains("lily") } -> {
                            Quadruple("Zambak (Lily)", "Lilium", "Büyük ve gösterişli çiçekleri olan, soğanlı ve hoş kokulu bitki.", false)
                        }
                        labelStrings.any { it.contains("hibiscus") } -> {
                            Quadruple("Japon Gülü (Hibiskus)", "Hibiscus rosa-sinensis", "Büyük, boru şeklinde çiçekleri olan tropikal bir çalı.", true)
                        }
                        labelStrings.any { it.contains("clover") } -> {
                            Quadruple("Yonca", "Trifolium", "Genellikle üç yapraklı olan, şans getirdiğine inanılan çayır bitkisi.", false)
                        }
                        labelStrings.any { it.contains("pine") || it.contains("conifer") } -> {
                            Quadruple("Çam Ağacı", "Pinus", "İğne yapraklı ve kozalaklı, her mevsim yeşil kalan anıtsal ağaç.", false)
                        }
                        labelStrings.any { it.contains("oak") } -> {
                            Quadruple("Meşe Ağacı", "Quercus", "Güç ve dayanıklılığın sembolü, palamut meyvesi veren heybetli ağaç.", false)
                        }
                        labelStrings.any { it.contains("maple") } -> {
                            Quadruple("Akçaağaç", "Acer", "Karakteristik yaprak şekliyle bilinen, sonbaharda renk değiştiren ağaç.", false)
                        }
                        labelStrings.any { it.contains("ivy") } -> {
                            Quadruple("Sarmaşık", "Hedera helix", "Duvarlara ve ağaçlara tırmanan, her daim yeşil kalan tırmanıcı bitki.", false)
                        }
                        labelStrings.any { it.contains("palm") } -> {
                            Quadruple("Palmiye", "Arecaceae", "Tropikal bölgelerin simgesi, geniş ve yelpaze benzeri yapraklı ağaç.", true)
                        }
                        labelStrings.any { it.contains("grass") } -> {
                            Quadruple("Çimen / Ot", "Poaceae", "Toprağı örten, yeşil alanların temelini oluşturan ince yapraklı bitki.", false)
                        }
                        labelStrings.any { it.contains("fern") } -> {
                            Quadruple("Eğrelti Otu", "Polypodiopsida", "Çiçeksiz, gölge ve nemli yerleri seven kadim bir bitki türü.", false)
                        }
                        labelStrings.any { it.contains("aloe") } -> {
                            Quadruple("Aloe Vera", "Aloe barbadensis miller", "Etli yapraklarında jel barındıran, tıbbi amaçlı kullanılan sukulent.", true)
                        }
                        labelStrings.any { it.contains("moss") } -> {
                            Quadruple("Yosun", "Bryophyta", "Nemli yüzeylerde halı gibi yayılan, köksüz küçük yeşil bitki.", false)
                        }
                        else -> {
                            Quadruple("Yabani Bitki", "Flora silvestris", "Şehir ekosisteminde kendiliğinden yetişen, doğanın bir parçası olan bitki.", false)
                        }
                    }
                    continuation.resume(AnalysisResult.Success(result.first, result.second, result.third, result.fourth, confidence))
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
