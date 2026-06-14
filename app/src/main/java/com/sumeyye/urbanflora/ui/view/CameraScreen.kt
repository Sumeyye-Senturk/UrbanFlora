package com.sumeyye.urbanflora.ui.view

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sumeyye.urbanflora.ui.navigation.Screen
import com.sumeyye.urbanflora.ui.viewmodel.PlantScanViewModel
import com.sumeyye.urbanflora.ui.viewmodel.ScanUiState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PlantScanViewModel = viewModel()
) {
    val scanState by viewModel.scanState.collectAsState()
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            viewModel.analyzeImage(bitmap)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bitki Teşhisi",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Captured Image Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (capturedBitmap != null) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Çekilen Bitki Fotoğrafı",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Kamerayı açmak için aşağıdaki butona tıklayın",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // State Machine UI
            AnimatedContent(
                targetState = scanState,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScanStateAnimation"
            ) { state ->
                when (state) {
                    is ScanUiState.Idle -> {
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Camera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Fotoğraf Çek ve Analiz Et", fontSize = 16.sp)
                        }
                    }
                    is ScanUiState.Analyzing -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Görüntü yapay zeka ile işleniyor...",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    is ScanUiState.Success -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = state.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    if (state.isRare) {
                                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                                            Text(
                                                text = "Nadir Tür (+50 Puan)",
                                                color = MaterialTheme.colorScheme.onError,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    } else {
                                        Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                            Text(
                                                text = "Yaygın Tür (+10 Puan)",
                                                color = MaterialTheme.colorScheme.onSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Botanik Adı: ${state.scientificName}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = state.description,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Yapay Zeka Doğruluk Oranı: %${"%.1f".format(state.confidence * 100)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.resetState() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Tekrar Çek")
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.saveDiscovery(
                                                name = state.name,
                                                scientificName = state.scientificName,
                                                description = state.description,
                                                isRare = state.isRare,
                                                latitude = 41.0082,
                                                longitude = 28.9784,
                                                imageUrl = ""
                                            )
                                        },
                                        modifier = Modifier.weight(1.5f)
                                    ) {
                                        Text(text = "Keşif Kitaplığına Ekle")
                                    }
                                }
                            }
                        }
                    }
                    is ScanUiState.NoPlantFound -> {
                        ResultStateCard(
                            title = "Bitki Bulunamadı",
                            description = "Fotoğrafta belirgin bir bitki veya çiçek algılanamadı. Lütfen daha yakından ve net bir şekilde tekrar çekin.",
                            icon = Icons.Default.Warning,
                            iconColor = MaterialTheme.colorScheme.error,
                            onRetry = { viewModel.resetState() }
                        )
                    }
                    is ScanUiState.Error -> {
                        ResultStateCard(
                            title = "Hata Oluştu",
                            description = state.message,
                            icon = Icons.Default.Warning,
                            iconColor = MaterialTheme.colorScheme.error,
                            onRetry = { viewModel.resetState() }
                        )
                    }
                    is ScanUiState.Saving -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Keşif veritabanına kaydediliyor...")
                        }
                    }
                    is ScanUiState.DiscoverySaved -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "Keşif Başarıyla Kaydedildi!",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Tebrikler! ${state.scoreEarned} Botanik Keşif Puanı kazandınız.",
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center
                                )

                                if (state.badgeUnlocked) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "🎉 YENİ ROZET AÇILDI!",
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(6.dp),
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.resetState()
                                            capturedBitmap = null
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Yeni Tarama")
                                    }
                                    Button(
                                        onClick = { navController.navigate(Screen.Profile.route) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = "Profile Git")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultStateCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                color = iconColor
            )
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Tekrar Dene")
            }
        }
    }
}
