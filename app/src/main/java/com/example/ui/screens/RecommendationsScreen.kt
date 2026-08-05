@file:android.annotation.SuppressLint("NewApi")
package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ProductSuggestion
import com.example.ui.theme.*
import com.example.ui.viewmodel.SkinCareViewModel

@Composable
fun RecommendationsScreen(
    viewModel: SkinCareViewModel,
    modifier: Modifier = Modifier
) {
    val activeProfile by viewModel.skinProfile.collectAsState()
    val selectedSkinType by viewModel.selectedRecommendSkinType.collectAsState()
    val currentRec by viewModel.currentRecommendation.collectAsState()
    val isLoading by viewModel.isRecommendationLoading.collectAsState()
    val recError by viewModel.recommendationError.collectAsState()

    // Main View Mode: 0 -> Ürün Önerileri, 1 -> Uygulama Rehberi & Adımlar
    var activeMainMode by remember { mutableIntStateOf(0) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Tümü") }
    var storeSearchProduct by remember { mutableStateOf<String?>(null) }

    val skinTypes = listOf("Karma", "Kuru", "Yağlı", "Hassas", "Normal")
    val categoryTabs = listOf("Tümü", "Temizleyici", "Serum", "Nemlendirici", "Güneş Kremi", "Fondöten", "Kapatıcı", "Astar (Primer)")

    val creamParseResult = remember(currentRec?.creamSuggestionsJson) {
        RecommendationsHelper.parseProductSuggestions(currentRec?.creamSuggestionsJson)
    }
    val makeupParseResult = remember(currentRec?.makeupSuggestionsJson) {
        RecommendationsHelper.parseProductSuggestions(currentRec?.makeupSuggestionsJson)
    }

    val hasJsonError = creamParseResult.isError || makeupParseResult.isError

    val filteredCreams = remember(creamParseResult.items, searchQuery, selectedCategoryFilter) {
        RecommendationsHelper.filterProducts(creamParseResult.items, searchQuery, selectedCategoryFilter)
    }
    val filteredMakeup = remember(makeupParseResult.items, searchQuery, selectedCategoryFilter) {
        RecommendationsHelper.filterProducts(makeupParseResult.items, searchQuery, selectedCategoryFilter)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
    ) {
        // Top Header
        Column(modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Cilt Bakım & Öneri Hub'ı", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    val profileSubtitle = if (activeProfile != null) {
                        "${activeProfile?.skinType} Cilt · ${activeProfile?.skincareGoal} Hedefi"
                    } else {
                        "Profil Belirtilmedi · Varsayılan Öneriler"
                    }
                    Text(profileSubtitle, fontSize = 12.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(SurfaceCard, CircleShape)
                        .border(1.dp, BorderDefault, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Purple600, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Switcher Tabs (Ürün Önerileri vs Uygulama Rehberi)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderDefault, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeMainMode == 0) Navy900 else Color.Transparent)
                        .clickable { activeMainMode = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = if (activeMainMode == 0) White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Ürün Önerileri",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (activeMainMode == 0) White else TextSecondary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (activeMainMode == 1) Purple600 else Color.Transparent)
                        .clickable { activeMainMode = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (activeMainMode == 1) White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Uygulama Rehberi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (activeMainMode == 1) White else TextSecondary
                        )
                    }
                }
            }
        }

        if (activeMainMode == 0) {
            // MODE 0: PRODUCT RECOMMENDATIONS
            Column(modifier = Modifier.fillMaxSize()) {

                // Skin Type Selector Bar & Gemini Regeneration
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .background(SurfaceCard, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderDefault, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cilt Tipi Seçimi:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)

                        Button(
                            onClick = { viewModel.regenerateRecommendationWithGemini() },
                            enabled = !isLoading,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Yenileniyor...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI ile Yenile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(skinTypes) { st ->
                            val isSelected = selectedSkinType.equals(st, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Purple600 else SurfaceTint)
                                    .border(1.dp, if (isSelected) Purple600 else BorderDefault, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.selectSkinTypeForRecommendation(st) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    st,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) White else Navy900
                                )
                            }
                        }
                    }
                }

                // Search & Category Filters Bar
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Ürün adı, kategori veya içerik ara...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = Purple500,
                            unfocusedBorderColor = BorderDefault
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoryTabs) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) Navy900 else SurfaceCard)
                                    .border(1.dp, if (isSelected) Navy900 else BorderDefault, CircleShape)
                                    .clickable { selectedCategoryFilter = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Recommendations Dynamic List / State Cards
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Loading State
                    if (isLoading) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, BorderDefault)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    CircularProgressIndicator(color = Purple600)
                                    Text(
                                        "Yapay zeka ile $selectedSkinType cilt tipine özel öneriler hazırlanıyor...",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Navy900,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // 2. Error State (Gemini / API error)
                    if (recError != null && !isLoading) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Rose100),
                                border = BorderStroke(1.dp, Rose600.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Rose600, modifier = Modifier.size(32.dp))
                                    Text("Öneri Oluşturma Hatası", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Rose600)
                                    Text(recError ?: "", fontSize = 12.sp, color = Navy900, textAlign = TextAlign.Center)
                                    Button(
                                        onClick = { viewModel.regenerateRecommendationWithGemini() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                                    ) {
                                        Text("Tekrar Dene", color = White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 3. JSON Parse Error State
                    if (hasJsonError && recError == null && !isLoading) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Amber100),
                                border = BorderStroke(1.dp, Amber600.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Amber600, modifier = Modifier.size(32.dp))
                                    Text("Öneri Okuma Hatası", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Amber600)
                                    Text("Kayıtlı öneri verisi okunamadı veya biçim geçersiz.", fontSize = 12.sp, color = Navy900, textAlign = TextAlign.Center)
                                    Button(
                                        onClick = { viewModel.regenerateRecommendationWithGemini() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                                    ) {
                                        Text("AI ile Yeniden Oluştur", color = White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 4. Empty List State (No recommendations at all)
                    if (!isLoading && recError == null && !hasJsonError && creamParseResult.items.isEmpty() && makeupParseResult.items.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, BorderDefault)
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                                    Text("Bu cilt tipi için henüz öneri bulunamadı.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    Button(
                                        onClick = { viewModel.regenerateRecommendationWithGemini() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Purple600)
                                    ) {
                                        Text("AI ile Öneri Üret", color = White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // 5. No Search Results State
                    if (!isLoading && recError == null && !hasJsonError &&
                        (creamParseResult.items.isNotEmpty() || makeupParseResult.items.isNotEmpty()) &&
                        filteredCreams.isEmpty() && filteredMakeup.isEmpty()
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, BorderDefault)
                            ) {
                                Column(
                                    modifier = Modifier.padding(28.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.FilterListOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                    Text("Arama kriterlerinize uygun öneri bulunamadı.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    OutlinedButton(
                                        onClick = { searchQuery = ""; selectedCategoryFilter = "Tümü" },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Filtreleri Temizle", color = Navy900)
                                    }
                                }
                            }
                        }
                    }

                    // 6. Content Items
                    if (!isLoading && !hasJsonError) {
                        // Section: Cilt Bakım Önerileri
                        if (filteredCreams.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Mint100, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Spa, contentDescription = null, tint = Mint500, modifier = Modifier.size(16.dp))
                                    }
                                    Text("Cilt Bakım Önerileri", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                }
                            }

                            items(filteredCreams) { product ->
                                DynamicProductCard(
                                    product = product,
                                    cardBg = Mint100,
                                    onSearchStores = { storeSearchProduct = it }
                                )
                            }
                        }

                        // Section: Makyaj Önerileri
                        if (filteredMakeup.isNotEmpty()) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(Lilac100, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Brush, contentDescription = null, tint = Purple700, modifier = Modifier.size(16.dp))
                                    }
                                    Text("Makyaj Önerileri", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                }
                            }

                            items(filteredMakeup) { product ->
                                DynamicProductCard(
                                    product = product,
                                    cardBg = Lilac100,
                                    onSearchStores = { storeSearchProduct = it }
                                )
                            }
                        }

                        // Section: Genel Tavsiyeler
                        if (!currentRec?.generalTips.isNullOrBlank()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Amber100),
                                    border = BorderStroke(1.dp, Amber600.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Amber600, modifier = Modifier.size(20.dp))
                                            Text("Genel Bakım İpucu", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                        }
                                        Text(currentRec?.generalTips ?: "", fontSize = 12.sp, color = Navy700, lineHeight = 17.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // MODE 1: APPLICATION GUIDE & STEPS
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    GuideSectionCard(
                        title = "Gündüz Cilt Bakımı Sıralaması",
                        badgeText = "SABAH RUTİNİ",
                        badgeBg = Amber100,
                        badgeFg = Amber600,
                        steps = listOf(
                            GuideStepItem("1. Adım: Su Bazlı Nazik Temizleyici", "Gece boyunca biriken sebumu ve atıkları cildi germeden temizleyin."),
                            GuideStepItem("2. Adım: Antioksidan / C Vitamini Serumu", "Serbest radikallere ve çevre kirliliğine karşı cildinizi koruyun."),
                            GuideStepItem("3. Adım: Hafif Yapılı Nemlendirici", "Cilt bariyerinizi gün boyu nemli tutarak esneklik sağlayın."),
                            GuideStepItem("4. Adım: Geniş Spektrumlu Güneş Kremi (SPF 50+)", "UV ışınlarına karşı leke ve yaşlanma önleyici en kritik korumadır.")
                        )
                    )
                }

                item {
                    GuideSectionCard(
                        title = "Gece Cilt Bakımı Sıralaması",
                        badgeText = "AKŞAM RUTİNİ",
                        badgeBg = Purple100,
                        badgeFg = Purple700,
                        steps = listOf(
                            GuideStepItem("1. Adım: Çift Aşamal Temizlik (Misel Su / Yağ)", "Güneş kremi, makyaj ve kirliliği cildinizden tamamen arındırın."),
                            GuideStepItem("2. Adım: Su Bazlı Yüz Temizleme Jeli", "Gözeneklerde kalan artıkları derinlemesine yıkayıp temizleyin."),
                            GuideStepItem("3. Adım: Hedefe Yönelik Onarıcı Serum", "AHA/BHA, Niasinamid veya Retinol gibi aktif içerikleri uygulayın."),
                            GuideStepItem("4. Adım: Bariyer Güçlendirici Gece Kremi", "Gece boyu süren hücresel onarımı ve nem kilitlenmesini destekleyin.")
                        )
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Bakım Ürünleri Kullanım Kuralları", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            HorizontalDivider(color = BorderDefault)
                            RuleBulletPoint("İnce Yapıdan Kalın Yapıya Doğru:", "Ürünleri daima en akışkan olandan (tonik, serum) en yoğun olana (krem, yağ) doğru uygulayın.")
                            RuleBulletPoint("Aktif İçerik Çakışmalarına Dikkat Edin:", "C Vitamini ile BHA/AHA asitlerini aynı rutinde üst üste kullanmaktan kaçının.")
                            RuleBulletPoint("Patch Test Yapın:", "Yeni aldığınız bir ürünü tüm yüzünüze sürmeden önce bilek içinde veya kulak arkasında 24 saat test edin.")
                        }
                    }
                }
            }
        }
        if (storeSearchProduct != null) {
            StoreSearchBottomSheet(
                productName = storeSearchProduct!!,
                onDismiss = { storeSearchProduct = null }
            )
        }
    }
}

