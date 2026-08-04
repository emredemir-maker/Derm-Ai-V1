package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.viewmodel.calculateDynamicSkinScore
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.R

@Composable
fun HomeScreen(
    viewModel: SkinCareViewModel,
    onNavigateToMakeupAnalysis: () -> Unit = {},
    onNavigateToFaceMap: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToDiary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeProfileState by viewModel.skinProfile.collectAsState()
    val scanAnalysis by viewModel.scanProfileAnalysis.collectAsState()
    
    var routineTab by remember { mutableIntStateOf(0) }
    var routineState by remember { mutableStateOf(listOf(true, true, false, false, false)) }
    
    val profile = activeProfileState ?: return

    val profileConcernsList = profile.skinConcerns.split(",").map { it.trim() }.filter { it.isNotBlank() }

    val score = remember(profile, scanAnalysis) {
        calculateDynamicSkinScore(profile, scanAnalysis)
    }
    val doneCount = routineState.count { it }

    val statusTitle = when {
        score < 65 -> "Yoğun Bakım İhtiyacı ⚠️"
        score in 65..75 -> "Denge & Onarım İhtiyacı 🎯"
        else -> "Işıldıyor ✨"
    }

    val statusDesc = when {
        scanAnalysis?.explanation?.isNotBlank() == true -> scanAnalysis!!.explanation
        profileConcernsList.isNotEmpty() -> "Ciltte ${profileConcernsList.take(3).joinToString(", ")} gibi odaklanılması gereken şikayetler mevcut."
        else -> "Nem dengesi iyi, günlük düzenli koruyucu bakım önerilir."
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(SurfacePage)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Greeting & Profile Photo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Merhaba, İpek 👋", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy900, lineHeight = 32.sp)
                Text(
                    text = if (score < 68) "Bugün cildinin özel bakıma ihtiyacı var 💆‍♀️" else "Bugün cildin dengede kalmaya devam ediyor ✨",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCard, CircleShape)
                        .border(1.dp, BorderDefault, CircleShape)
                        .clickable { onNavigateToDiary() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Cilt Günlüğü & Takvim", tint = Purple600, modifier = Modifier.size(20.dp))
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCard, CircleShape)
                        .border(1.dp, BorderDefault, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Navy700, modifier = Modifier.size(19.dp))
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(end=10.dp, top=9.dp).size(7.dp).background(Pink400, CircleShape).border(1.5.dp, White, CircleShape))
                }
                Box(
                    modifier = Modifier.size(40.dp).background(Lilac100, CircleShape).border(2.dp, White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Face, contentDescription = null, tint = Navy700)
                }
            }
        }
        
        // Streak Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(20.dp))
                .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("6", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Purple600)
                Text("günlük seri", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextMuted)
            }
            
            Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderDefault))
            Spacer(modifier = Modifier.width(12.dp))
            
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                listOf("P" to true, "S" to true, "Ç" to true, "P" to true, "C" to true, "C" to true, "P" to false).forEachIndexed { i, (day, isDone) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (isDone) {
                            Box(modifier = Modifier.size(26.dp).background(Brush.horizontalGradient(listOf(Pink400, Blue400)), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(14.dp))
                            }
                        } else {
                            Box(modifier = Modifier.size(26.dp).background(Purple100, CircleShape).border(1.5.dp, Purple300, CircleShape))
                        }
                        Text(day, fontSize = 10.sp, fontWeight = if (isDone) FontWeight.Medium else FontWeight.Bold, color = if (isDone) TextMuted else Purple600)
                    }
                }
            }
        }
        
        // Score Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(24.dp))
                .border(1.dp, BorderDefault, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cilt Sağlığı Skoru", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Row(
                        modifier = Modifier.background(Mint100, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Green600, modifier = Modifier.size(12.dp))
                        Text("+4 bu hafta", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Green600)
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(modifier = Modifier.size(112.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = Purple500,
                            trackColor = BorderDefault,
                            strokeWidth = 10.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(score.toString(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("/100", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(statusTitle, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = if (score < 65) Rose600 else Navy900)
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = if (score < 65) Rose600 else Purple500, modifier = Modifier.size(16.dp))
                        }
                        Text(statusDesc, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
                    }
                }
                
                val puruzsuzukVal = "%${score.coerceIn(38, 95)}"
                val bariyerVal = if (score < 65) "Zayıf" else if (score < 78) "Hassas" else "Güçlü"
                val sebumVal = if (profile.skinType.lowercase().contains("yağlı")) "Aşırı" else if (profile.skinType.lowercase().contains("kuru")) "Düşük" else "Dengesiz"

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBox(title = "PÜRÜZSÜZLÜK", value = puruzsuzukVal, colorTitle = if (score < 65) Rose600 else Blue600, colorValue = Navy900, bgColor = if (score < 65) Rose100 else Blue100, modifier = Modifier.weight(1f))
                    MetricBox(title = "BARİYER", value = bariyerVal, colorTitle = if (score < 65) Rose600 else Green600, colorValue = Navy900, bgColor = if (score < 65) Rose100 else Mint100, modifier = Modifier.weight(1f))
                    MetricBox(title = "SEBUM", value = sebumVal, colorTitle = Amber600, colorValue = Navy900, bgColor = Amber100, modifier = Modifier.weight(1f))
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceTint)
                    .border(1.dp, BorderDefault)
                    .clickable { onNavigateToMakeupAnalysis() }
                    .padding(vertical = 13.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(22.dp).background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
                }
                Text("AI Makyaj Analizi yap", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Purple600, modifier = Modifier.size(18.dp))
            }
        }

        // Cilt Bakım & Rutin Uygulama Rehberi Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToGuide() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderDefault)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Purple100, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Purple700, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Cilt Bakım & Rutin Uygulama Rehberi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    Text("Sabah & akşam adımları, katmanlama kuralları ve yapılmaması gerekenler", fontSize = 11.sp, color = TextSecondary)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Purple600, modifier = Modifier.size(20.dp))
            }
        }

        // Cilt Günlüğü & Takvim Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToDiary() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderDefault)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Mint100, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Green600, modifier = Modifier.size(22.dp))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Cilt Günlüğü & Takvim", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    Text("Aylık ısı haritası, gün bazlı cilt kayıtları ve değişim takibi", fontSize = 11.sp, color = TextSecondary)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
            }
        }

        // Interactive Face Map Section
        val scanAnalysis by viewModel.scanProfileAnalysis.collectAsState()
        val lastPhotoPath by viewModel.lastScannedPhotoPath.collectAsState()
        FaceMapDiagnosticCard(
            analysisResult = scanAnalysis,
            photoPath = lastPhotoPath,
            userConcerns = profileConcernsList,
            onApplyToProfile = { skinType, concerns, goal ->
                viewModel.saveSkinProfile(
                    skinType = skinType,
                    skinConcerns = concerns,
                    skincareGoal = goal,
                    makeupPreference = "Doğal & Hafif (Yok Gibi Makyaj)"
                )
            },
            onRetakePhoto = onNavigateToFaceMap
        )
        
        // Checklist
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bugün ne yapmalıyım?", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                Row(
                    modifier = Modifier.background(Lilac100, CircleShape).border(1.dp, BorderDefault, CircleShape).padding(3.dp)
                ) {
                    TabButton("Sabah", Icons.Default.WbSunny, routineTab == 0) { routineTab = 0 }
                    TabButton("Akşam", Icons.Default.NightsStay, routineTab == 1) { routineTab = 1 }
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LinearProgressIndicator(
                    progress = { doneCount / 5f },
                    modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                    color = Purple500,
                    trackColor = Lilac200
                )
                Text("$doneCount/5 tamamlandı", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Purple600)
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChecklistItem(
                    title = "Nazik Köpük Temizleyici", desc = "Amino asitli · pH dengeli",
                    icon = Icons.Default.WaterDrop, iconBg = SurfaceBrandSoft, iconTint = Purple600,
                    isDone = routineState[0], onToggle = {
                        val m = routineState.toMutableList(); m[0] = !m[0]; routineState = m
                    }
                )
                ChecklistItem(
                    title = "Niasinamid & Çinko Serum", desc = "Gözenek ve sebum dengesi için",
                    icon = Icons.Default.Science, iconBg = Blue100, iconTint = Blue600,
                    isDone = routineState[1], onToggle = {
                        val m = routineState.toMutableList(); m[1] = !m[1]; routineState = m
                    }
                )
                ChecklistItem(
                    title = "Dengeleyici Jel-Krem", desc = "Skualen · Centella · yeşil çay",
                    icon = Icons.Default.Spa, iconBg = Mint100, iconTint = Mint500,
                    isDone = routineState[2], onToggle = {
                        val m = routineState.toMutableList(); m[2] = !m[2]; routineState = m
                    }
                )
                ChecklistItem(
                    title = "Güneş Kremi SPF 50", desc = "Atlamak skorunu düşürür",
                    icon = Icons.Default.WbSunny, iconBg = Amber100, iconTint = Amber600,
                    isDone = routineState[3], onToggle = {
                        val m = routineState.toMutableList(); m[3] = !m[3]; routineState = m
                    }
                )
                ChecklistItem(
                    title = "Makyaj: Soft Peach", desc = "Gözenek dostu yarı mat baz",
                    icon = Icons.Default.Brush, iconBg = Pink100, iconTint = Pink600,
                    isDone = routineState[4], onToggle = {
                        val m = routineState.toMutableList(); m[4] = !m[4]; routineState = m
                    }
                )
            }
            
            if (doneCount == 5) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(SurfaceBrandSoft, RoundedCornerShape(16.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, tint = Purple600, modifier = Modifier.size(18.dp))
                    Text("Bugünü tamamladın — seri 7 güne çıktı ✨", fontSize = 12.sp, color = Purple700)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(130.dp))
    }
}

@Composable
fun MetricBox(title: String, value: String, colorTitle: Color, colorValue: Color, bgColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = colorTitle)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colorValue)
    }
}

@Composable
fun TabButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(if (selected) White else Color.Transparent, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Navy900, modifier = Modifier.size(14.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
    }
}

@Composable
fun ChecklistItem(
    title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color, iconTint: Color, isDone: Boolean, onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDefault, RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp).background(iconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
            Text(desc, fontSize = 12.sp, color = TextMuted)
        }
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(if (isDone) Purple500 else Color.Transparent, CircleShape)
                .border(if (isDone) 0.dp else 2.dp, if (isDone) Color.Transparent else BorderStrong, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
            }
        }
    }
}
