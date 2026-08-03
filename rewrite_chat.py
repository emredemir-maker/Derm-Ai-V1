# -*- coding: utf-8 -*-
import codecs

content = """package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: SkinCareViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isChatTyping.collectAsState()
    var draft by remember { mutableStateOf("") }
    
    val listState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll on new message
    LaunchedEffect(messages.size, isTyping) {
        coroutineScope.launch {
            listState.animateScrollTo(listState.maxValue)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(38.dp).background(SurfaceCard, CircleShape).border(1.dp, BorderDefault, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Navy700, modifier = Modifier.size(20.dp))
            }
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text("AI Asistan", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(modifier = Modifier.size(6.dp).background(Mint500, CircleShape))
                        Text("Profilini biliyor", fontSize = 12.sp, color = Mint500)
                    }
                }
            }
            
            Box(
                modifier = Modifier.size(38.dp).background(SurfaceCard, CircleShape).border(1.dp, BorderDefault, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Navy700, modifier = Modifier.size(19.dp))
            }
        }
        
        // Chat Area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(listState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            messages.forEach { msg ->
                val isMe = msg.isUser
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 282.dp)
                            .background(
                                if (isMe) Brush.horizontalGradient(listOf(Pink400, Blue400)) else Brush.linearGradient(listOf(SurfaceCard, SurfaceCard)),
                                RoundedCornerShape(
                                    topStart = 18.dp, topEnd = 18.dp,
                                    bottomStart = if (isMe) 18.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 18.dp
                                )
                            )
                            .border(
                                if (isMe) 0.dp else 1.dp,
                                if (isMe) Color.Transparent else BorderDefault,
                                RoundedCornerShape(
                                    topStart = 18.dp, topEnd = 18.dp,
                                    bottomStart = if (isMe) 18.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 18.dp
                                )
                            )
                            .padding(horizontal = 15.dp, vertical = 13.dp)
                    ) {
                        Text(
                            text = msg.content,
                            fontSize = 14.sp,
                            color = if (isMe) White else Navy900,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            if (isTyping) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .background(SurfaceCard, RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                            .border(1.dp, BorderDefault, RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                            .padding(horizontal = 15.dp, vertical = 13.dp)
                    ) {
                        Text("Yazıyor...", fontSize = 14.sp, color = TextMuted)
                    }
                }
            }
        }
        
        // Input Area
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAskChip("Siyah noktalar") { viewModel.sendChatMessage(it) }
                QuickAskChip("Retinol") { viewModel.sendChatMessage(it) }
                QuickAskChip("Makyaj altı baz") { viewModel.sendChatMessage(it) }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.5f), CircleShape)
                    .border(1.dp, BorderInput, CircleShape)
                    .padding(start = 18.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mesajını yaz...", color = Navy900, fontSize = 14.sp) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Navy900)
                )
                
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Brush.horizontalGradient(listOf(Pink400, Blue400)), CircleShape)
                        .clickable {
                            if (draft.isNotBlank()) {
                                viewModel.sendChatMessage(draft)
                                draft = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = White, modifier = Modifier.size(19.dp))
                }
            }
        }
    }
}

@Composable
fun QuickAskChip(text: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .background(SurfaceCard, CircleShape)
            .border(1.dp, Purple300, CircleShape)
            .clickable { onClick(text) }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Purple700)
    }
}
"""

with codecs.open("app/src/main/java/com/example/ui/screens/ChatScreen.kt", "w", "utf-8") as f:
    f.write(content)