@Composable
fun DynamicProductCard(
    product: ProductSuggestion,
    cardBg: Color,
    onSearchStores: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(cardBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when (product.category) {
                                "Temizleyici" -> Icons.Default.WaterDrop
                                "Serum" -> Icons.Default.InvertColors
                                "Güneş Kremi" -> Icons.Default.WbSunny
                                "Fondöten", "Kapatıcı", "Astar (Primer)", "Makyaj" -> Icons.Default.Brush
                                else -> Icons.Default.Spa
                            },
                            contentDescription = null,
                            tint = Purple700,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(product.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900, lineHeight = 19.sp)
                        Box(
                            modifier = Modifier
                                .background(Purple100, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(product.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Purple700)
                        }
                    }
                }
            }

            // Active Ingredients
            if (product.activeIngredients.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Aktif Bileşenler:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceTint, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Purple600, modifier = Modifier.size(14.dp))
                        Text(product.activeIngredients, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Purple700)
                    }
                }
            }

            // Description
            if (product.description.isNotBlank()) {
                Text(product.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
            }

            // Usage Tip
            if (product.usageTip.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Mint100.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .border(1.dp, Mint500.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Mint500, modifier = Modifier.size(16.dp))
                    Text("Kullanım Önerisi: ${product.usageTip}", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                }
            }

            // Store Search Action
            if (onSearchStores != null) {
                OutlinedButton(
                    onClick = { onSearchStores(product.name) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Purple600),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Purple600, modifier = Modifier.size(16.dp))
                        Text("Mağazalarda Ara", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple600)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreSearchBottomSheet(
    productName: String,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val storeLinks = remember(productName) {
        com.example.data.repository.MarketSearchRepository.getStoreSearchLinks(productName)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mağazalarda Ara", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    Text(productName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Purple600)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextMuted)
                }
            }

            // Disclaimer Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Amber100),
                border = BorderStroke(1.dp, Amber600.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Amber600, modifier = Modifier.size(20.dp))
                    Text(
                        "Fiyatlar uygulama tarafından doğrulanmaz. Güncel fiyat ve stok bilgisini mağazada kontrol edin.",
                        fontSize = 12.sp,
                        color = Navy900,
                        lineHeight = 16.sp
                    )
                }
            }

            Text("Doğrulanabilir Mağaza Arama Bağlantıları", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)

            // Store List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                storeLinks.forEach { store ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse(store.searchUrl)
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceTint),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Purple100, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Purple700, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    store.platformName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Mağazada Ara", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple600)
                                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Purple600, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GuideSectionCard(
    title: String,
    badgeText: String,
    badgeBg: Color,
    badgeFg: Color,
    steps: List<GuideStepItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeFg)
                }
            }
            HorizontalDivider(color = BorderDefault)
            steps.forEach { step ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(step.stepTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Purple700)
                    Text(step.stepDesc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }
        }
    }
}

data class GuideStepItem(val stepTitle: String, val stepDesc: String)

@Composable
fun RuleBulletPoint(title: String, desc: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
        Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
    }
}
