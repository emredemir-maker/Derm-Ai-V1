package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.viewmodel.mapToStandardConcerns
import com.example.ui.viewmodel.mapToStandardGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: SkinCareViewModel,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSkinType by remember { mutableStateOf("Karma") }
    var selectedConcerns by remember { mutableStateOf(setOf<String>()) }
    var selectedGoal by remember { mutableStateOf("Nemlendirme") }
    var showCameraForScan by remember { mutableStateOf(false) }

    val skinTypes = listOf(
        "Kuru" to "Nemsiz, gergin ve pullanmaya eğilimli",
        "Yağlı" to "Aşırı sebum, parlama ve gözenekli",
        "Karma" to "T bölgesi yağlı, yanaklar kuru/normal",
        "Hassas" to "Kızarmaya ve tahrişe yatkın",
        "Normal" to "Dengeli, gözenekleri belirsiz"
    )

    val concerns = listOf(
        "Akne & Sivilce", "Siyah Noktalar", "Geniş Gözenekler",
        "Lekeler & Pigmentasyon", "Kırışıklık & İnce Çizgiler", "Kızarıklık", "Kuruluk & Pullanma"
    )

    val goals = listOf(
        "Nemlendirme", "Aydınlatma & Parlaklık", "Yaşlanma Karşıtı (Anti-Aging)",
        "Sivilce Kontrolü", "Cilt Bariyeri Güçlendirme"
    )

    val scanAnalysis by viewModel.scanProfileAnalysis.collectAsState()
    val isScanLoading by viewModel.isScanLoading.collectAsState()
    var showScanResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(scanAnalysis) {
        val analysis = scanAnalysis
        if (analysis != null) {
            showScanResultDialog = true
            selectedSkinType = when {
                analysis.skinType.contains("Kuru", ignoreCase = true) -> "Kuru"
                analysis.skinType.contains("Yağlı", ignoreCase = true) -> "Yağlı"
                analysis.skinType.contains("Hassas", ignoreCase = true) -> "Hassas"
                analysis.skinType.contains("Normal", ignoreCase = true) -> "Normal"
                else -> "Karma"
            }
            val mappedConcerns = mapToStandardConcerns(analysis.concerns)
            if (mappedConcerns.isNotEmpty()) {
                selectedConcerns = mappedConcerns.toSet()
            }
            selectedGoal = mapToStandardGoal(analysis.goal)
        }
    }

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
        return
    }

    if (showScanResultDialog && scanAnalysis != null) {
        val lastPhotoPath by viewModel.lastScannedPhotoPath.collectAsState()
        AlertDialog(
            onDismissRequest = { showScanResultDialog = false },
            confirmButton = {},
            dismissButton = {},
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            text = {
                FaceMapDiagnosticCard(
                    analysisResult = scanAnalysis,
                    photoPath = lastPhotoPath,
                    userConcerns = selectedConcerns.toList(),
                    onApplyToProfile = { skinType, concernsList, goal ->
                        val mappedType = when {
                            skinType.contains("Kuru", ignoreCase = true) -> "Kuru"
                            skinType.contains("Yağlı", ignoreCase = true) -> "Yağlı"
                            skinType.contains("Hassas", ignoreCase = true) -> "Hassas"
                            skinType.contains("Normal", ignoreCase = true) -> "Normal"
                            else -> "Karma"
                        }
                        val mappedC = mapToStandardConcerns(concernsList)
                        val mappedG = mapToStandardGoal(goal)

                        selectedSkinType = mappedType
                        if (mappedC.isNotEmpty()) {
                            selectedConcerns = mappedC.toSet()
                        }
                        selectedGoal = mappedG

                        showScanResultDialog = false
                        currentStep = 1 // Take user to Step 1 (Olası Problemler) to review and edit!
                    },
                    onRetakePhoto = {
                        showScanResultDialog = false
                        showCameraForScan = true
                    }
                )
            },
            containerColor = SurfacePage,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfacePage,
        bottomBar = {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Button(
                    onClick = {
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            viewModel.saveSkinProfile(
                                skinType = selectedSkinType,
                                skinConcerns = selectedConcerns.toList(),
                                skincareGoal = selectedGoal,
                                makeupPreference = "Doğal & Hafif (Yok Gibi Makyaj)"
                            )
                            onCompleted()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = if (currentStep == 2) "Rutinimi Oluştur" else "Devam Et",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        IconButton(
                            onClick = { currentStep-- },
                            modifier = Modifier
                                .size(38.dp)
                                .background(SurfaceCard, RoundedCornerShape(19.dp))
                                .border(1.dp, BorderDefault, RoundedCornerShape(19.dp))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Navy700)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(38.dp))
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cilt profilin", fontSize = 12.sp, color = TextSecondary)
                            Text("Adım ${currentStep + 1}/3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple600)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (currentStep + 1) / 3f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Purple600,
                            trackColor = Lilac200,
                        )
                    }
                    
                    TextButton(onClick = { currentStep = 2 }) {
                        Text("Geç", color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Content
                AnimatedContent(targetState = currentStep, label = "step") { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp)
                    ) {
                        when (step) {
                            0 -> {
                                Text(
                                    "Cildini nasıl\ntanımlarsın?",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Gün ortasında cildine dokunduğunda ne hissediyorsun? En yakın olanı seç — sonra değiştirebilirsin.",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                skinTypes.forEach { (type, desc) ->
                                    val isSelected = selectedSkinType == type
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(SurfaceCard)
                                            .border(
                                                if (isSelected) 2.dp else 1.5.dp,
                                                if (isSelected) Purple500 else BorderDefault,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedSkinType = type }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(if (isSelected) Purple100 else Lilac100, RoundedCornerShape(16.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Purple600)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(type, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Navy900)
                                            Text(desc, fontSize = 12.sp, color = TextSecondary)
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .border(1.5.dp, Purple400, RoundedCornerShape(16.dp))
                                        .clickable {
                                            showCameraForScan = true
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Purple600, RoundedCornerShape(20.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Emin değil misin? AI ile Yüz Haritanı Çıkar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Purple800)
                                        Text("Yüz fotoğrafı çekerek otomatik analiz et", fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Button(
                                        onClick = { showCameraForScan = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Fotoğraf Çek", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = White)
                                    }
                                }
                                Spacer(modifier = Modifier.height(130.dp))
                            }
                            1 -> {
                                Surface(
                                    color = Mint100,
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Text(
                                        "$selectedSkinType cilt",
                                        color = Green600,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Neyi düzeltmek\nistiyorsun?",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Birden fazla seçebilirsin. Rutinini bu şikayetlere göre kuracağız.",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                if (scanAnalysis != null) {
                                    Surface(
                                        color = Purple100,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .border(1.dp, Purple300, RoundedCornerShape(16.dp))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(Purple600, RoundedCornerShape(18.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("AI Yüz Analizi Sonucunuz İşlendi ✨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Purple800)
                                                Text("Fotoğrafınızdan tespit edilen problemler aşağıda otomatik işaretlendi. Dokunarak ekleme veya çıkarma yapabilirsiniz.", fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                                            }
                                        }
                                    }
                                }

                                @OptIn(ExperimentalLayoutApi::class)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    concerns.forEach { concern ->
                                        val isSelected = selectedConcerns.contains(concern)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(if (isSelected) Purple100 else SurfaceCard)
                                                .border(
                                                    if (isSelected) 2.dp else 1.5.dp,
                                                    if (isSelected) Purple600 else BorderDefault,
                                                    RoundedCornerShape(24.dp)
                                                )
                                                .clickable {
                                                    selectedConcerns = if (isSelected) {
                                                        selectedConcerns - concern
                                                    } else {
                                                        selectedConcerns + concern
                                                    }
                                                }
                                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                if (isSelected) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Purple700, modifier = Modifier.size(16.dp))
                                                }
                                                Text(
                                                    concern,
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                    color = if (isSelected) Purple800 else Navy900
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Surface(
                                    color = SurfaceBrandSoft,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "İhtiyacınıza göre seçenekleri işaretleyip veya kaldırabilirsiniz.",
                                        color = Purple700,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                            2 -> {
                                Text(
                                    "Bu ay neye\nodaklanalım?",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tek bir hedef seç — rutinin, ürün önerilerin ve skor takibin bu hedefe göre kurulur.",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                
                                goals.forEach { goal ->
                                    val isSelected = selectedGoal == goal
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(SurfaceCard)
                                            .border(
                                                if (isSelected) 2.dp else 1.5.dp,
                                                if (isSelected) Purple500 else BorderDefault,
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable { selectedGoal = goal }
                                            .padding(18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            goal,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = Navy900,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Purple600)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }

            // Loading Overlay when scanning photo with AI
            if (isScanLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = Purple600, strokeWidth = 3.5.dp, modifier = Modifier.size(48.dp))
                            Text(
                                "AI Yüz & Cilt Analizi Yapılıyor...",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Fotoğrafınız taranıyor; cilt tipi, leke, gözenek ve yüz haritanız çıkarılıyor. Lütfen bekleyin ✨",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
