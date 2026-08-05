@file:android.annotation.SuppressLint("NewApi")

package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.annotation.SuppressLint
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
import java.time.YearMonth
import java.time.ZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.io.File
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    viewModel: SkinCareViewModel,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.diaryEntries.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now(ZoneId.systemDefault())) }
    var showCamera by remember { mutableStateOf(false) }
    var capturedPhoto by remember { mutableStateOf<File?>(null) }

    val zoneId = ZoneId.systemDefault()
    val stats = calculateDiaryState(entries, currentMonth, zoneId)

    val tint = listOf(Color.Transparent, Rose100, Amber100, Blue100, Mint100, Mint300)
    val ink = listOf(TextDisabled, Rose600, Amber600, Blue600, Green600, Green600)

    if (showCamera) {
        CameraCaptureView(
            onPhotoCaptured = { file ->
                capturedPhoto = file
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
        return
    }

    if (capturedPhoto != null) {
        var rating by remember { mutableIntStateOf(3) }
        var note by remember { mutableStateOf("") }
        var isSaving by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { if (!isSaving) capturedPhoto = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfacePage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Yeni Günlük Kaydı", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy900)

                    AsyncImage(
                        model = capturedPhoto,
                        contentDescription = "Çekilen Fotoğraf",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f/4f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Cildin bugün nasıl hissediyor? (1-5)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            (1..5).forEach { r ->
                                val isSelected = rating == r
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(if (isSelected) tint[r] else SurfaceCard, CircleShape)
                                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) ink[r] else BorderDefault, CircleShape)
                                        .clickable { if (!isSaving) rating = r },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(r.toString(), fontWeight = FontWeight.Bold, color = if (isSelected) ink[r] else TextSecondary)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Günün Notu (İsteğe Bağlı)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        enabled = !isSaving
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { capturedPhoto = null },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving
                        ) {
                            Text("İptal")
                        }
                        Button(
                            onClick = {
                                isSaving = true
                                viewModel.saveDiaryEntry(note, rating, capturedPhoto?.absolutePath) {
                                    isSaving = false
                                    capturedPhoto = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Text("Kaydet")
                            }
                        }
                    }
                }
            }
        }
    }

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
                        val avgStr = if (stats.entryCount > 0) String.format(Locale.US, "%.1f", stats.averageRating) else "-"
                        Text("${stats.selectedMonthLabel} · ${stats.entryCount} kayıt · ortalama $avgStr / 5", fontSize = 12.sp, color = TextSecondary)
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
                    Box(modifier = Modifier.size(30.dp).background(Lilac100, CircleShape).clickable { currentMonth = currentMonth.minusMonths(1) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Navy700, modifier = Modifier.size(17.dp))
                    }
                    Text(stats.selectedMonthLabel, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)

                    val isNextDisabled = currentMonth.isAfter(YearMonth.now(zoneId)) || currentMonth == YearMonth.now(zoneId)
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (isNextDisabled) SurfacePage else Lilac100, CircleShape)
                            .clickable(enabled = !isNextDisabled) { currentMonth = currentMonth.plusMonths(1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = if (isNextDisabled) TextDisabled else Navy700, modifier = Modifier.size(17.dp))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("P", "S", "Ç", "P", "C", "C", "P").forEach {
                        Text(it, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    }
                }

                val rows = stats.calendarDays.chunked(7)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    rows.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            week.forEach { (d, s) ->
                                val isSelected = s > 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(if (d == 0) Color.Transparent else tint[s], RoundedCornerShape(10.dp))
                                        .border(if (isSelected) 2.dp else 0.dp, if (isSelected) Purple500 else Color.Transparent, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (d != 0) {
                                        Text(d.toString(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) ink[s] else TextSecondary)
                                    }
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

            // Entries List
            if (entries.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Lilac200, modifier = Modifier.size(64.dp))
                    Text("Henüz bir kayıt yok.", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Navy700)
                    Text("Kamera butonuna tıklayarak ilk günlük kaydınızı oluşturun.", fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }
            } else {
                val dtf = DateTimeFormatter.ofPattern("d MMMM, EEEE", Locale.forLanguageTag("tr-TR"))
                entries.sortedByDescending { it.date }.forEach { entry ->
                    val entryDate = Instant.ofEpochMilli(entry.date).atZone(zoneId).toLocalDate()
                    Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Text(entryDate.format(dtf), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.background(tint[entry.rating], CircleShape).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                    Text("Puan · ${entry.rating}/5", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ink[entry.rating])
                                }
                                var showDeleteConfirm by remember { mutableStateOf(false) }
                                Icon(Icons.Default.Delete, contentDescription = "Sil", tint = Rose600, modifier = Modifier.size(20.dp).clickable { showDeleteConfirm = true })
                                if (showDeleteConfirm) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteConfirm = false },
                                        title = { Text("Kaydı Sil") },
                                        text = { Text("Bu günlük kaydını silmek istediğinize emin misiniz?") },
                                        confirmButton = {
                                            TextButton(onClick = { viewModel.deleteDiaryEntry(entry.id); showDeleteConfirm = false }) { Text("Sil", color = Rose600) }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDeleteConfirm = false }) { Text("İptal") }
                                        }
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceCard, RoundedCornerShape(20.dp))
                                .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
                        ) {
                            if (entry.photoPath != null) {
                                val file = File(entry.photoPath)
                                if (file.exists()) {
                                    AsyncImage(
                                        model = file,
                                        contentDescription = "Günlük Fotoğrafı",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.Black),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }
                            }

                            if (entry.note.isNotBlank()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("“${entry.note}”", fontSize = 14.sp, color = Navy700, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                }
                            }

                            if (!entry.aiFeedback.isNullOrBlank()) {
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
                                    Text(entry.aiFeedback, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(56.dp)
                .background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape)
                .clickable { showCamera = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = White, modifier = Modifier.size(24.dp))
        }
    }
}
