package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.theme.*

data class DetailedProductRecommendation(
    val title: String,
    val brand: String,
    val category: String, // Temizleyici, Serum, Nemlendirici, Güneş Kremi, Makyaj, Bariyer
    val matchPercentage: String,
    val matchColor: Color,
    val usageTime: String, // Sabah, Akşam, Sabah + Akşam
    val activeIngredients: List<String>,
    val description: String,
    val skinTypeSuitability: String,
    val lowestPrice: String,
    val bestStore: String,
    val priceDetails: String,
    val otherPrices: List<Pair<String, String>>, // Store -> Price
    val cardBg: Color
)

val sampleProductDatabase = listOf(
    DetailedProductRecommendation(
        title = "Effaclar Arındırıcı Yüz Temizleme Jeli",
        brand = "La Roche-Posay",
        category = "Temizleyici",
        matchPercentage = "%96",
        matchColor = Mint500,
        usageTime = "Sabah + Akşam",
        activeIngredients = listOf("Çinko PCA", "Termal Su", "Sabunsuz"),
        description = "Hassas ve karma/yağlı ciltler için fazla sebumu nazikçe arındırır, gözenekleri tıkamadan ferahlatır.",
        skinTypeSuitability = "Karma, Yağlı ve Akneye Eğilimli Ciltler",
        lowestPrice = "₺349",
        bestStore = "Gratis",
        priceDetails = "En uygun fiyat · Ücretsiz mağazadan teslim",
        otherPrices = listOf("Trendyol" to "₺375", "Watsons" to "₺389", "Hepsiburada" to "₺360"),
        cardBg = Blue100
    ),
    DetailedProductRecommendation(
        title = "Niacinamide 10% + Zinc 1%",
        brand = "The Ordinary",
        category = "Serum",
        matchPercentage = "%94",
        matchColor = Mint500,
        usageTime = "Sabah veya Akşam",
        activeIngredients = listOf("Niasinamid (B3)", "Çinko", "Su Bazlı"),
        description = "Geniş gözenek görünümünü sıkılaştırır, cilt tonu eşitsizliklerini giderir ve kızarıklıkları yatıştırır.",
        skinTypeSuitability = "Tüm Cilt Tipleri (Karma/Yağlı Öncelikli)",
        lowestPrice = "₺289",
        bestStore = "Trendyol",
        priceDetails = "Kargo bedava · Fırsat ürünü",
        otherPrices = listOf("Hepsiburada" to "₺299", "Sephora" to "₺320"),
        cardBg = Lilac100
    ),
    DetailedProductRecommendation(
        title = "Hydrating Hyaluronic Acid Cleanser",
        brand = "CeraVe",
        category = "Temizleyici",
        matchPercentage = "%92",
        matchColor = Mint500,
        usageTime = "Sabah + Akşam",
        activeIngredients = listOf("3 Temel Seramid", "Hyalüronik Asit", "MVE Teknolojisi"),
        description = "Cildin doğal koruyucu bariyerine zarar vermeden nemlendirerek temizler. Kuruluk ve gerginlik hissettirmez.",
        skinTypeSuitability = "Normal, Kuru ve Hassas Ciltler",
        lowestPrice = "₺295",
        bestStore = "Watsons",
        priceDetails = "Flaş İndirim · Mağaza Stoklarında",
        otherPrices = listOf("Trendyol" to "₺310", "Gratis" to "₺325"),
        cardBg = Mint100
    ),
    DetailedProductRecommendation(
        title = "Relief Sun : Rice + Probiotics SPF50+ PA++++",
        brand = "Beauty of Joseon",
        category = "Güneş Kremi",
        matchPercentage = "%98",
        matchColor = Mint500,
        usageTime = "Her Sabah",
        activeIngredients = listOf("Pirinç Özü %30", "Tahıl Probiyotikleri", "Niasinamid"),
        description = "Beyazlık bırakmayan, nemlendirici krem yapısında hibrit güneş koruyucu. Makyaj altında topaklanma yapmaz.",
        skinTypeSuitability = "Hassas, Karma ve Kuru Ciltler",
        lowestPrice = "₺480",
        bestStore = "Trendyol",
        priceDetails = "Kore Cilt Bakımı Orijinal İthalat",
        otherPrices = listOf("Hepsiburada" to "₺495", "Watsons" to "₺520"),
        cardBg = Amber100
    ),
    DetailedProductRecommendation(
        title = "Centella Unscented Serum",
        brand = "Purito",
        category = "Serum",
        matchPercentage = "%95",
        matchColor = Mint500,
        usageTime = "Sabah + Akşam",
        activeIngredients = listOf("Centella Asiatica %49", "Niasinamid", "Peptitler"),
        description = "Parfümsüz ve esansiyel yağsız formülü ile hassaslaşmış cilt bariyerini anında yatıştırır ve onarır.",
        skinTypeSuitability = "Hassas, Kızarık ve Onarım İsteyen Ciltler",
        lowestPrice = "₺410",
        bestStore = "Hepsiburada",
        priceDetails = "Bariyer Onarım Özel Kampanyası",
        otherPrices = listOf("Trendyol" to "₺430", "Gratis" to "₺450"),
        cardBg = Rose100
    ),
    DetailedProductRecommendation(
        title = "Advanced Snail 96 Mucin Power Essence",
        brand = "COSRX",
        category = "Bariyer",
        matchPercentage = "%93",
        matchColor = Mint500,
        usageTime = "Sabah + Akşam",
        activeIngredients = listOf("Salyangoz Salgısı Filtratı %96", "Hyalüronik Asit"),
        description = "Cilde derinlemesine nem kazandırır, pürüzleri giderir, cam cilt (glass skin) görünümü ve esneklik sağlar.",
        skinTypeSuitability = "Kuru, Dehidre ve Donuk Ciltler",
        lowestPrice = "₺450",
        bestStore = "Gratis",
        priceDetails = "Sadakat Kartına Özel %15 İndirim",
        otherPrices = listOf("Trendyol" to "₺470", "Watsons" to "₺485"),
        cardBg = Lilac100
    ),
    DetailedProductRecommendation(
        title = "Sensibio H2O Yatıştırıcı Misel Su",
        brand = "Bioderma",
        category = "Temizleyici",
        matchPercentage = "%97",
        matchColor = Mint500,
        usageTime = "Akşam (İlk Aşama)",
        activeIngredients = listOf("Misel Teknolojisi", "Salatalık Özü", "Fizyolojik pH"),
        description = "Makyajı ve güneş kremini ovalamadan tek hamlede nazikçe temizler. Göz çevresinde yanma yapmaz.",
        skinTypeSuitability = "Tüm Cilt Tipleri & En Hassas Ciltler",
        lowestPrice = "₺260",
        bestStore = "Trendyol",
        priceDetails = "İkili Fırsat Paketi Avantajı",
        otherPrices = listOf("Watsons" to "₺280", "Gratis" to "₺290"),
        cardBg = Pink100
    ),
    DetailedProductRecommendation(
        title = "Fit Me Matte + Poreless Yarı Mat Fondöten",
        brand = "Maybelline New York",
        category = "Makyaj",
        matchPercentage = "%89",
        matchColor = Amber600,
        usageTime = "Gündüz / Makyaj",
        activeIngredients = listOf("Mikro Pudra Partikülleri", "Gözenek Gizleyici"),
        description = "Karma ve yağlı ciltlerde gün boyu parlamayı kontrol altında tutar, gözenekleri pürüzsüzleştirir.",
        skinTypeSuitability = "Karma ve Yağlı Ciltler",
        lowestPrice = "₺215",
        bestStore = "Gratis",
        priceDetails = "Seçili Tonlarda İndirim",
        otherPrices = listOf("Watsons" to "₺230", "Trendyol" to "₺225"),
        cardBg = Purple100
    )
)

