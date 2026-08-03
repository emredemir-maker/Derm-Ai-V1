package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.RecommendationsScreen
import com.example.ui.screens.IngredientScanScreen
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.CenterFocusWeak
import com.example.ui.theme.MyApplicationTheme
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
        
        // Initial setup/schedule for reminders
        NotificationHelper.updateReminders(this)

        // Request POST_NOTIFICATIONS permission dynamically for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        setContent {
            MyApplicationTheme {
                val skinProfile by viewModel.skinProfile.collectAsState()
                var currentTab by remember { mutableStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        // Only show bottom navigation if the user has completed their profile setup
                        if (skinProfile != null) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.testTag("main_navigation_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == 0,
                                    onClick = { currentTab = 0 },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Cilt Analizi",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Analiz", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("tab_home")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 1,
                                    onClick = { currentTab = 1 },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Spa,
                                            contentDescription = "Kozmetik Önerileri",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Öneriler", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("tab_recommendations")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 2,
                                    onClick = { currentTab = 2 },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "Fotoğraf Günlüğü",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Günlük", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("tab_diary")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 3,
                                    onClick = { currentTab = 3 },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.ChatBubble,
                                            contentDescription = "Yapay Zeka Danışmanı",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Danışman", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("tab_chat")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 4,
                                    onClick = { currentTab = 4 },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.CenterFocusWeak,
                                            contentDescription = "İçerik Analizi",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("İçerik", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag("tab_ingredient_scan")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)

                    if (skinProfile == null) {
                        // Guide users through onboarding first
                        ProfileSetupScreen(
                            viewModel = viewModel,
                            onCompleted = {
                                currentTab = 0
                            },
                            modifier = modifier
                        )
                    } else {
                        // Switch between different tabs
                        when (currentTab) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                modifier = modifier
                            )
                            1 -> RecommendationsScreen(
                                viewModel = viewModel,
                                modifier = modifier
                            )
                            2 -> DiaryScreen(
                                viewModel = viewModel,
                                modifier = modifier
                            )
                            3 -> ChatScreen(
                                viewModel = viewModel,
                                modifier = modifier
                            )
                            4 -> IngredientScanScreen(
                                viewModel = viewModel,
                                onNavigateToChat = { query ->
                                    viewModel.sendChatMessage(query)
                                    currentTab = 3
                                },
                                modifier = modifier
                            )
                        }
                    }
                }
            }
        }
    }
}
