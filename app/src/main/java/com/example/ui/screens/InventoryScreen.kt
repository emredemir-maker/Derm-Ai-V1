package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.InventoryItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.SkinCareViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: SkinCareViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.inventoryItems.collectAsState()
    val weeklyCheck by viewModel.weeklyInventoryCheck.collectAsState()
    val weeklyError by viewModel.weeklyInventoryError.collectAsState()
    val isWeeklyLoading by viewModel.isWeeklyInventoryLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf("Tümü") }
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<InventoryItem?>(null) }
    var showWeeklyResultDialog by remember { mutableStateOf(false) }

    val filteredItems = remember(items, searchQuery, typeFilter) {
        InventoryHelper.filterAndSearchItems(items, searchQuery, typeFilter)
    }

    val canRunCheck = remember(items) {
        InventoryHelper.canRunWeeklyCheck(items)
    }

    LaunchedEffect(weeklyCheck) {
        if (weeklyCheck != null) {
            showWeeklyResultDialog = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SurfacePage,
        topBar = {
            TopAppBar(
                title = { Text("Ürün Dolabım ve Envanter", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Navy900)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfacePage)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Purple600,
                contentColor = White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ürün Ekle")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search and Filter Bar
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Ürün adı, marka, kategori veya içerik ara...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Temizle", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = Purple500,
                        unfocusedBorderColor = BorderDefault
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Tümü", "Cilt Bakımı", "Makyaj").forEach { filter ->
                        val isSelected = typeFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { typeFilter = filter },
                            label = { Text(filter, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = SurfaceCard,
                                selectedContainerColor = Purple100,
                                labelColor = TextSecondary,
                                selectedLabelColor = Purple700
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Purple300 else BorderDefault
                            )
                        )
                    }
                }
            }

            // Weekly Inventory Check Banner / Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderDefault)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Brush.horizontalGradient(listOf(Purple500, Pink400)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = White, modifier = Modifier.size(18.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Haftalık AI Envanter & Çakışma Analizi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            Text("Ürün içeriklerinizi cilt tipinize göre tarayın", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    if (weeklyError != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Rose100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Rose600, modifier = Modifier.size(18.dp))
                                Text(weeklyError!!, fontSize = 12.sp, color = Rose600, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.runWeeklyInventoryCheck() },
                        enabled = canRunCheck && !isWeeklyLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isWeeklyLoading) {
                            CircularProgressIndicator(color = White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analiz Ediliyor...", fontSize = 13.sp)
                        } else {
                            Text(if (!canRunCheck) "Analiz için içerik bilgisi olan ürün gerekli" else "Haftalık Envanter Analizini Başlat", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Inventory List
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = TextDisabled, modifier = Modifier.size(48.dp))
                        Text("Envanterinizde ürün bulunmuyor", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                        Text("Sağ alttaki + butonuna basarak ürün ekleyebilirsiniz.", fontSize = 12.sp, color = TextMuted)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        InventoryItemCard(
                            item = item,
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }
        }
    }

    // Add Product Dialog / Bottom Sheet
    if (showAddDialog) {
        AddInventoryItemDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, brand, type, category, openedDate, shelfLifeMonths, ingredients, notes ->
                viewModel.addInventoryItem(
                    name = name,
                    brand = brand,
                    type = type,
                    category = category,
                    shelfLifeMonths = shelfLifeMonths,
                    ingredients = ingredients,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Ürünü Sil") },
            text = { Text("\"${itemToDelete!!.name}\" envanterinizden silinecek. Emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteInventoryItem(it.id) }
                        itemToDelete = null
                    }
                ) {
                    Text("Sil", color = Rose600, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Vazgeç")
                }
            }
        )
    }

    // Weekly Result Dialog
    if (showWeeklyResultDialog && weeklyCheck != null) {
        WeeklyResultDialog(
            response = weeklyCheck!!,
            onDismiss = {
                showWeeklyResultDialog = false
                viewModel.clearWeeklyInventoryCheck()
            }
        )
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onDelete: () -> Unit
) {
    val expiryDate = remember(item.openedDate, item.shelfLifeMonths) {
        InventoryHelper.calculateExpiryDate(item.openedDate, item.shelfLifeMonths)
    }
    val status = remember(item.openedDate, item.shelfLifeMonths) {
        InventoryHelper.getInventoryStatus(item.openedDate, item.shelfLifeMonths)
    }

    val (statusText, statusColor, statusBg) = when (status) {
        InventoryStatus.ACTIVE -> Triple("Aktif", Green600, Mint100)
        InventoryStatus.EXPIRING_SOON -> Triple("30 Gün İçinde Doluyor", Amber600, Amber100)
        InventoryStatus.EXPIRED -> Triple("Süresi Doldu", Rose600, Rose100)
    }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (item.brand.isNotBlank()) {
                        Text(item.brand.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Purple600)
                    }
                    Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    Text("${item.type} • ${item.category}", fontSize = 12.sp, color = TextSecondary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusBg
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Sil", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }

            HorizontalDivider(color = BorderDefault, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Açılış Tarihi", fontSize = 10.sp, color = TextMuted)
                    Text(dateFormat.format(Date(item.openedDate)), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Navy900)
                }
                Column {
                    Text("Son Kullanma", fontSize = 10.sp, color = TextMuted)
                    Text(dateFormat.format(expiryDate), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Navy900)
                }
                Column {
                    Text("Kullanım Süresi", fontSize = 10.sp, color = TextMuted)
                    Text("${item.shelfLifeMonths} Ay", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Navy900)
                }
                Column {
                    Text("Uyum Durumu", fontSize = 10.sp, color = TextMuted)
                    Text(InventoryHelper.getCompatibilityText(item.compatibilityScore), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Purple700)
                }
            }

            if (item.ingredients.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceTint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("İçerik Listesi (INCI):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                        Text(item.ingredients, fontSize = 11.sp, color = TextSecondary, maxLines = 2)
                    }
                }
            } else {
                Text("İçerik bilgisi girilmedi", fontSize = 11.sp, color = TextMuted, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }

            if (!item.notes.isNullOrBlank()) {
                Text("Not: ${item.notes}", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Long, Int, String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Cilt Bakımı") }
    var category by remember { mutableStateOf("Nemlendirici") }
    var shelfLifeMonths by remember { mutableIntStateOf(12) }
    var ingredients by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val categories = if (type == "Cilt Bakımı") {
        listOf("Nemlendirici", "Serum", "Temizleyici", "Güneş Kremi", "Tonik", "Maske", "Göz Kremi", "Peeling")
    } else {
        listOf("Fondöten", "Pudra", "Kapatıcı", "Allık", "Ruj", "Maskara", "Far")
    }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Envantere Yeni Ürün Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ürün Adı *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marka (İsteğe bağlı)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Type selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Ürün Türü", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Cilt Bakımı", "Makyaj").forEach { t ->
                            val isSelected = type == t
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    type = t
                                    category = if (t == "Cilt Bakımı") "Nemlendirici" else "Fondöten"
                                },
                                label = { Text(t) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Category dropdown / selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Kategori", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(category)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Shelf life slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Açıldıktan Sonra Kullanım Süresi: $shelfLifeMonths Ay", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Slider(
                        value = shelfLifeMonths.toFloat(),
                        onValueChange = { shelfLifeMonths = it.toInt() },
                        valueRange = 1f..60f,
                        steps = 59
                    )
                }

                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text("İçerik Listesi (INCI) - İsteğe bağlı") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    placeholder = { Text("Water, Glycerin, Niacinamide...") }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notlar (İsteğe bağlı)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && !isSaving) {
                        isSaving = true
                        onSave(name.trim(), brand.trim(), type, category, System.currentTimeMillis(), shelfLifeMonths, ingredients.trim(), notes.takeIf { it.isNotBlank() })
                    }
                },
                enabled = name.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Purple600)
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!isSaving) onDismiss() }) {
                Text("İptal")
            }
        }
    )
}

