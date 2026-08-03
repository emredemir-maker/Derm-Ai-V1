package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.SkinProfile
import com.example.ui.viewmodel.SkinCareViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.data.notification.NotificationHelper
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SkinCareViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.skinProfile.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val hasCompletedTour by viewModel.hasCompletedTour.collectAsState()
    var currentTourStep by remember { mutableStateOf(-1) }

    val scrollState = rememberScrollState()

    // Start tour automatically for first-time users once profile setup is done
    LaunchedEffect(hasCompletedTour, profile) {
        if (!hasCompletedTour && profile != null && currentTourStep == -1) {
            currentTourStep = 0
        }
    }

    // Auto scroll based on current tour step
    LaunchedEffect(currentTourStep) {
        if (currentTourStep >= 0) {
            when (currentTourStep) {
                0 -> scrollState.animateScrollTo(0)     // Profil Özeti
                1 -> scrollState.animateScrollTo(220)   // Cilt Sağlığı Puanı
                2 -> scrollState.animateScrollTo(420)   // Hızlı İpuçları
                3 -> scrollState.animateScrollTo(680)   // Yapay Zeka Tavsiyeleri
                4 -> scrollState.animateScrollTo(950)   // Bildirim Kurulumu
                5 -> scrollState.animateScrollTo(1250)  // Keşfedin!
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isAnalyzing) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Yapay Zeka Cildinizi Analiz Ediyor...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Cilt tipinize ve şikayetlerinize en uygun kozmetik krem bileşenleri, günlük rutinler ve gözenek dostu makyaj tüyoları hazırlanıyor.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        } else {
            profile?.let { activeProfile ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    // Welcome & Guided Tour Button Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DermaAI Analiz",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Kişisel Cilt Bakım Rehberiniz",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                        
                        FilledTonalButton(
                            onClick = { currentTourStep = 0 },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("start_walkthrough_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Nasıl Kullanılır?",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Nasıl Kullanılır?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Premium Aesthetic Hero Banner Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.skincare_hero),
                                contentDescription = "Skincare Hero",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Soft warm gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color(0xFF242F27).copy(alpha = 0.75f))
                                        )
                                    )
                            )
                            // Text inside the banner
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "Işıltılı Cildiniz",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Kişiselleştirilmiş yapay zeka analizli bakım rutinleri",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // Profile Info Header Card
                    ProfileSummaryCard(
                        profile = activeProfile,
                        onReset = { viewModel.resetProfile() }
                    )

                    // Sleek Interface: AI Health Score Card
                    AiHealthScoreCard(profile = activeProfile)

                    // Sleek Interface: Quick Actions Grid
                    QuickActionsGrid(profile = activeProfile)

                    // Reminders & Notification Setup Card
                    RemindersCard()

                    // Analysis routine
                    if (activeProfile.lastAnalysisRoutine.isNullOrBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    "Analiziniz Hazır Değil",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Yapay zekanın size özel tavsiyeler üretmesi için hemen analizi başlatın.",
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = { viewModel.triggerFullAIAnalysis() },
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("analyze_now_button")
                                ) {
                                    Text("Analiz Et")
                                }
                            }
                        }
                    } else {
                        // Display generated analyses
                        Text(
                            "Yapay Zeka Tavsiyeleriniz",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // 1. Morning Routine Card
                        RoutineSectionCard(
                            title = "Sabah Cilt Bakım Rutini",
                            icon = Icons.Default.WbSunny,
                            iconTint = Color(0xFFF39C12),
                            content = activeProfile.lastAnalysisRoutine ?: ""
                        )

                        // 2. Makeup Advisory Card
                        RoutineSectionCard(
                            title = "Cildinize Özel Makyaj Tavsiyeleri",
                            icon = Icons.Default.Brush,
                            iconTint = MaterialTheme.colorScheme.primary,
                            content = activeProfile.lastAnalysisMakeup ?: "Teninize en uygun doğal tonlar ve hafif kapatıcılar tercih edin."
                        )

                        // Button to re-analyze
                        OutlinedButton(
                            onClick = { viewModel.triggerFullAIAnalysis() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("reanalyze_button"),
                            shape = RoundedCornerShape(25.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Önerileri Yapay Zeka ile Yenile")
                        }

                        // Last analysis date footer
                        if (activeProfile.lastAnalysisDate > 0L) {
                            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr"))
                            val dateStr = sdf.format(Date(activeProfile.lastAnalysisDate))
                            Text(
                                text = "Son analiz tarihi: $dateStr",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Profil yükleniyor...")
            }
        }

        // Render the beautiful walkthrough overlay if tour is active
        profile?.let { activeProfile ->
            if (currentTourStep >= 0) {
                WalkthroughOverlay(
                    currentStep = currentTourStep,
                    onNext = {
                        if (currentTourStep < 5) {
                            currentTourStep++
                        } else {
                            viewModel.completeTour()
                            currentTourStep = -1
                        }
                    },
                    onBack = {
                        if (currentTourStep > 0) {
                            currentTourStep--
                        }
                    },
                    onSkip = {
                        viewModel.completeTour()
                        currentTourStep = -1
                    },
                    skinProfile = activeProfile
                )
            }
        }
    }
}

@Composable
fun ProfileSummaryCard(
    profile: SkinProfile,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Cilt Profiliniz",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${profile.skinType} Cilt",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("reset_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Profili Düzenle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Profile info rows
            InfoRow(label = "Cilt Şikayetleri", value = profile.skinConcerns)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Bakım Hedefi", value = profile.skincareGoal)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = "Makyaj Tercihi", value = profile.makeupPreference)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun RoutineSectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Küçült" else "Genişlet",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = content,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AiHealthScoreCard(
    profile: SkinProfile,
    modifier: Modifier = Modifier
) {
    val score = remember(profile) {
        var calculated = 95 // Start high, deduct for issues
        
        // Deduct based on concerns
        if (profile.skinConcerns.isNotBlank() && profile.skinConcerns != "Yok") {
            val concernCount = profile.skinConcerns.split(",").size
            calculated -= (concernCount * 6)
        }
        
        // Deduct based on allergies
        if (profile.allergies.isNotBlank() && profile.allergies != "Yok") {
            val allergyCount = profile.allergies.split(",").size
            calculated -= (allergyCount * 4)
        }

        // Adjust based on skin type baseline
        when (profile.skinType) {
            "Kuru", "Yağlı" -> calculated -= 5
            "Karma", "Hassas" -> calculated -= 3
            "Normal" -> calculated += 5
        }

        calculated.coerceIn(40, 98)
    }

    val feedbackTitle = when {
        score >= 85 -> "Cildiniz Işıldıyor ve Dengeli"
        score >= 70 -> "Cilt Bariyeriniz Güçleniyor"
        else -> "Cilt Bakım Rutinine Odaklanın"
    }

    val feedbackDesc = when {
        score >= 85 -> "Genel cilt sağlığınız harika görünüyor. Mevcut rutininizi korumaya devam edin."
        score >= 70 -> "Cildiniz yavaş yavaş toparlanıyor. Belirttiğiniz hassasiyetler için hedefe yönelik bakımı sürdürün."
        else -> "Cilt sorunlarınızı ve hassasiyetlerinizi onarmak için rutininizde iyileştirmeler yapmanız faydalı olacaktır."
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "YAPAY ZEKA ANALİZİ",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "Bugün",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular progress ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    CircularProgressIndicator(
                        progress = score / 100f,
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp,
                        modifier = Modifier.fillMaxSize(),
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = score.toString(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "PUAN",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = feedbackTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = feedbackDesc,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = "${profile.skinType} Cilt",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = profile.skincareGoal.split(" ").firstOrNull() ?: "Bakım",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    profile: SkinProfile,
    modifier: Modifier = Modifier
) {
    val routineTip = when (profile.skinType) {
        "Yağlı" -> "Salisilik Asit ekleyin 🧪"
        "Kuru" -> "Hiyalüronik Asit ekleyin 💧"
        "Hassas" -> "Centella Asiatica tercih edin 🌿"
        else -> "C Vitamini ile canlandırın 🍊"
    }

    val makeupTip = when {
        profile.makeupPreference.contains("Doğal") -> "Soft Peach & Nude tonlar 🍑"
        profile.makeupPreference.contains("Mat") -> "Mat bitişli gözenek gizleyici 🧼"
        else -> "Dewy & Islak bitişli allık ✨"
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Card 1: Rutin Önerisi
        Card(
            modifier = Modifier
                .weight(1f)
                .height(130.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Rutin Önerisi",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = routineTip,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Card 2: Makyaj Uyumu
        Card(
            modifier = Modifier
                .weight(1f)
                .height(130.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF7D9D3), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = null,
                        tint = Color(0xFFC06D5E),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Makyaj Uyumu",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = makeupTip,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RemindersCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(NotificationHelper.PREFS_NAME, Context.MODE_PRIVATE) }

    var morningEnabled by remember { mutableStateOf(prefs.getBoolean(NotificationHelper.KEY_MORNING_ENABLED, true)) }
    var morningHour by remember { mutableStateOf(prefs.getInt(NotificationHelper.KEY_MORNING_HOUR, 8)) }
    var morningMinute by remember { mutableStateOf(prefs.getInt(NotificationHelper.KEY_MORNING_MINUTE, 0)) }

    var eveningEnabled by remember { mutableStateOf(prefs.getBoolean(NotificationHelper.KEY_EVENING_ENABLED, true)) }
    var eveningHour by remember { mutableStateOf(prefs.getInt(NotificationHelper.KEY_EVENING_HOUR, 21)) }
    var eveningMinute by remember { mutableStateOf(prefs.getInt(NotificationHelper.KEY_EVENING_MINUTE, 0)) }

    var weeklyEnabled by remember { mutableStateOf(prefs.getBoolean(NotificationHelper.KEY_WEEKLY_ENABLED, true)) }

    val formatTime = { hour: Int, minute: Int ->
        String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Bakım Hatırlatıcıları",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Rutinlerinizi aksatmamak için bildirimleri ayarlayın",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Morning Reminder Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = Color(0xFFF39C12),
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Sabah Bakımı",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (morningEnabled) {
                            Text(
                                text = "Saat: ${formatTime(morningHour, morningMinute)} (Dokun ve değiştir)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            morningHour = h
                                            morningMinute = m
                                            prefs.edit()
                                                .putInt(NotificationHelper.KEY_MORNING_HOUR, h)
                                                .putInt(NotificationHelper.KEY_MORNING_MINUTE, m)
                                                .apply()
                                            NotificationHelper.updateReminders(context)
                                        },
                                        morningHour,
                                        morningMinute,
                                        true
                                    )
                                    timePickerDialog.show()
                                }
                            )
                        } else {
                            Text(
                                text = "Kapalı",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                Switch(
                    checked = morningEnabled,
                    onCheckedChange = { isChecked ->
                        morningEnabled = isChecked
                        prefs.edit().putBoolean(NotificationHelper.KEY_MORNING_ENABLED, isChecked).apply()
                        NotificationHelper.updateReminders(context)
                    },
                    modifier = Modifier.testTag("morning_reminder_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Evening Reminder Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = null,
                        tint = Color(0xFF9B59B6),
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Akşam Bakımı",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (eveningEnabled) {
                            Text(
                                text = "Saat: ${formatTime(eveningHour, eveningMinute)} (Dokun ve değiştir)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    val timePickerDialog = TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            eveningHour = h
                                            eveningMinute = m
                                            prefs.edit()
                                                .putInt(NotificationHelper.KEY_EVENING_HOUR, h)
                                                .putInt(NotificationHelper.KEY_EVENING_MINUTE, m)
                                                .apply()
                                            NotificationHelper.updateReminders(context)
                                        },
                                        eveningHour,
                                        eveningMinute,
                                        true
                                    )
                                    timePickerDialog.show()
                                }
                            )
                        } else {
                            Text(
                                text = "Kapalı",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                Switch(
                    checked = eveningEnabled,
                    onCheckedChange = { isChecked ->
                        eveningEnabled = isChecked
                        prefs.edit().putBoolean(NotificationHelper.KEY_EVENING_ENABLED, isChecked).apply()
                        NotificationHelper.updateReminders(context)
                    },
                    modifier = Modifier.testTag("evening_reminder_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // 3. Weekly Reminder Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Haftalık Cilt Analizi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Her Pazar Saat 10:00",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = weeklyEnabled,
                    onCheckedChange = { isChecked ->
                        weeklyEnabled = isChecked
                        prefs.edit().putBoolean(NotificationHelper.KEY_WEEKLY_ENABLED, isChecked).apply()
                        NotificationHelper.updateReminders(context)
                    },
                    modifier = Modifier.testTag("weekly_reminder_switch")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(14.dp))

            // 4. Send Test Notification Button
            Button(
                onClick = {
                    NotificationHelper.sendImmediateTestNotification(
                        context,
                        "Cilt Bakım Asistanı ✨",
                        "Harika görünüyorsun! Cilt bakımı hatırlatıcıların başarıyla kuruldu. Rutinlerine sadık kalarak cildini şımartmaya devam et!"
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_test_notification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Şimdi Test Bildirimi Gönder", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Guided Walkthrough / Tour Step Data Structure
data class TourStep(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val highlightLabel: String
)

// Interactive and beautiful Walkthrough overlay screen
@Composable
fun WalkthroughOverlay(
    currentStep: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    skinProfile: SkinProfile
) {
    val steps = remember(skinProfile) {
        listOf(
            TourStep(
                title = "Cilt Profiliniz ✨",
                desc = "Cilt tipiniz, şikayetleriniz ve hedefleriniz burada özetlenir. Dilediğiniz zaman kalem simgesine dokunarak profil bilgilerinizi güncelleyebilirsiniz.",
                icon = Icons.Default.Face,
                highlightLabel = "Cilt Profil Özeti"
            ),
            TourStep(
                title = "Cilt Sağlığı Skorunuz 📈",
                desc = "Yapay zeka tarafından cildinizin nem dengesi, bariyer ve yağ oranına göre hesaplanan anlık sağlık skorunuzdur. İyileşme trendinizi buradan takip edebilirsiniz.",
                icon = Icons.Default.AutoAwesome,
                highlightLabel = "Yapay Zeka Skor Kartı"
            ),
            TourStep(
                title = "Günlük Akıllı Tavsiyeler 💡",
                desc = "Cildinize özel en faydalı bileşen önerileri (örn: Salisilik Asit veya Hiyalüronik Asit) ve makyaj uyum ipuçları günlük olarak güncellenir.",
                icon = Icons.Default.Favorite,
                highlightLabel = "Hızlı Akıllı Tavsiyeler"
            ),
            TourStep(
                title = "Kişiselleştirilmiş Bakım Rutinleri 🌿",
                desc = "Yapay zekanın cildinize özel olarak tasarladığı Sabah Bakım Rutini ve Makyaj Tavsiyeleri. Adımları genişleterek bakım adımlarını detaylıca görebilirsiniz.",
                icon = Icons.Default.Spa,
                highlightLabel = "Bakım ve Makyaj Rutini"
            ),
            TourStep(
                title = "Bakım Hatırlatıcıları ⏰",
                desc = "Rutinlerinizi aksatmamak için sabah ve akşam hatırlatıcı bildirimlerini etkinleştirip saatlerini kişiselleştirin.",
                icon = Icons.Default.NotificationsActive,
                highlightLabel = "Bildirim Kurulumu"
            ),
            TourStep(
                title = "Harika Özellikleri Keşfedin! 🚀",
                desc = "Alt menüdeki 'Öneriler' sekmesinden bütçe dostu krem fiyatlarını karşılaştırın, 'Günlük' ile cilt fotoğraflarınızı biriktirin ve 'Danışman' yapay zeka ile sohbete başlayın!",
                icon = Icons.Default.AutoAwesome,
                highlightLabel = "DermaAI Dünyasını Keşfedin!"
            )
        )
    }

    if (currentStep < 0 || currentStep >= steps.size) return

    val step = steps[currentStep]
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable(enabled = true, onClick = {}) // Block clicks underneath
            .testTag("walkthrough_overlay_step_$currentStep"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Highlighting pointer effect
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        CircleShape
                    )
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Highlighting Area label
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = step.highlightLabel.uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Walkthrough content card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Step progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hızlı Tanıtım Turu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${currentStep + 1} / ${steps.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Content
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = step.desc,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Linear Indicator
                    LinearProgressIndicator(
                        progress = (currentStep + 1) / steps.size.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )

                    // Navigation Actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onSkip,
                            modifier = Modifier.testTag("walkthrough_skip_btn")
                        ) {
                            Text(
                                "Turu Geç",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (currentStep > 0) {
                                OutlinedButton(
                                    onClick = onBack,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("walkthrough_back_btn"),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NavigateBefore,
                                        contentDescription = "Geri",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Geri", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = onNext,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("walkthrough_next_btn"),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (currentStep == steps.size - 1) "Kapat" else "İleri",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (currentStep < steps.size - 1) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.NavigateNext,
                                        contentDescription = "İleri",
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Tamamla",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

