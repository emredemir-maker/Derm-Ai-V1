package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    viewModel: SkinCareViewModel,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var skinType by remember { mutableStateOf("Normal") }
    val selectedConcerns = remember { mutableStateListOf<String>() }
    var skincareGoal by remember { mutableStateOf("Nemlendirme") }
    var makeupPreference by remember { mutableStateOf("Doğal & Hafif") }
    var allergies by remember { mutableStateOf("") }

    var showCameraForScan by remember { mutableStateOf(false) }
    val scanResult by viewModel.scanProfileAnalysis.collectAsState()
    val isScanLoading by viewModel.isScanLoading.collectAsState()

    LaunchedEffect(scanResult) {
        scanResult?.let { result ->
            skinType = result.skinType
            selectedConcerns.clear()
            selectedConcerns.addAll(result.concerns)
            skincareGoal = result.goal
        }
    }

    val skinTypes = listOf(
        "Kuru" to "Nemsiz, gergin ve pullanmaya eğilimli",
        "Yağlı" to "Aşırı sebum, parlama ve gözenekli",
        "Karma" to "T bölgesi yağlı, yanaklar kuru/normal",
        "Hassas" to "Kızarmaya ve tahrişe yatkın",
        "Normal" to "Dengeli, gözenekleri belirsiz"
    )

    val concerns = listOf(
        "Akne & Sivilce", "Siyah Noktalar", "Geniş Gözenekler",
        "Lekeler & Pigmentasyon", "Kırışıklık & İnce Çizgiler",
        "Kızarıklık", "Kuruluk & Pullanma"
    )

    val goals = listOf(
        "Nemlendirme", "Aydınlatma & Parlaklık",
        "Yaşlanma Karşıtı (Anti-Aging)", "Sivilce Kontrolü",
        "Cilt Bariyeri Güçlendirme"
    )

    val makeupPreferences = listOf(
        "Doğal & Hafif (Yok Gibi Makyaj)",
        "Mat & Yoğun Kapatıcı (Uzun Ömürlü)",
        "Işıltılı & Islak Bitişli (Canlı Görünüm)"
    )

    if (showCameraForScan) {
        CameraCaptureView(
            onPhotoCaptured = { file ->
                showCameraForScan = false
                viewModel.analyzeScanForProfile(file.absolutePath)
            },
            onDismiss = {
                showCameraForScan = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "DermaAI",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Hero intro card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Cilt Analiz Testi",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Cildinizi analiz edip size özel günlük bakım rutinleri, krem önerileri ve makyaj tüyoları sunabilmemiz için aşağıdaki kısa soruları yanıtlayın.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // AI Scan Setup Banner/Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Akıllı Hızlı Kurulum (Önerilen)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Text(
                                "Soruları tek tek yanıtlamak yerine kameranızla selfie çekerek yapay zekanın cildinizi otomatik taramasını ve soruları yanıtlamasını sağlayabilirsiniz! Taramadan sonra sonuçları gözden geçirip dilediğiniz gibi değiştirebilirsiniz.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                            
                            Button(
                                onClick = { showCameraForScan = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kamerayı Aç ve Yapay Zeka ile Tara", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Success Banner
                    scanResult?.let { result ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Yapay Zeka Taraması Uygulandı!",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    val confidenceColor = when {
                                        result.confidenceScore >= 80 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        result.confidenceScore >= 50 -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                        else -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                    }
                                    Surface(
                                        color = confidenceColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "%${result.confidenceScore} Güvenilirlik",
                                            color = confidenceColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = result.explanation,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = "Belirlenen Cilt Tipi: ${result.skinType} | Hedef: ${result.goal}\nSaptanan Şikayetler: ${if (result.concerns.isEmpty()) "Yok/Saptanamadı" else result.concerns.joinToString(", ")}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Seçimleri aşağıdan değiştirebilirsiniz.",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = { viewModel.clearScanAnalysis() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Temizle", color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Question 1: Skin Type
                    QuestionSection(
                        title = "1. Cilt Tipiniz Nedir?",
                        icon = Icons.Default.Face,
                        description = "Cildinizin gün içindeki nem ve yağ salgılama durumuna en yakın seçeneği işaretleyin."
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            skinTypes.forEach { (type, desc) ->
                                val isSelected = skinType == type
                                Card(
                                    onClick = { skinType = type },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { skinType = type },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = MaterialTheme.colorScheme.onPrimary,
                                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = type,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = desc,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Question 2: Skin Concerns (Multiple Choice)
                    QuestionSection(
                        title = "2. Cilt Şikayetleriniz (Çoklu Seçim)",
                        icon = Icons.Default.Favorite,
                        description = "Cildinizde iyileştirmek veya kontrol altına almak istediğiniz sorunları belirtin."
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            concerns.forEach { concern ->
                                val isSelected = selectedConcerns.contains(concern)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            selectedConcerns.remove(concern)
                                        } else {
                                            selectedConcerns.add(concern)
                                        }
                                    },
                                    label = { Text(concern) },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                )
                            }
                        }
                    }

                    // Question 3: Skincare Goal
                    QuestionSection(
                        title = "3. Cilt Bakım Hedefiniz",
                        icon = Icons.Default.AutoAwesome,
                        description = "Ulaşmak istediğiniz en öncelikli cilt hedefini seçin."
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            goals.forEach { goal ->
                                val isSelected = skincareGoal == goal
                                Card(
                                    onClick = { skincareGoal = goal },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { skincareGoal = goal }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = goal,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Question 4: Makeup Preference
                    QuestionSection(
                        title = "4. Makyaj Bitiş Tercihiniz",
                        icon = Icons.Default.Brush,
                        description = "Size en uygun ten makyajı ve ürün bitişini belirtin."
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            makeupPreferences.forEach { pref ->
                                val isSelected = makeupPreference == pref
                                Card(
                                    onClick = { makeupPreference = pref },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { makeupPreference = pref }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = pref,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Question 5: Allergies
                    QuestionSection(
                        title = "5. Alerjileriniz (İsteğe Bağlı)",
                        icon = Icons.Default.Face,
                        description = "Bildiğiniz bir alerjiniz veya hassasiyetiniz varsa (örn: Parfüm, Paraben, Niasinamid) buraya yazın."
                    ) {
                        OutlinedTextField(
                            value = allergies,
                            onValueChange = { allergies = it },
                            placeholder = { Text("Alerjileriniz (isteğe bağlı)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                    }

                    // Action Button
                    Button(
                        onClick = {
                            viewModel.saveSkinProfile(
                                skinType = skinType,
                                skinConcerns = selectedConcerns.toList(),
                                skincareGoal = skincareGoal,
                                makeupPreference = makeupPreference,
                                allergies = allergies
                            )
                            viewModel.triggerFullAIAnalysis()
                            onCompleted()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Cilt Profilimi Kaydet ve Analiz Et",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Scan Loading Overlay
                if (isScanLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Yapay Zeka Cilt Taraması",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Selfie fotoğrafınız inceleniyor; cilt tipi, gözenekler, nem seviyesi ve olası şikayetler belirleniyor. Lütfen bekleyin...",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionSection(
    title: String,
    icon: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Box(modifier = Modifier.padding(top = 4.dp)) {
                content()
            }
        }
    }
}
