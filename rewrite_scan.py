# -*- coding: utf-8 -*-
import codecs

content = """package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.theme.*

@Composable
fun IngredientScanScreen(
    viewModel: SkinCareViewModel,
    onNavigateToChat: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val analysisResult by viewModel.ingredientAnalysisResult.collectAsState()
    val isAnalyzing by viewModel.isScanLoading.collectAsState()
    var showCameraForScan by remember { mutableStateOf(false) }

    if (showCameraForScan) {
        CameraCaptureView(
            onPhotoCaptured = { file ->
                showCameraForScan = false
                viewModel.analyzeProductScan(file.absolutePath)
            },
            onDismiss = { showCameraForScan = false }
        )
        return
    }

    if (isAnalyzing) {
        Box(modifier = modifier.fillMaxSize().background(SurfacePage), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CircularProgressIndicator(color = Purple500)
                Text("Ürün içerikleri inceleniyor...", fontSize = 16.sp, color = Navy900)
            }
        }
        return
    }

    if (analysisResult != null) {
        // Result State
        Box(modifier = modifier.fillMaxSize().background(SurfacePage)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(38.dp).background(SurfaceCard, CircleShape).border(1.dp, BorderDefault, CircleShape).clickable { viewModel.clearScanAnalysis() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Navy700, modifier = Modifier.size(20.dp))
                    }
                    Text("Tarama sonucu", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Box(modifier = Modifier.size(38.dp).background(SurfaceCard, CircleShape).border(1.dp, BorderDefault, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Navy700, modifier = Modifier.size(19.dp))
                    }
                }
                
                // Result Card
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(20.dp))
                        .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Mint300, Mint100))).padding(horizontal = 18.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(26.dp).background(White, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(15.dp))
                        }
                        Text("Bu ürün sana uygun", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    }
                    
                    Row(modifier = Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.width(96.dp).height(118.dp).background(Lilac100, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Navy250, modifier = Modifier.size(32.dp))
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(progress = { 0.92f }, modifier = Modifier.fillMaxSize(), color = Purple500, trackColor = BorderDefault, strokeWidth = 8.dp)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("92", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("/100", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                                    }
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text("Niasinamid Serum", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                                    Text("The Ordinary · 30 ml", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Row(modifier = Modifier.background(Mint100, CircleShape).padding(horizontal = 11.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Green600, modifier = Modifier.size(13.dp))
                                Text("Gözenek tıkamaz", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Green600)
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().background(SurfaceTint).border(1.dp, BorderDefault).padding(horizontal = 18.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        Box(modifier = Modifier.size(22.dp).background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(13.dp))
                        }
                        Text("Karma cildin ve gözenek şikayetin için ideal. Sabah rutinine ekleyebilirsin.", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Why Section
                Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Neden?", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WhyItem("Niasinamid %10", "Sebum dengesi, gözenek görünümü", Icons.Default.CheckCircle, Mint100, Green600)
                        WhyItem("Çinko PCA %1", "Akne eğilimli bölgeler için yatıştırıcı", Icons.Default.CheckCircle, Mint100, Green600)
                        WhyItem("Fenoksietanol", "Hassas dönemlerde hafif tahriş yapabilir", Icons.Default.Info, Amber100, Amber600)
                    }
                    
                    var expanded by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(12.dp),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("18 içeriğin tamamını gör", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Purple600)
                        Spacer(modifier = Modifier.width(7.dp))
                        Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Purple600, modifier = Modifier.size(17.dp))
                    }
                    if (expanded) {
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().background(Lilac50, RoundedCornerShape(16.dp)).padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Aqua" to White, "Niacinamide" to Mint100, "Pentylene Glycol" to White,
                                "Zinc PCA" to Mint100, "Tamarindus Indica" to White, "Carrageenan" to White,
                                "Xanthan Gum" to White, "Phenoxyethanol" to Amber100, "Chlorphenesin" to White
                            ).forEach { (name, bg) ->
                                val fg = if (bg == Mint100) Green600 else if (bg == Amber100) Amber600 else Navy700
                                Box(modifier = Modifier.background(bg, CircleShape).border(1.dp, if (bg == White) BorderDefault else Color.Transparent, CircleShape).padding(horizontal = 10.dp, vertical = 5.dp)) {
                                    Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = fg)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Store
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().background(Brush.linearGradient(listOf(Blue300, Blue100)), RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.size(34.dp).background(White.copy(alpha = 0.8f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("₺189 · Gratis'te en ucuz", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                        Text("5 mağaza tarandı · kargo 3 gün", fontSize = 12.sp, color = Navy700)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
                }
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, SurfacePage)))
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Button(
                    onClick = { viewModel.clearScanAnalysis() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Purple500, Pink400)), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                            Text("Rutinime Ekle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                    }
                }
            }
        }
        return
    }

    // Default Initial State
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("İçerik & Kozmetik Hub", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Purple600)
            Text("Kozmetik ürün etiketlerini taratın, yeni ürün alırken uyumluluğunu sorun veya elinizdeki makyaj ve cilt bakımı envanterini yönetin.", fontSize = 12.sp, color = TextSecondary)
        }
        
        Column(
            modifier = Modifier.fillMaxWidth().background(Lilac200, RoundedCornerShape(16.dp)).border(1.dp, Purple300.copy(alpha=0.5f), RoundedCornerShape(16.dp)).padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Purple600, modifier = Modifier.size(18.dp))
                Text("Cilt Profilinize Göre Analiz", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
            }
            Text("Tip: Karma | Sorunlar: Akne & Sivilce | Hedef: Nemlendirme", fontSize = 11.sp, color = TextSecondary)
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HubTab("Kamera", Icons.Default.CameraAlt, true)
            HubTab("Metin", Icons.Default.Edit, false)
            HubTab("Ürün Sor", Icons.Default.Search, false)
            HubTab("Envanterim", Icons.Default.Inventory, false)
        }
        
        Column(
            modifier = Modifier.fillMaxWidth().background(SurfaceCard, RoundedCornerShape(20.dp)).border(1.dp, BorderDefault, RoundedCornerShape(20.dp)).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Etiketi Taratın", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy900)
            Text("Ürünün 'İçindekiler' listesini olabildiğince net çekin.\n\nKavisli şişelerde geniş açı kullanın. Kenarlara doğru eğilen yazıları AI tamamlar.", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)
            Button(
                onClick = { showCameraForScan = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kamerayı Başlat", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WhyItem(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color, fg: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceCard, RoundedCornerShape(16.dp)).border(1.dp, BorderDefault, RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(32.dp).background(bg, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(17.dp))
        }
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
            Text(desc, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun RowScope.HubTab(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean) {
    Column(
        modifier = Modifier.weight(1f).background(if (active) Purple600 else Color.Transparent, RoundedCornerShape(14.dp)).border(1.dp, if (active) Color.Transparent else BorderDefault, RoundedCornerShape(14.dp)).padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = null, tint = if (active) White else Purple600, modifier = Modifier.size(20.dp))
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (active) White else Purple600)
    }
}
"""

with codecs.open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "w", "utf-8") as f:
    f.write(content)
