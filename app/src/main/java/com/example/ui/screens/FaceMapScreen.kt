package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.SkinCareViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceMapScreen(
    viewModel: SkinCareViewModel,
    onNavigateToChat: (String) -> Unit = {},
    onAnalysisConfirmed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeProfile by viewModel.skinProfile.collectAsState()
    val scanAnalysis by viewModel.scanProfileAnalysis.collectAsState()
    val scanAnalysisError by viewModel.scanAnalysisError.collectAsState()
    val lastPhotoPath by viewModel.lastScannedPhotoPath.collectAsState()
    val isScanLoading by viewModel.isScanLoading.collectAsState()
    var showCameraView by remember { mutableStateOf(false) }

    if (showCameraView) {
        CameraCaptureView(
            onPhotoCaptured = { file ->
                showCameraView = false
                viewModel.analyzeScanForProfile(file.absolutePath)
            },
            onDismiss = { showCameraView = false },
            requireFaceQuality = true,
            modifier = modifier
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "Görsel Cilt Değerlendirmesi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }
                    Text(
                        text = "Fotoğrafta görülebilen özelliklerin 6 yüz bölgesine göre bakım özeti",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Real Photo Upload & Scanner Card
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderDefault),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!lastPhotoPath.isNullOrBlank() && File(lastPhotoPath!!).exists()) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(2.dp, Purple500, RoundedCornerShape(14.dp))
                        ) {
                            AsyncImage(
                                model = File(lastPhotoPath!!),
                                contentDescription = "Yüz fotoğrafın",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(14.dp))
                                Text("Kendi Fotoğrafın Aktif", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            }
                            Text("Puanlar kendi yüz hatların üzerinde gösteriliyor", fontSize = 11.sp, color = TextSecondary)
                        }
                        Button(
                            onClick = { showCameraView = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple100),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Purple700, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Yenile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple700)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Lilac100, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Purple600, modifier = Modifier.size(24.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kendi Yüz Fotoğrafını Ekle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            Text("Daha gerçekçi analiz için selfie çek veya tara", fontSize = 11.sp, color = TextSecondary)
                        }
                        Button(
                            onClick = { showCameraView = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Fotoğraf Çek", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                }
            }

            if (isScanLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(color = Purple600)
                        Text("Yapay zeka yüz bölgelerini haritalandırıyor...", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Purple700)
                    }
                }
            } else if (scanAnalysis != null) {
                FaceMapDiagnosticCard(
                    analysisResult = scanAnalysis,
                    photoPath = lastPhotoPath,
                    userConcerns = activeProfile?.skinConcerns?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
                    onApplyToProfile = { skinType, concerns, goal ->
                        viewModel.saveSkinProfile(
                            userName = activeProfile?.userName ?: "",
                            age = activeProfile?.age ?: 0,
                            gender = activeProfile?.gender ?: "",
                            skinType = skinType,
                            skinConcerns = concerns,
                            skincareGoal = goal,
                            makeupPreference = activeProfile?.makeupPreference ?: "",
                            allergies = activeProfile?.allergies ?: "",
                            onSaved = onAnalysisConfirmed
                        )
                    },
                    onRetakePhoto = {
                        showCameraView = true
                    }
                )
            } else if (!lastPhotoPath.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text("Yüz analizi tamamlanamadı", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(
                            scanAnalysisError ?: "Geçerli bir analiz sonucu alınamadı.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Button(
                            onClick = { showCameraView = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Yeniden Fotoğraf Çek")
                        }
                    }
                }
            }

            // Quick Ask AI Button for Face Map
            Surface(
                color = SurfaceTint,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderDefault),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigateToChat("Yüz haritama göre bölgesel bakımlarımı nasıl sıralamalıyım?")
                    }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Yüz Haritan Hakkında AI Soru Sor", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Text("Hangi bölgeye hangi sırayla ürün süreceğini öğren", fontSize = 11.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Purple600)
                }
            }

            Spacer(modifier = Modifier.height(130.dp))
        }
    }
}