@Composable
fun RecommendationsScreen(
    viewModel: SkinCareViewModel,
    modifier: Modifier = Modifier
) {
    val activeProfile by viewModel.skinProfile.collectAsState()
    
    // Main View Mode: 0 -> Ürün Önerileri, 1 -> Uygulama Rehberi & Adımlar
    var activeMainMode by remember { mutableIntStateOf(0) }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("Tümü") }
    
    val categoryTabs = listOf("Tümü", "Temizleyici", "Serum", "Nemlendirici", "Güneş Kremi", "Makyaj", "Bariyer")
    
    val userSkinType = activeProfile?.skinType ?: "Karma"
    val userGoal = activeProfile?.skincareGoal ?: "Nemlendirme"

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
                    Text("Cilt Bakım & Öneri Hub'ı", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    Text("$userSkinType Cilt · $userGoal Hedefi", fontSize = 12.sp, color = TextSecondary)
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
                // Search + Category Filters
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Ürün, marka veya aktif içerik ara...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = Purple600,
                            unfocusedBorderColor = BorderInput
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoryTabs.forEach { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Box(
                                modifier = Modifier
                                    .background(if (isSelected) Navy900 else SurfaceCard, CircleShape)
                                    .border(1.dp, if (isSelected) Color.Transparent else BorderDefault, CircleShape)
                                    .clickable { selectedCategoryFilter = cat }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) White else Navy700
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val filteredProducts = remember(searchQuery, selectedCategoryFilter) {
                    sampleProductDatabase.filter { item ->
                        val matchesCategory = (selectedCategoryFilter == "Tümü" || item.category.equals(selectedCategoryFilter, ignoreCase = true))
                        val matchesSearch = searchQuery.isBlank() ||
                                item.title.contains(searchQuery, ignoreCase = true) ||
                                item.brand.contains(searchQuery, ignoreCase = true) ||
                                item.description.contains(searchQuery, ignoreCase = true) ||
                                item.activeIngredients.any { it.contains(searchQuery, ignoreCase = true) }
                        matchesCategory && matchesSearch
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (filteredProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                                Text("Aramanıza uygun ürün bulunamadı.", fontSize = 14.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            }
                        }
                    } else {
                        filteredProducts.forEach { item ->
                            RichProductCard(product = item)
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        } else {
            // MODE 1: STEP-BY-STEP APPLICATION GUIDE (Yapılması ve Uygulanması Gerekenler)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Purple600, Purple800)),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Amber600, modifier = Modifier.size(20.dp))
                            Text("Dermatolojik Adım Adım Bakım Rehberi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
                        }
                        Text(
                            "$userSkinType cildiniz için sabah ve akşam yapılması gereken doğru sıralama, ürün katmanlama kuralları ve içerik çakışmaları aşağıda özetlenmiştir.",
                            fontSize = 12.sp,
                            color = White.copy(alpha = 0.9f),
                            lineHeight = 17.sp
                        )
                    }
                }

                // Morning Routine Steps
                GuideSectionCard(
                    title = "🌅 SABAH RUTİNİ (5 Adım)",
                    badgeText = "Koruma & Nem",
                    badgeBg = Amber100,
                    badgeFg = Amber600,
                    steps = listOf(
                        GuideStepItem("1. Adım: Su Bazlı Yıkama Jeli", "Gece boyunca biriken sebumu ve teri cildinizden ılık su ile nazikçe arındırın. Cildi gıcırtılı kurutmamalıdır."),
                        GuideStepItem("2. Adım: Yatıştırıcı Tonik / Esans", "Cildinizin pH dengesini düzenleyin. Nemli cilde uygulayarak sonraki serumların emilimini 2 kat artırın."),
                        GuideStepItem("3. Adım: C Vitamini Serumu", "Çevre kirliliği ve serbest radikallere karşı cildi korur, leke oluşumunu engeller ve cilde ışıltı kazandırır."),
                        GuideStepItem("4. Adım: Cilt Tipine Uygun Nemlendirici", "Nem moleküllerini cildinizde hapseder. $userSkinType cildiniz için gözenek tıkamayan hafif yapılı formül seçin."),
                        GuideStepItem("5. Adım: SPF 50+ Güneş Kremi (EN ÖNEMLİ!)", "2 Parmak kuralı ile yüz ve boyun bölgesine sürün. Yaşlanma ve leke oluşumunu önleyen 1 numaralı adımdır.")
                    )
                )

                // Evening Routine Steps
                GuideSectionCard(
                    title = "🌙 AKŞAM RUTİNİ (5 Adım)",
                    badgeText = "Onarım & Yenilenme",
                    badgeBg = Lilac100,
                    badgeFg = Purple700,
                    steps = listOf(
                        GuideStepItem("1. Adım: Çift Aşama Temizleme (Double Cleansing)", "Önce Yağ Bazlı Temizleyici ile güneş kremi ve makyajı eritin, ardından Su Bazlı Jel ile gözenekleri yıkayın."),
                        GuideStepItem("2. Adım: Hedefe Yönelik Serum / Asit (AHA/BHA veya Retinol)", "Haftada 2-3 gece gözenek sıkılaştırıcı BHA veya hücre yenileyici Retinol uygulayın."),
                        GuideStepItem("3. Adım: Göz Çevresi Kremi", "Yüzük parmağınızla tampon hareketlerle nazikçe uygulayın. Çekme hareketi yapmayın."),
                        GuideStepItem("4. Adım: Yoğun Bariyer Nemlendirici (Seramid & Skualen)", "Gece boyunca cildin nem bariyerini (skin barrier) yeniden inşa eder."),
                        GuideStepItem("5. Adım: Gece Nem Maskesi / Dudak Bakımı", "Ekstra kuruluk hissettiğiniz günlerde nem kilitleyici uyku maskesi ekleyin.")
                    )
                )

                // Layering & Collision Rules Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Purple600, modifier = Modifier.size(22.dp))
                            Text("Ürün Katmanlama & İçerik Kuralları", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        }

                        Divider(color = BorderDefault)

                        RuleBulletPoint("🌊 İnce Yapıdan Yoğuna Sıralama:", "Her zaman en akışkan su bazlı üründen (Tonik -> Serum) en yoğun krem/yağ yapılı ürüne doğru katmanlama yapın.")
                        RuleBulletPoint("💧 Hyalüronik Asit Nemli Cilde Sürülür:", "Kuru cilde sürülürse tam tersine cildin alt katmanlarındaki suyu çeker! Nemli yüze sürüp nemlendiriciyle kilitleyin.")
                        RuleBulletPoint("⛔ C Vitamini + Retinol Birlikte Kullanılmaz:", "Tahrişe yol açabilir. C Vitaminini her sabah, Retinol'ü ise akşamları kullanın.")
                        RuleBulletPoint("⛔ AHA/BHA + Retinol Aynı Gecede Sürmeyin:", "Bariyeri yıpratır. Farklı gecelerde dönüşümlü (Skin Cycling) olarak uygulayın.")
                        RuleBulletPoint("✅ Niasinamid + Salisilik Asit Harika İkilidir:", "Gözenek ve siyah nokta temizliğinde güvenle birlikte kullanılabilir.")
                    }
                }

                // Do's and Don'ts Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Do's
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Mint100),
                        border = BorderStroke(1.dp, Mint500.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(18.dp))
                                Text("Yapılması Gerekenler", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Green600)
                            }
                            Text("• Yeni ürünü önce çene altında test et (Patch test).", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                            Text("• Her sabah düzenli SPF 50+ güneş kremi kullan.", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                            Text("• Asit kullandıktan sonra bariyer kremi ile destekle.", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                        }
                    }

                    // Don'ts
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Rose100),
                        border = BorderStroke(1.dp, Rose600.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = Rose600, modifier = Modifier.size(18.dp))
                                Text("Sakın Yapmayın!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Rose600)
                            }
                            Text("• Sivilce ve siyah noktaları elinle sıkma.", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                            Text("• Cildi gıcırdayana kadar Sert sabunla kurutma.", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                            Text("• Aynı anda 3'ten fazla aktif asit karıştırma.", fontSize = 11.sp, color = Navy900, lineHeight = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun RichProductCard(product: DetailedProductRecommendation) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Product Icon Box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(product.cardBg, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (product.category) {
                            "Temizleyici" -> Icons.Default.WaterDrop
                            "Serum" -> Icons.Default.InvertColors
                            "Güneş Kremi" -> Icons.Default.WbSunny
                            "Makyaj" -> Icons.Default.Brush
                            "Bariyer" -> Icons.Default.Shield
                            else -> Icons.Default.Spa
                        },
                        contentDescription = null,
                        tint = Purple700,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.brand, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Purple600)
                            Text(product.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900, lineHeight = 19.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(product.matchColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(product.matchPercentage, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = product.matchColor)
                        }
                    }

                    Text("${product.category} · ${product.usageTime}", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)

                    // Active Ingredient Chips
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        product.activeIngredients.forEach { ing ->
                            Box(
                                modifier = Modifier
                                    .background(Purple100, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(ing, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Purple700)
                            }
                        }
                    }

                    Text(product.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }

            // Price Bar Dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceTint)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Mint500, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = White, modifier = Modifier.size(13.dp))
                    }
                    Column {
                        Text("${product.lowestPrice} · ${product.bestStore}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Text(product.priceDetails, fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (expanded) "Gizle" else "Fiyat Karşılaştır", fontSize = 11.sp, color = Purple600, fontWeight = FontWeight.SemiBold)
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Purple600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceTint)
                        .padding(bottom = 8.dp)
                ) {
                    Divider(color = BorderDefault)
                    product.otherPrices.forEach { (store, price) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(store, fontSize = 12.sp, color = Navy700)
                            Text(price, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                        }
                    }
                }
            }
        }
    }
}

data class GuideStepItem(val stepTitle: String, val stepDesc: String)

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
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeFg)
                }
            }

            Divider(color = BorderDefault)

            steps.forEach { step ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(step.stepTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Purple700)
                    Text(step.stepDesc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
fun RuleBulletPoint(title: String, desc: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
        Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
    }
}
