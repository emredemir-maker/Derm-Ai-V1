package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.api.IngredientAnalysisResponse
import com.example.data.api.IngredientIssue
import com.example.data.api.IngredientBenefit
import com.example.ui.viewmodel.SkinCareViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientScanScreen(
    viewModel: SkinCareViewModel,
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val analysisResult by viewModel.ingredientAnalysis.collectAsState()
    val isLoading by viewModel.isIngredientLoading.collectAsState()
    val profile by viewModel.skinProfile.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0 = Kamera, 1 = Manuel Metin, 2 = Ürün Sor, 3 = Envanterim
    var temporaryPhotoPath by remember { mutableStateOf<String?>(null) }
    var showCameraView by remember { mutableStateOf(false) }
    var manualIngredientsInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            IngredientScanningLoadingView()
        } else if (analysisResult != null) {
            IngredientAnalysisResultView(
                result = analysisResult!!,
                userSkinType = profile?.skinType ?: "Normal",
                onReset = {
                    viewModel.clearIngredientAnalysis()
                    temporaryPhotoPath = null
                },
                onAskAI = { productName ->
                    val query = "${productName} kozmetik ürününün içeriğini analiz ettim ve cildimle uyumunu inceledim. Bana bu ürünün içeriğindeki aktif maddeler ve kullanımı hakkında daha fazla bilgi verebilir misin?"
                    onNavigateToChat(query)
                }
            )
        } else if (showCameraView) {
            IngredientCameraCaptureView(
                onPhotoCaptured = { file ->
                    showCameraView = false
                    temporaryPhotoPath = file.absolutePath
                    viewModel.analyzeProductIngredients(file.absolutePath, null)
                },
                onDismiss = {
                    showCameraView = false
                }
            )
        } else {
            // Main Dashboard View with selector
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Feature Header
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "İçerik & Kozmetik Hub",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = "Kozmetik ürün etiketlerini taratın, yeni ürün alırken uyumluluğunu sorun veya elinizdeki makyaj ve cilt bakımı envanterini yönetin.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }

                // Profile Summary Banner for Context
                profile?.let { activeProfile ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Cilt Profilinize Göre Analiz",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Tip: ${activeProfile.skinType} | Sorunlar: ${activeProfile.skinConcerns} | Hedef: ${activeProfile.skincareGoal}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Mode Selector Segmented Tab
                ScrollableTabRow(
                    selectedTabIndex = activeSubTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 0.dp,
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = activeSubTab == 0,
                        onClick = { activeSubTab = 0 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Kamera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_camera_scan")
                    )
                    Tab(
                        selected = activeSubTab == 1,
                        onClick = { activeSubTab = 1 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Metin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_text_input")
                    )
                    Tab(
                        selected = activeSubTab == 2,
                        onClick = { activeSubTab = 2 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocalMall, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Ürün Sor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_ask_product")
                    )
                    Tab(
                        selected = activeSubTab == 3,
                        onClick = { activeSubTab = 3 },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Envanterim", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.testTag("tab_inventory")
                    )
                }

                // Screen Body depending on sub-tab
                if (activeSubTab == 0) {
                    // Camera Scan Interface
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Etiketi Taratın",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Ürünün 'İçindekiler' (Ingredients) listesini olabildiğince net çekin.\n\n📷 Kavisli / Yuvarlak Şişeler:\nMetnin tamamının görünmesi için kamerayı biraz uzaklaştırıp geniş açı kullanın. Kenarlara doğru eğilen yazıları AI otomatik tamamlamaya çalışacaktır.\n\n💡 Alternatif: Çok silikse 'Metin Girişi' sekmesini kullanın.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Start,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { showCameraView = true },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("launch_camera_scanner_button")
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kamerayı Başlat", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (activeSubTab == 1) {
                    // Manual Text Paste Interface
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "İçindekiler Listesini Yapıştırın",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            OutlinedTextField(
                                value = manualIngredientsInput,
                                onValueChange = { manualIngredientsInput = it },
                                placeholder = {
                                    Text(
                                        "Örn: Aqua, Glycerin, Niacinamide, Salicylic Acid, Centella Asiatica Extract, Sodium Hyaluronate, Phenoxyethanol...",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .testTag("ingredients_input_field"),
                                textStyle = TextStyle(fontSize = 13.sp),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Button(
                                onClick = {
                                    if (manualIngredientsInput.isNotBlank()) {
                                        viewModel.analyzeProductIngredients(null, manualIngredientsInput)
                                    }
                                },
                                enabled = manualIngredientsInput.isNotBlank(),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("analyze_manual_text_button")
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("İçerikleri Analiz Et", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else if (activeSubTab == 2) {
                    // Alışveriş Danışmanı (Pre-purchase Advisor)
                    val advice by viewModel.purchaseAdvice.collectAsState()
                    val isAdviceLoading by viewModel.isPurchaseAdviceLoading.collectAsState()
                    var askProductName by remember { mutableStateOf("") }
                    var askBrandName by remember { mutableStateOf("") }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Yeni Ürün Alırken Danışın",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Almayı düşündüğünüz makyaj veya kozmetik ürünün adını ve markasını yazın. Yapay zekamız cildinize uygunluğunu, riskleri ve F/P değerini dürüstçe söylesin.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                            
                            OutlinedTextField(
                                value = askBrandName,
                                onValueChange = { askBrandName = it },
                                label = { Text("Marka Adı") },
                                placeholder = { Text("Örn: L'Oreal, La Roche Posay, Mac") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            OutlinedTextField(
                                value = askProductName,
                                onValueChange = { askProductName = it },
                                label = { Text("Ürün Adı") },
                                placeholder = { Text("Örn: Red Peeling, Hyaluronic Acid Serum") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = { viewModel.checkPurchaseAdvice(askProductName, askBrandName) },
                                enabled = askProductName.isNotBlank() && !isAdviceLoading,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                if (isAdviceLoading) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Satın Alım Uygunluğunu Sor", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (advice != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(advice!!.brand.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(advice!!.productName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Text(
                                            "%${advice!!.suitabilityScore} Uyum",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                val verdictColor = when (advice!!.verdict) {
                                    "ALMALISINIZ", "ALINABİLİR" -> Color(0xFFE8F5E9)
                                    "DİKKATLİ KULLANMALISINIZ", "DİKKATLİ OLUN", "DİKKATLİ KULLANIN" -> Color(0xFFFFF3E0)
                                    else -> Color(0xFFFFEBEE)
                                }
                                val verdictTextColor = when (advice!!.verdict) {
                                    "ALMALISINIZ", "ALINABİLİR" -> Color(0xFF2E7D32)
                                    "DİKKATLİ KULLANMALISINIZ", "DİKKATLİ OLUN", "DİKKATLİ KULLANIN" -> Color(0xFFE65100)
                                    else -> Color(0xFFC62828)
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = verdictColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = verdictTextColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = advice!!.verdict,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = verdictTextColor
                                        )
                                    }
                                }

                                Text(advice!!.reasoning, fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurface)

                                if (advice!!.positiveIngredients.isNotEmpty()) {
                                    Text("Olumlu Maddeler:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    advice!!.positiveIngredients.forEach { ing ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(ing, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                if (advice!!.riskyIngredients.isNotEmpty()) {
                                    Text("İçerik Riskleri:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                    advice!!.riskyIngredients.forEach { ing ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(ing, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                if (advice!!.alternativeSuggestions.isNotEmpty()) {
                                    Text("Alternatif Ürün Önerileri:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    advice!!.alternativeSuggestions.forEach { alt ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(alt, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.clearPurchaseAdvice() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Analizi Temizle", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else if (activeSubTab == 3) {
                    // Makyaj & Bakım Envanterim (Cosmetics Inventory)
                    val inventoryItems by viewModel.inventoryItems.collectAsState()
                    val weeklyAnalysis by viewModel.weeklyInventoryCheck.collectAsState()
                    val isWeeklyLoading by viewModel.isWeeklyInventoryLoading.collectAsState()

                    var showAddDialog by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Haftalık Akıllı Ürün Taraması", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Envanterinizdeki tüm kozmetik ve makyaj malzemelerini birlikte tarayarak madde çakışmalarını tespit edin ve haftalık rutin rehberinizi oluşturun.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { showAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ürün Ekle", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.runWeeklyInventoryCheck() },
                                    enabled = inventoryItems.isNotEmpty() && !isWeeklyLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isWeeklyLoading) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary, modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("AI Taramayı Başlat", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (showAddDialog) {
                        var prodName by remember { mutableStateOf("") }
                        var prodBrand by remember { mutableStateOf("") }
                        var prodType by remember { mutableStateOf("Cilt Bakımı") }
                        var prodCategory by remember { mutableStateOf("Nemlendirici") }
                        var prodLife by remember { mutableStateOf("12") }
                        var prodNotes by remember { mutableStateOf("") }

                        AlertDialog(
                            onDismissRequest = { showAddDialog = false },
                            title = { Text("Yeni Ürün Ekle", fontWeight = FontWeight.Bold) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                                    OutlinedTextField(
                                        value = prodBrand,
                                        onValueChange = { prodBrand = it },
                                        label = { Text("Marka") },
                                        placeholder = { Text("Örn: L'Oreal, The Ordinary") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = prodName,
                                        onValueChange = { prodName = it },
                                        label = { Text("Ürün Adı") },
                                        placeholder = { Text("Örn: Glikolik Asit Tonik") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        listOf("Cilt Bakımı", "Makyaj").forEach { t ->
                                            FilterChip(
                                                selected = prodType == t,
                                                onClick = { prodType = t },
                                                label = { Text(t) }
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = prodCategory,
                                        onValueChange = { prodCategory = it },
                                        label = { Text("Kategori (Temizleyici, Fondöten, Ruj vb.)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = prodLife,
                                        onValueChange = { prodLife = it },
                                        label = { Text("Kullanım Ömrü (Açıldıktan Sonra Ay)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = prodNotes,
                                        onValueChange = { prodNotes = it },
                                        label = { Text("Not (İsteğe Bağlı)") },
                                        placeholder = { Text("Örn: Sadece geceleri") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (prodName.isNotBlank() && prodBrand.isNotBlank()) {
                                            viewModel.addInventoryItem(
                                                name = prodName,
                                                brand = prodBrand,
                                                type = prodType,
                                                category = prodCategory,
                                                shelfLifeMonths = prodLife.toIntOrNull() ?: 12,
                                                notes = prodNotes.ifBlank { null }
                                            )
                                            showAddDialog = false
                                        }
                                    },
                                    enabled = prodName.isNotBlank() && prodBrand.isNotBlank()
                                ) {
                                    Text("Kaydet")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddDialog = false }) {
                                    Text("İptal")
                                }
                            }
                        )
                    }

                    if (weeklyAnalysis != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Haftalık Rutin & Envanter Değerlendirmesi", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 14.sp)
                                }
                                
                                Text(weeklyAnalysis!!.generalAnalysis, fontSize = 12.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)

                                if (weeklyAnalysis!!.ingredientConflicts.isNotEmpty()) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("⚠️ ETKİN MADDE ÇAKIŞMALARI (UYARILAR):", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFC62828))
                                            weeklyAnalysis!!.ingredientConflicts.forEach { conf ->
                                                Column {
                                                    Text("${conf.productA} ❌ ${conf.productB}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                                    Text(conf.conflictReason, fontSize = 11.sp, color = Color(0xFF5D4037))
                                                    Text("Çözüm: ${conf.solution}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                }
                                                HorizontalDivider(color = Color(0xFFEF9A9A), modifier = Modifier.padding(vertical = 4.dp))
                                            }
                                        }
                                    }
                                }

                                if (weeklyAnalysis!!.routineSuggestions.isNotEmpty()) {
                                    Text("Önerilen Haftalık Kullanım Planı:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    weeklyAnalysis!!.routineSuggestions.forEach { rout ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Text(rout.timeOfDay, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            rout.steps.forEachIndexed { i, step ->
                                                Text("${i + 1}. $step", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                }

                                if (weeklyAnalysis!!.missingItems.isNotEmpty()) {
                                    Text("Envanterinizde Eksik Olan Temel Basamaklar:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    weeklyAnalysis!!.missingItems.forEach { m ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(m, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Text(
                                    text = "Gelecek Haftanın Cilt Hedefi: ${weeklyAnalysis!!.nextWeekFocus}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )

                                Button(
                                    onClick = { viewModel.clearWeeklyInventoryCheck() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Analizi Temizle", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Text("Envanterimdeki Ürünler (${inventoryItems.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    
                    if (inventoryItems.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Envanteriniz Henüz Boş", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Sahip olduğunuz makyaj ve bakım malzemelerini ekleyerek tarih takibi ve içerik analizi yapın.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            inventoryItems.forEach { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(item.brand.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                SuggestionBadge(
                                                    text = item.type,
                                                    containerColor = if (item.type == "Makyaj") Color(0xFFF3E5F5) else Color(0xFFE3F2FD),
                                                    textColor = if (item.type == "Makyaj") Color(0xFF7B1FA2) else Color(0xFF0D47A1)
                                                )
                                                SuggestionBadge(
                                                    text = "%${item.compatibilityScore} Uyum",
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            val openMillis = item.openedDate
                                            val expiryMillis = openMillis + (item.shelfLifeMonths.toLong() * 30L * 24L * 60L * 60L * 1000L)
                                            val remainingDays = ((expiryMillis - System.currentTimeMillis()) / (24L * 60L * 60L * 1000L)).toInt()
                                            
                                            if (remainingDays <= 0) {
                                                Text("⚠️ SKT GEÇTİ! (Hemen Atın)", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                                            } else {
                                                val remainingMonths = remainingDays / 30
                                                val dateStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(openMillis))
                                                Text("Açılış: $dateStr | SKT'ye Kalan: $remainingMonths Ay ($remainingDays Gün)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            if (!item.notes.isNullOrBlank()) {
                                                Text("Not: ${item.notes}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                        
                                        IconButton(onClick = { viewModel.deleteInventoryItem(item.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Educational Skincare Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text("Biliyor muydunuz?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(
                            text = "Kozmetik içerik listelerinde (INCI), maddeler yoğunluk sırasına göre en yüksekten en düşüğe doğru sıralanır. İlk 5 içerik genellikle ürünün %80'inden fazlasını oluşturur. Analizimiz bu konsantrasyonu da dikkate almaktadır.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientScanningLoadingView() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    // Subtitle text rotation for responsive feedback
    val messages = listOf(
        "Kozmetik etiketindeki içerikler taranıyor...",
        "Bileşenler kozmetik veritabanıyla eşleştiriliyor...",
        "Alerjen ve irritan maddeler ayıklanıyor...",
        "Cilt tipinizle uyumluluk puanı hesaplanıyor...",
        "Hedefe yönelik aktif bileşenler inceleniyor...",
        "Dermatolojik tavsiyeler hazırlanıyor..."
    )
    var currentMsgIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2200)
            currentMsgIndex = (currentMsgIndex + 1) % messages.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(80.dp)
            )
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Yapay Zeka Analiz Ediyor...",
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        AnimatedContent(
            targetState = messages[currentMsgIndex],
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "loading_text"
        ) { text ->
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientAnalysisResultView(
    result: IngredientAnalysisResponse,
    userSkinType: String,
    onReset: () -> Unit,
    onAskAI: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // Determine color based on compatibility score
    val compatibilityColor = when {
        result.compatibilityScore >= 80 -> Color(0xFF4CAF50) // Green
        result.compatibilityScore >= 50 -> Color(0xFFFF9800) // Orange/Yellow
        else -> Color(0xFFF44336) // Red
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Upper Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onReset,
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Geri Dön")
            }
            Text(
                text = "Analiz Sonucu",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { onAskAI(result.productName) },
                colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Sohbet", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Product Identity and Score Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val confidenceColor = when {
                        result.confidenceScore >= 80 -> Color(0xFF4CAF50)
                        result.confidenceScore >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                    Surface(
                        color = confidenceColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "%${result.confidenceScore} AI Analiz Güvenilirliği",
                            color = confidenceColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = result.productName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Cilt Tipi Uyumluluğu ($userSkinType)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Gauge/Circle Score Representation
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    // Score Background Track
                    CircularProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        strokeWidth = 8.dp
                    )
                    // Actual Score Indicator
                    CircularProgressIndicator(
                        progress = { result.compatibilityScore / 100f },
                        modifier = Modifier.fillMaxSize(),
                        color = compatibilityColor,
                        strokeWidth = 8.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "%${result.compatibilityScore}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = compatibilityColor
                        )
                        Surface(
                            color = compatibilityColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = result.compatibilityLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = compatibilityColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Compatibility Explanation
                Text(
                    text = result.compatibilityExplanation,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Allergens and Irritants Warnings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (result.allergensAndIrritants.isNotEmpty()) Color(0xFFE53935) else Color(0xFF4CAF50),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Alerjenler ve İrritan Maddeler",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (result.allergensAndIrritants.isEmpty()) {
                    Text(
                        text = "Harika! Bu üründe cilt profilinize zarar verebilecek veya hassasiyet yaratabilecek herhangi bir alerjen veya irritan madde tespit edilmedi.",
                        fontSize = 12.sp,
                        color = Color(0xFF4CAF50),
                        lineHeight = 18.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        result.allergensAndIrritants.forEach { issue ->
                            val severityColor = when (issue.severity.lowercase()) {
                                "yüksek", "high" -> Color(0xFFE53935)
                                "orta", "medium" -> Color(0xFFFF9800)
                                else -> Color(0xFF3F51B5)
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(severityColor.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .border(BorderStroke(0.5.dp, severityColor.copy(alpha = 0.2f)), RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = issue.ingredientName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        color = severityColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = issue.severity.uppercase(),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = severityColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = issue.riskDescription,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Beneficial Ingredients Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFE6C15C),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Faydalı Aktif Bileşenler",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (result.beneficialIngredients.isEmpty()) {
                    Text(
                        text = "Bu üründe cilt hedefinize doğrudan etki edecek belirgin aktif bileşenler tespit edilmedi, ancak temel nemlendiriciler bulunabilir.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        result.beneficialIngredients.forEach { benefit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Column {
                                    Text(
                                        text = benefit.ingredientName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = benefit.benefitDescription,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Final Verdict and Usage Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Genel Değerlendirme & Öneri",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = result.finalVerdict,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFE6C15C), modifier = Modifier.size(16.dp))
                        Text("Kullanım İpucu:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = result.usageTips,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // All Detected Ingredients List (Chips Layout)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tespit Edilen Tüm İçerikler (${result.detectedIngredients.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                result.detectedIngredients.forEach { ingredient ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = ingredient,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Yeni Tarama", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onAskAI(result.productName) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Danışmana Sor", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun IngredientCameraCaptureView(
    onPhotoCaptured: (File) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        val lensFacing = CameraSelector.LENS_FACING_BACK // Always rear camera for labels
        var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }

        val preview = remember { Preview.Builder().build() }
        val imageCapture = remember { ImageCapture.Builder().build() }
        val cameraSelector = remember {
            CameraSelector.Builder().requireLensFacing(lensFacing).build()
        }

        val previewView = remember { PreviewView(context) }

        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

        DisposableEffect(Unit) {
            val cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (e: Exception) {
                Log.e("IngredientCamera", "Use case binding failed", e)
            }

            onDispose {
                try {
                    cameraProvider.unbindAll()
                } catch (e: Exception) {
                    Log.e("IngredientCamera", "Error unbinding on dispose", e)
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Camera Live Viewfinder
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Document/Label Scanner Rectangle Cutout Overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Entire background dark overlay
                    val rectPath = Path().apply {
                        addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                    }

                    // Centered horizontal rectangle for product label scanning
                    val rectWidth = canvasWidth * 0.85f
                    val rectHeight = canvasHeight * 0.35f
                    val left = (canvasWidth - rectWidth) / 2f
                    val top = (canvasHeight - rectHeight) / 2.2f
                    val scannerRect = Rect(left, top, left + rectWidth, top + rectHeight)

                    val cutoutPath = Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = scannerRect,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                            )
                        )
                    }

                    // Subtract cutout from full overlay
                    val maskPath = Path.combine(
                        PathOperation.Difference,
                        rectPath,
                        cutoutPath
                    )

                    drawPath(
                        path = maskPath,
                        color = Color.Black.copy(alpha = 0.65f)
                    )

                    // Draw golden focus borders/guides
                    drawRoundRect(
                        color = Color(0xFFE6C15C),
                        topLeft = Offset(left, top),
                        size = Size(rectWidth, rectHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 15f), 0f)
                        )
                    )
                }

                // Guide HUD text
                Text(
                    text = "İÇERİK ETİKETİNİ ÇERÇEVEYE SIĞDIRIN",
                    color = Color(0xFFE6C15C),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 180.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // Top Bar Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kapat", tint = Color.White)
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "İçerik Etiketi Tarayıcı",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yuvarlak şişeler için kamerayı biraz uzaklaştırın",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                IconButton(
                    onClick = {
                        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                            ImageCapture.FLASH_MODE_ON
                        } else {
                            ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = if (flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flaş",
                        tint = Color.White
                    )
                }
            }

            // Bottom Shutter controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(Color.White, CircleShape)
                        .clickable {
                            val storageDir = context.cacheDir
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                            val photoFile = File.createTempFile("Ingredients_${timeStamp}_", ".jpg", storageDir)

                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture.flashMode = flashMode

                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        onPhotoCaptured(photoFile)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("IngredientCamera", "Error capturing image", exception)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .border(3.dp, Color.Black, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    } else {
        // Permission screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Kamera İzni Gerekli",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Kozmetik ürün etiketini kamera ile tarayabilmek için uygulamanın kameraya erişmesine izin vermelisiniz.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Kamera İznini Onayla", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", fontSize = 15.sp)
            }
        }
    }
}
