package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.theme.*

@Composable
fun DiaryScreen(
    viewModel: SkinCareViewModel,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeProfile by viewModel.skinProfile.collectAsState()
    
    val tint = listOf(Color.Transparent, Rose100, Amber100, Blue100, Mint100, Mint300)
    val ink = listOf(TextDisabled, Rose600, Amber600, Blue600, Green600, Green600)
    
    val days = listOf(
        27 to 0, 28 to 0, 29 to 0, 30 to 0, 31 to 0, 1 to 3, 2 to 4,
        3 to 4, 4 to 2, 5 to 2, 6 to 3, 7 to 4, 8 to 5, 9 to 5,
        10 to 3, 11 to 1, 12 to 2, 13 to 3, 14 to 4, 15 to 4, 16 to 5,
        17 to 4, 18 to 3, 19 to 4, 20 to 5, 21 to 5, 22 to 4, 23 to 0,
        24 to 0, 25 to 0, 26 to 0, 27 to 0, 28 to 0, 29 to 0, 30 to 0
    )
    
    Box(modifier = modifier.fillMaxSize().background(SurfacePage)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 130.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Navy900)
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Cilt Günlüğü & Takvim", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy900, lineHeight = 30.sp)
                        Text("Ağustos 2026 · 18 kayıt · ortalama 4,1 / 5", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCard, CircleShape)
                        .border(1.dp, BorderDefault, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Purple600, modifier = Modifier.size(20.dp))
                }
            }
            
            // Heatmap Calendar
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .background(SurfaceCard, RoundedCornerShape(24.dp))
                    .border(1.dp, BorderDefault, RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(30.dp).background(Lilac100, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Navy700, modifier = Modifier.size(17.dp))
                    }
                    Text("Ağustos 2026", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Box(modifier = Modifier.size(30.dp).background(Lilac100, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(17.dp))
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("P", "S", "Ç", "P", "C", "C", "P").forEach {
                        Text(it, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    }
                }
                
                val rows = days.chunked(7)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    rows.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            week.forEach { (d, s) ->
                                val isSelected = d == 21 && s > 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(tint[s], RoundedCornerShape(10.dp))
                                        .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Purple500 else Color.Transparent, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(d.toString(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ink[s])
                                }
                            }
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 2.dp)) {
                    Text("Zayıf", fontSize = 12.sp, color = TextMuted)
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf(Rose100, Amber100, Blue100, Mint100, Mint300).forEach { bg ->
                            Box(modifier = Modifier.weight(1f).height(8.dp).background(bg, RoundedCornerShape(3.dp)))
                        }
                    }
                    Text("Işıltılı", fontSize = 12.sp, color = TextMuted)
                }
            }
            
            // Insight Note
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .background(Mint100, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(34.dp).background(White.copy(alpha = 0.8f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text("Son 10 gün üst üste iyi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Text("SPF'i düzenli sürdüğün haftalarda skorun ortalama +6", fontSize = 12.sp, color = Navy700)
                }
            }
            
            // Detailed Day Card
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("21 Ağustos, Cuma", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Box(modifier = Modifier.background(Mint100, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("Işıltılı · 5/5", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Green600)
                    }
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(20.dp))
                        .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Lilac100)) {
                            Box(modifier = Modifier.padding(10.dp).background(SurfaceCard.copy(alpha = 0.8f), CircleShape).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text("12 Ağu", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Navy700)
                            }
                        }
                        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(White))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Lilac200)) {
                            Box(modifier = Modifier.padding(10.dp).background(SurfaceCard.copy(alpha = 0.8f), CircleShape).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text("Bugün", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Navy700)
                            }
                        }
                    }
                    
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("“Yeni jel-kremi 9 gündür kullanıyorum, T-bölgesi öğlene kadar mat kalıyor.”", fontSize = 14.sp, color = Navy700, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.background(Blue100, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Blue600, modifier = Modifier.size(12.dp))
                                Text("2,1 L su", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Blue600)
                            }
                            Row(modifier = Modifier.background(Purple100, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Icon(Icons.Default.NightsStay, contentDescription = null, tint = Purple700, modifier = Modifier.size(12.dp))
                                Text("7 sa uyku", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Purple700)
                            }
                            Row(modifier = Modifier.background(Mint100, CircleShape).padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(12.dp))
                                Text("Rutin tam", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Green600)
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceTint)
                            .border(1.dp, BorderDefault)
                            .padding(vertical = 13.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(22.dp).background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
                        }
                        Text("AI: 9 günlük seride gözenek görünümü belirgin şekilde azalmış.", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(56.dp)
                .background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = White, modifier = Modifier.size(24.dp))
        }
    }
}
