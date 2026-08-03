package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.DiaryEntry
import com.example.ui.viewmodel.SkinCareViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    viewModel: SkinCareViewModel,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.diaryEntries.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showCameraForEntry by remember { mutableStateOf(false) }
    var temporaryCapturedPhotoPath by remember { mutableStateOf<String?>(null) }

    if (showCameraForEntry) {
        CameraCaptureView(
            onPhotoCaptured = { file ->
                temporaryCapturedPhotoPath = file.absolutePath
                showCameraForEntry = false
                showAddDialog = true
            },
            onDismiss = {
                showCameraForEntry = false
                showAddDialog = true
            }
        )
    } else {
        Scaffold(
            modifier = modifier,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_diary_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Yeni Günlük Girişi Ekle")
                }
            }
        ) { innerPadding ->
            var selectedTab by remember { mutableStateOf(0) } // 0 = Takvim, 1 = Liste
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                // Header & Tab Navigation Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Cilt Değişim Günlüğü",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        "Bakım rutininizin ve kozmetiklerinizin cildinizdeki etkisini ve günlük analizlerinizi takip edin.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Takvim", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Liste", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Galeri", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (selectedTab == 0) {
                        DiaryCalendarView(
                            entries = entries,
                            onSelectEntry = { /* selected implicitly via date selection */ },
                            onDeleteEntry = { id -> viewModel.deleteDiaryEntry(id) },
                            onAddEntryClick = { showAddDialog = true }
                        )
                    } else {
                        if (entries.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Cilt Değişim Günlüğünüz Boş",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Cildinizdeki iyileşmeleri takip etmek için sağ alttaki '+' butonuna tıklayarak ilk günlüğünüzü oluşturun. Fotoğraf, cilt puanı ve notlarınızı ekleyin!",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        } else {
                            if (selectedTab == 1) {
                                LazyColumn(
                                    contentPadding = PaddingValues(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(entries, key = { it.id }) { entry ->
                                        DiaryEntryCard(
                                            entry = entry,
                                            onDelete = { viewModel.deleteDiaryEntry(entry.id) }
                                        )
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(entries, key = { it.id }) { entry ->
                                        DiaryGalleryCard(
                                            entry = entry,
                                            modifier = Modifier.clickable { /* Could expand or show detail later */ }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showAddDialog) {
                        AddDiaryEntryDialog(
                            capturedPhotoPath = temporaryCapturedPhotoPath,
                            onCaptureClick = {
                                showAddDialog = false
                                showCameraForEntry = true
                            },
                            onClearPhoto = {
                                temporaryCapturedPhotoPath = null
                            },
                            onDismiss = {
                                showAddDialog = false
                                temporaryCapturedPhotoPath = null
                            },
                            onSave = { note, rating, photoCode ->
                                viewModel.saveDiaryEntry(note, rating, photoCode)
                                showAddDialog = false
                                temporaryCapturedPhotoPath = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryCalendarView(
    entries: List<DiaryEntry>,
    onSelectEntry: (DiaryEntry) -> Unit,
    onDeleteEntry: (Int) -> Unit,
    onAddEntryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) } // 0-indexed
    val selectedCalendar = remember { mutableStateOf(Calendar.getInstance()) }
    
    val gridDays = remember(currentYear, currentMonth) {
        getDaysInMonthGrid(currentYear, currentMonth)
    }
    
    val monthNames = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )
    val weekdays = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
    
    val currentYearInt = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonthInt = Calendar.getInstance().get(Calendar.MONTH)
    val currentDayInt = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    
    // Filter entries for the selected day
    val entriesForSelectedDay = remember(entries, selectedCalendar.value) {
        entries.filter { entry ->
            val entryCal = Calendar.getInstance().apply { timeInMillis = entry.date }
            entryCal.get(Calendar.YEAR) == selectedCalendar.value.get(Calendar.YEAR) &&
            entryCal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.value.get(Calendar.DAY_OF_YEAR)
        }
    }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar Control Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Month & Year Selector Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (currentMonth == 0) {
                                    currentMonth = 11
                                    currentYear -= 1
                                } else {
                                    currentMonth -= 1
                                }
                            }
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Önceki Ay")
                        }
                        
                        Text(
                            text = "${monthNames[currentMonth]} $currentYear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        IconButton(
                            onClick = {
                                if (currentMonth == 11) {
                                    currentMonth = 0
                                    currentYear += 1
                                } else {
                                    currentMonth += 1
                                }
                            }
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Sonraki Ay")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Weekday Names Row
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekdays.forEach { weekday ->
                            Text(
                                text = weekday,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Days Grid
                    val chunkedWeeks = gridDays.chunked(7)
                    chunkedWeeks.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            week.forEach { dayCal ->
                                if (dayCal == null) {
                                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                } else {
                                    val dayNum = dayCal.get(Calendar.DAY_OF_MONTH)
                                    val isSelected = 
                                        selectedCalendar.value.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR) &&
                                        selectedCalendar.value.get(Calendar.MONTH) == dayCal.get(Calendar.MONTH) &&
                                        selectedCalendar.value.get(Calendar.DAY_OF_MONTH) == dayNum
                                    
                                    val isToday = 
                                        currentYearInt == dayCal.get(Calendar.YEAR) &&
                                        currentMonthInt == dayCal.get(Calendar.MONTH) &&
                                        currentDayInt == dayNum
                                    
                                    // Check if this specific day has a logged skin photo
                                    val dayEntriesList = entries.filter { entry ->
                                        val entryCal = Calendar.getInstance().apply { timeInMillis = entry.date }
                                        entryCal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR) &&
                                        entryCal.get(Calendar.MONTH) == dayCal.get(Calendar.MONTH) &&
                                        entryCal.get(Calendar.DAY_OF_MONTH) == dayNum
                                    }
                                    
                                    val hasPhoto = dayEntriesList.any { it.photoPath != null }
                                    val hasAnyEntry = dayEntriesList.isNotEmpty()
                                    
                                    val topRating = if (hasAnyEntry) dayEntriesList.maxOf { it.rating } else 0
                                    val ratingEmojis = listOf("😞", "😐", "🙂", "😊", "😍")
                                    val ratingEmoji = if (topRating in 1..5) ratingEmojis[topRating - 1] else null
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                                                    isToday -> MaterialTheme.colorScheme.surfaceVariant
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .border(
                                                width = if (isToday && !isSelected) 1.dp else 0.dp,
                                                color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                selectedCalendar.value = dayCal
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = dayNum.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                                    isToday -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                            
                                            if (hasAnyEntry) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (hasPhoto) {
                                                        Icon(
                                                            imageVector = Icons.Default.CameraAlt,
                                                            contentDescription = "Fotoğraf var",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(9.dp)
                                                        )
                                                    } else {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(MaterialTheme.colorScheme.primary)
                                                        )
                                                    }
                                                    
                                                    if (ratingEmoji != null) {
                                                        Text(ratingEmoji, fontSize = 7.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Selected Date Details Title
        item {
            val dateSdf = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
            val selectedDateStr = dateSdf.format(selectedCalendar.value.time)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedDateStr Tarihindeki Kayıtlar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Giriş: ${entriesForSelectedDay.size}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Selected Date Entries List
        if (entriesForSelectedDay.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Kayıt Bulunmamaktadır",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bu güne ait bir cilt fotoğrafı veya analizi bulunamadı.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onAddEntryClick,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bugün İçin Ekle", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(entriesForSelectedDay, key = { it.id }) { entry ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    DiaryEntryCard(
                        entry = entry,
                        onDelete = { onDeleteEntry(entry.id) }
                    )
                }
            }
        }
    }
}

private fun getDaysInMonthGrid(year: Int, month: Int): List<Calendar?> {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val blanks = (firstDayOfWeek - Calendar.MONDAY + 7) % 7
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val grid = mutableListOf<Calendar?>()
    for (i in 0 until blanks) {
        grid.add(null)
    }
    for (day in 1..daysInMonth) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        grid.add(cal)
    }
    return grid
}

@Composable
fun DiaryEntryCard(
    entry: DiaryEntry,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr"))
    val dateStr = sdf.format(Date(entry.date))

    val ratingEmojis = listOf("😞", "😐", "🙂", "😊", "😍")
    val ratingTexts = listOf("Çok Hassas", "Sorunlu/Kuru", "Normal/Dengeli", "Canlı & İyi", "Mükemmel")

    val ratingEmoji = ratingEmojis.getOrNull(entry.rating - 1) ?: "🙂"
    val ratingText = ratingTexts.getOrNull(entry.rating - 1) ?: "Normal"

    // Skin Condition Photo metadata mapping
    val skinIllustrationColor = when (entry.photoPath) {
        "kizari" -> Color(0xFFFFCDD2) // Redness
        "akne" -> Color(0xFFFFE0B2) // Acne
        "kuruluk" -> Color(0xFFE1BEE7) // Dry
        "saglik" -> Color(0xFFC8E6C9) // Healthy Glow
        "isilti" -> Color(0xFFFFF9C4) // Radiant Sparkle
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val skinConditionName = when (entry.photoPath) {
        "kizari" -> "Kızarık Cilt Durumu"
        "akne" -> "Akne & Sivilceli Durum"
        "kuruluk" -> "Mat & Kuru Cilt"
        "saglik" -> "Pürüzsüz & Sağlıklı"
        "isilti" -> "Işıltılı & Canlı Cilt"
        else -> "Cilt Durumu"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(skinIllustrationColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ratingEmoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = dateStr,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = ratingText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Girişi Sil",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Photo Diary Representation
            val isRealPhoto = entry.photoPath != null && entry.photoPath !in listOf("kizari", "akne", "kuruluk", "saglik", "isilti")
            if (isRealPhoto) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = java.io.File(entry.photoPath!!),
                        contentDescription = "Cilt Günlüğü Fotoğrafı",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Kamera Fotoğrafı",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = skinIllustrationColor.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Fotoğraf Analizi: $skinConditionName",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Note section
            Text(
                text = entry.note,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            // AI Feedback section
            entry.aiFeedback?.let { feedback ->
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Yapay Zeka Yorumu",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = feedback,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDiaryEntryDialog(
    capturedPhotoPath: String?,
    onCaptureClick: () -> Unit,
    onClearPhoto: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (note: String, rating: Int, photoCode: String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var selectedPhoto by remember { mutableStateOf("saglik") }

    val emojis = listOf("😞", "😐", "🙂", "😊", "😍")
    val textLabels = listOf("Hassas", "Kuru/Mat", "Dengeli", "Canlı", "Işıltılı")

    val photoOptions = listOf(
        "kizari" to "Kızarık/Hassas",
        "akne" to "Sivilceli/Akne",
        "kuruluk" to "Kuru/Pullanmış",
        "saglik" to "Pürüzsüz/Dengeli",
        "isilti" to "Canlı/Işıltılı"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Yeni Günlük Girişi",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 1. Skin Rating Picker
                Column {
                    Text(
                        "Cildiniz Bugün Nasıl Hissediyor?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 1..5) {
                            val isSelected = rating == i
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { rating = i }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emojis[i - 1],
                                        fontSize = 22.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = textLabels[i - 1],
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. Select Skin condition photo template / Real Photo
                if (capturedPhotoPath != null) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Çekilen Cilt Fotoğrafı",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(
                                onClick = onCaptureClick,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Yeniden Çek", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = java.io.File(capturedPhotoPath),
                                contentDescription = "Çekilen Cilt Fotoğrafı",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            IconButton(
                                onClick = onClearPhoto,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                                ),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fotoğrafı Kaldır",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Cildinizin Görsel Durumu",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(
                                onClick = onCaptureClick,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Fotoğraf Çek", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            photoOptions.forEach { (code, label) ->
                                val isSel = selectedPhoto == code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { selectedPhoto = code }
                                        .border(
                                            1.dp,
                                            if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 10.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.Face,
                                            contentDescription = null,
                                            tint = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = label.split("/").first(),
                                            fontSize = 9.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. User Note Text Field
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Bugünkü Cilt Notunuz") },
                    placeholder = { Text("Örn: Yeni kremi denedim, hafif parlama yaptı ama yumuşacık.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("diary_note_input"),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (note.isNotBlank()) {
                        val finalPhoto = capturedPhotoPath ?: selectedPhoto
                        onSave(note, rating, finalPhoto)
                    }
                },
                enabled = note.isNotBlank(),
                modifier = Modifier.testTag("save_diary_entry_button")
            ) {
                Text("Günlüğü Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}

@Composable
fun DiaryGalleryCard(entry: DiaryEntry, modifier: Modifier = Modifier) {
    val dateStr = remember(entry.date) {
        SimpleDateFormat("d MMM yyyy", Locale("tr")).format(Date(entry.date))
    }
    
    val isRealPhoto = entry.photoPath != null && entry.photoPath !in listOf("kizari", "akne", "kuruluk", "saglik", "isilti")
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column {
            if (isRealPhoto) {
                AsyncImage(
                    model = java.io.File(entry.photoPath!!),
                    contentDescription = "Cilt Günlüğü Fotoğrafı",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.note,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                if (entry.aiFeedback != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "AI Analizi",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
