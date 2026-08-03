# -*- coding: utf-8 -*-
import codecs

content = """package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.data.notification.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: SkinCareViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(this)
        NotificationHelper.updateReminders(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        setContent {
            MyApplicationTheme {
                val skinProfile by viewModel.skinProfile.collectAsState()
                var currentTab by remember { mutableIntStateOf(0) }
                var showMakeupAnalysis by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SurfacePage,
                    bottomBar = {
                        if (skinProfile != null && !showMakeupAnalysis) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp), spotColor = Purple600.copy(alpha=0.12f))
                                    .background(SurfaceCard, RoundedCornerShape(28.dp))
                                    .padding(horizontal = 10.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    NavBarItem(0, currentTab, "Analiz", Icons.Default.Home) { currentTab = 0 }
                                    NavBarItem(1, currentTab, "Öneriler", Icons.Default.ShoppingBag) { currentTab = 1 }
                                    
                                    // Center FAB
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.weight(1f).clickable { currentTab = 3 }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .offset(y = (-24).dp)
                                                .size(52.dp)
                                                .background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape)
                                                .shadow(18.dp, CircleShape, spotColor = Purple600.copy(alpha=0.5f))
                                                .border(6.dp, White.copy(alpha=0.9f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(24.dp))
                                        }
                                        Text("Danışman", fontSize = 11.sp, fontWeight = if (currentTab == 3) FontWeight.SemiBold else FontWeight.Medium, color = if (currentTab == 3) Purple600 else TextSecondary, modifier = Modifier.offset(y = (-24).dp))
                                    }
                                    
                                    NavBarItem(2, currentTab, "Günlük", Icons.Default.CalendarToday) { currentTab = 2 }
                                    NavBarItem(4, currentTab, "İçerik", Icons.Default.DocumentScanner) { currentTab = 4 }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    if (skinProfile == null) {
                        ProfileSetupScreen(viewModel = viewModel, onCompleted = { currentTab = 0 })
                    } else if (showMakeupAnalysis) {
                        MakeupAnalysisScreen(viewModel = viewModel, onNavigateBack = { showMakeupAnalysis = false })
                    } else {
                        when (currentTab) {
                            0 -> HomeScreen(viewModel = viewModel, onNavigateToMakeupAnalysis = { showMakeupAnalysis = true })
                            1 -> RecommendationsScreen(viewModel = viewModel)
                            2 -> DiaryScreen(viewModel = viewModel)
                            3 -> ChatScreen(viewModel = viewModel)
                            4 -> IngredientScanScreen(viewModel = viewModel, onNavigateToChat = { q -> viewModel.sendChatMessage(q); currentTab = 3 })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.NavBarItem(index: Int, currentTab: Int, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val isSelected = currentTab == index
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.weight(1f).clickable { onClick() }.padding(top = 4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = if (isSelected) Purple600 else Navy700, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Purple600 else TextMuted)
    }
}
"""

with codecs.open("app/src/main/java/com/example/MainActivity.kt", "w", "utf-8") as f:
    f.write(content)