@Composable
fun WeeklyResultDialog(
    response: com.example.data.api.WeeklyInventoryCheckResponse,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Purple600)
                Text("Haftalık Envanter Analizi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (response.generalAnalysis.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(12.dp), color = SurfaceTint) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Genel Değerlendirme", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            Text(response.generalAnalysis, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                        }
                    }
                }

                if (response.conflicts.isNotEmpty() || response.ingredientConflicts.isNotEmpty()) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Rose100) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("⚠️ Çakışma ve Uyarılar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Rose600)
                            (response.conflicts + response.ingredientConflicts).forEach { conflict ->
                                val desc = if (conflict.conflictReason.isNotBlank()) conflict.conflictReason else conflict.reason
                                val prod = if (conflict.productA.isNotBlank()) "${conflict.productA} & ${conflict.productB}" else conflict.products.joinToString(", ")
                                Text("• $prod: $desc", fontSize = 11.sp, color = Navy900)
                            }
                        }
                    }
                }

                if (response.routineSuggestions.isNotEmpty() || response.routine.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Mint100) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("✨ Rutin Önerileri", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Green600)
                            if (response.routineSuggestions.isNotEmpty()) {
                                response.routineSuggestions.forEach { rs ->
                                    Text("• [${rs.timeOfDay}] ${rs.steps.joinToString(", ")}", fontSize = 11.sp, color = Navy900)
                                }
                            } else {
                                Text("• ${response.routine}", fontSize = 11.sp, color = Navy900)
                            }
                        }
                    }
                }

                if (response.missingItems.isNotEmpty()) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Blue100) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("💡 Eksik Ürün Önerileri", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Blue600)
                            response.missingItems.forEach { item ->
                                Text("• $item", fontSize = 11.sp, color = Navy900)
                            }
                        }
                    }
                }

                if (response.nextWeekFocus.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(12.dp), color = Purple100) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("🎯 Gelecek Hafta Odağı", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Purple700)
                            Text(response.nextWeekFocus, fontSize = 11.sp, color = Navy900)
                        }
                    }
                }

                Text(
                    "Bu analiz yapay zeka tarafından üretilmiştir ve tıbbi teşhis niteliği taşımaz.",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Purple600)) {
                Text("Kapat")
            }
        }
    )
}
