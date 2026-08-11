package com.example.ui.screens

import java.io.File
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.viewmodel.SkinCareViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeupAnalysisScreen(
    viewModel: SkinCareViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCamera by remember { mutableStateOf(false) }
    val isAnalyzing by viewModel.isScanLoading.collectAsState()
    val makeupResult by viewModel.makeupAnalysisResult.collectAsState()
    val makeupPhotoPath by viewModel.makeupPhotoPath.collectAsState()
    
    val scrollState = rememberScrollState()
    
    if (showCamera) {
        CameraCaptureView(
            onPhotoCaptured = { file ->
                showCamera = false
                viewModel.analyzeMakeup(file.absolutePath)
            },
            onDismiss = {
                showCamera = false
            },
            requireFaceQuality = true
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Yapay Zeka Makyaj Analizi", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            modifier = modifier
        ) { innerPadding ->
            if (isAnalyzing) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Makyajınız inceleniyor...", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (makeupResult == null) {
                        // Intro state
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Makyajınızı Yüzünüze Yakıştırıyor Musunuz?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Makyajlı bir fotoğrafınızı çekin, yapay zeka makyajınızı analiz etsin ve daha iyi bir görünüm için neler yapıp yapmamanız gerektiğini söylesin.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { showCamera = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Makyaj Analizine Başla", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Show result
                        makeupPhotoPath?.let { photoPath ->
                            Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(16.dp))) {
                                AsyncImage(
                                    model = photoPath,
                                    contentDescription = "Makyajlı Yüzünüz",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        makeupResult?.let { result ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Genel Değerlendirme", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(result.overallEvaluation, fontSize = 14.sp, lineHeight = 20.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Light green
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Neler Yapmalısınız?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    result.whatToDo.forEach { item ->
                                        Text("• $item", fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF1B5E20))
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Light red
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Neler Yapmamalısınız?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    result.whatNotToDo.forEach { item ->
                                        Text("• $item", fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFFB71C1C))
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            OutlinedButton(
                                onClick = { showCamera = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text("Yeni Fotoğraf Çek", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(130.dp))
                        }
                    }
                }
            }
        }
    }
}
