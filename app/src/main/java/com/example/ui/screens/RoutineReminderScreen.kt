package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue600
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.Lilac100
import com.example.ui.theme.Lilac200
import com.example.ui.theme.Mint100
import com.example.ui.theme.Mint500
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy900
import com.example.ui.theme.Pink100
import com.example.ui.theme.Pink600
import com.example.ui.theme.Purple100
import com.example.ui.theme.Purple600
import com.example.ui.theme.Purple700
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfacePage
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.util.RoutineParser

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineReminderScreen(
    viewModel: SkinCareViewModel,
    period: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.skinProfile.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisError by viewModel.analysisError.collectAsState()
    val activeProfile = profile ?: return
    val isEvening = period == "evening"
    val parsedRoutine = remember(activeProfile.lastAnalysisRoutine) {
        RoutineParser.parse(activeProfile.lastAnalysisRoutine)
    }
    val steps = if (isEvening) parsedRoutine.eveningSteps else parsedRoutine.morningSteps
    var completedSteps by remember(period, activeProfile.lastAnalysisRoutine) {
        mutableStateOf<Set<Int>>(emptySet())
    }
    val progress = if (steps.isEmpty()) 0f else completedSteps.size.toFloat() / steps.size
    val accent = if (isEvening) Purple700 else Amber600
    val headerBackground = if (isEvening) Lilac100 else Amber100
    val periodIcon = if (isEvening) Icons.Default.NightsStay else Icons.Default.WbSunny
    val periodTitle = if (isEvening) "Akşam bakımın" else "Sabah bakımın"

    BackHandler(onBack = onNavigateBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Navy700)
            }
            Text("Kişisel bakım rehberi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy900)
        }

        Surface(color = headerBackground, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(54.dp).background(SurfaceCard, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(periodIcon, contentDescription = null, tint = accent, modifier = Modifier.size(28.dp))
                }
                Text(
                    text = activeProfile.userName.trim().takeIf { it.isNotEmpty() }
                        ?.let { "$periodTitle, $it" } ?: periodTitle,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    "AI analizinden oluşturulan kişisel adımlarını sırayla uygula.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ContextChip(activeProfile.skinType)
                    ContextChip(activeProfile.skincareGoal)
                }
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (steps.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bugünün adımları", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Text("Sıralamayı bozmadan ilerle", fontSize = 12.sp, color = TextMuted)
                    }
                    Text("${completedSteps.size}/${steps.size}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accent)
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = accent,
                    trackColor = Lilac200
                )

                steps.forEachIndexed { index, step ->
                    ReminderStepCard(
                        index = index,
                        text = step,
                        isCompleted = index in completedSteps,
                        onToggle = {
                            completedSteps = if (index in completedSteps) completedSteps - index else completedSteps + index
                        }
                    )
                }

                if (completedSteps.size == steps.size) {
                    Surface(color = Mint100, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Mint500)
                            Text("Bakım adımlarını tamamladın.", fontWeight = FontWeight.Bold, color = Navy900)
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderDefault),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Purple600, modifier = Modifier.size(36.dp))
                        Text("Bu vakit için kişisel adım bulunamadı", fontWeight = FontWeight.Bold, color = Navy900)
                        Text(
                            "Profiline göre sabah ve akşam planını yeniden oluşturabilirsin.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Button(
                            onClick = { viewModel.triggerFullAIAnalysis() },
                            enabled = !isAnalyzing,
                            colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Text("Kişisel rutin oluştur")
                            }
                        }
                        if (analysisError != null) {
                            Text(analysisError.orEmpty(), fontSize = 12.sp, color = Pink600)
                        }
                    }
                }
            }

            Surface(color = Purple100, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Tahriş veya beklenmeyen reaksiyon oluşursa ürünü bırak ve bir sağlık uzmanına danış.",
                    modifier = Modifier.padding(14.dp),
                    fontSize = 12.sp,
                    color = Navy700
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ContextChip(text: String) {
    Surface(color = SurfaceCard, shape = CircleShape, border = BorderStroke(1.dp, BorderDefault)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 12.sp, color = Navy700)
    }
}

@Composable
private fun ReminderStepCard(index: Int, text: String, isCompleted: Boolean, onToggle: () -> Unit) {
    val (icon, background, tint) = routineStepVisual(text)
    Card(
        onClick = onToggle,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, if (isCompleted) Purple600 else BorderDefault),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.size(46.dp).background(background, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(23.dp))
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Adım ${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tint)
                Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Navy900, lineHeight = 20.sp)
            }
            Box(
                modifier = Modifier.size(28.dp).background(if (isCompleted) Purple600 else Lilac100, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) Icon(Icons.Default.Check, contentDescription = "Tamamlandı", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

private fun routineStepVisual(text: String): Triple<ImageVector, Color, Color> {
    val lower = text.lowercase()
    return when {
        lower.contains("güneş") || lower.contains("spf") -> Triple(Icons.Default.WbSunny, Amber100, Amber600)
        lower.contains("temizle") || lower.contains("jel") || lower.contains("yıka") -> Triple(Icons.Default.WaterDrop, Blue100, Blue600)
        lower.contains("serum") || lower.contains("asit") || lower.contains("aktif") -> Triple(Icons.Default.Science, Purple100, Purple700)
        lower.contains("krem") || lower.contains("nem") -> Triple(Icons.Default.Spa, Mint100, Mint500)
        lower.contains("makyaj") || lower.contains("baz") -> Triple(Icons.Default.Brush, Pink100, Pink600)
        else -> Triple(Icons.Default.AutoAwesome, Lilac100, Purple600)
    }
}
