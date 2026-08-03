# -*- coding: utf-8 -*-
import codecs

content = """package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SkinCareViewModel
import com.example.ui.theme.*

@Composable
fun RecommendationsScreen(
    viewModel: SkinCareViewModel,
    modifier: Modifier = Modifier
) {
    val activeProfile by viewModel.skinProfile.collectAsState()
    var selectedTab by remember { mutableStateOf("Tümü") }
    
    val tabs = listOf("Tümü", "Serum", "Nemlendirici", "Makyaj")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfacePage)
    ) {
        // Header Area
        Column(modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Sana uygun", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy900, lineHeight = 32.sp)
                    Text("Karma cilt · Nemlendirme hedefi · 14 ürün", fontSize = 12.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCard, CircleShape)
                        .border(1.dp, BorderDefault, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Navy700, modifier = Modifier.size(19.dp))
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(SurfaceCard, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderInput, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Ürün, içerik veya marka ara...", fontSize = 14.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) Navy900 else SurfaceCard, CircleShape)
                            .border(1.dp, if (isSelected) Color.Transparent else BorderDefault, CircleShape)
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            tab,
                            color = if (isSelected) White else Navy700,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        // Products List
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ProductCard(
                title = "Dengeleyici Jel-Krem",
                match = "%94", matchColor = Mint500,
                subtitle = "Nemlendirici · gündüz + gece",
                chips = listOf("Skualen", "Centella", "Yeşil çay"),
                chipBg = Mint100, chipFg = Green600,
                desc = "Yanakları kurutmadan nemlendirir, T-bölgesindeki sebumu dengeler.",
                price = "₺249", store = "Trendyol", priceDesc = "5 mağazanın en iyisi · kargo bedava",
                imageBg = Lilac100
            )
            
            ProductCard(
                title = "Niasinamid & Çinko Serum",
                match = "%91", matchColor = Mint500,
                subtitle = "Serum · sabah",
                chips = listOf("Niasinamid B3", "Cadı fındığı"),
                chipBg = Amber100, chipFg = Amber600, // Make second one amber by manual adjust inside
                desc = "Gözenek görünümünü azaltır; alkollü tonikle birlikte kullanma.",
                price = "₺189", store = "Gratis", priceDesc = "5 mağazanın en iyisi · 3 gün",
                imageBg = Blue100
            )
            
            ProductCard(
                title = "Yarı Mat Fondöten",
                match = "%78", matchColor = Amber600,
                subtitle = "Makyaj · Doğal & Hafif tercihine uygun",
                chips = listOf("Hiyalüronik asit", "Parfüm içerir"),
                chipBg = Rose100, chipFg = Rose600,
                desc = "Karma cilt için dengeli bitiş; hassas dönemlerde ara ver.",
                price = "₺329", store = "Watsons", priceDesc = "5 mağazanın en iyisi · mağazada",
                imageBg = Pink100
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProductCard(
    title: String, match: String, matchColor: Color, subtitle: String,
    chips: List<String>, chipBg: Color, chipFg: Color, desc: String,
    price: String, store: String, priceDesc: String, imageBg: Color
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(20.dp))
            .border(1.dp, BorderDefault, RoundedCornerShape(20.dp))
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier
                    .width(88.dp)
                    .height(104.dp)
                    .background(imageBg, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = Navy250, modifier = Modifier.size(32.dp))
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Navy900, modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(match, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = matchColor)
                        Text("UYUM", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = TextMuted)
                    }
                }
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    chips.forEachIndexed { i, chip ->
                        val bg = if (chip.contains("Parfüm") || chip.contains("Cadı")) chipBg else Mint100
                        val fg = if (chip.contains("Parfüm") || chip.contains("Cadı")) chipFg else Green600
                        Box(
                            modifier = Modifier
                                .background(bg, CircleShape)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(chip, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = fg)
                        }
                    }
                }
                Text(desc, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceTint)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(22.dp).background(Mint500, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("$price · $store", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                Text(priceDesc, fontSize = 12.sp, color = TextSecondary)
            }
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Purple600,
                modifier = Modifier.size(18.dp)
            )
        }
        
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceTint)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hepsiburada", fontSize = 14.sp, color = Navy700)
                    Text("₺265", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Watsons", fontSize = 14.sp, color = Navy700)
                    Text("₺279", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                }
                Row(modifier = Modifier.fillMaxWidth().background(Purple100).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Purple600, modifier = Modifier.size(16.dp))
                    Text("Trendyol'da kargo bedava — 40 ₺ tasarruf edersin.", fontSize = 12.sp, color = Purple700)
                }
            }
        }
    }
}
"""

with codecs.open("app/src/main/java/com/example/ui/screens/RecommendationsScreen.kt", "w", "utf-8") as f:
    f.write(content)
