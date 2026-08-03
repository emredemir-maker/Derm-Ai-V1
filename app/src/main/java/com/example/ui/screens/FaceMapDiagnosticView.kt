package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.api.ProfileAnalysisResult
import com.example.ui.theme.*
import java.io.File

data class FaceZoneData(
    val id: String,
    val name: String,
    val issue: String,
    val severity: String, // "Yüksek Öncelik", "Orta Öncelik", "Hafif / Denge"
    val severityColor: Color,
    val recommendedIngredient: String,
    val routineAction: String,
    val tip: String,
    val normalizedX: Float, // 0.0 to 1.0 on face canvas
    val normalizedY: Float  // 0.0 to 1.0 on face canvas
)

fun getDefaultFaceZones(
    result: ProfileAnalysisResult? = null,
    userConcerns: List<String> = emptyList()
): List<FaceZoneData> {
    if (result != null && result.faceMapRegions.isNotEmpty()) {
        return result.faceMapRegions.mapIndexed { idx, region ->
            val color = when {
                region.issue.contains("kuru", ignoreCase = true) || region.issue.contains("kırışık", ignoreCase = true) -> Amber600
                region.issue.contains("akne", ignoreCase = true) || region.issue.contains("sebum", ignoreCase = true) || region.issue.contains("yağ", ignoreCase = true) || region.issue.contains("leke", ignoreCase = true) -> Rose600
                region.issue.contains("kızar", ignoreCase = true) || region.issue.contains("hassas", ignoreCase = true) -> Purple600
                else -> Mint500
            }
            FaceZoneData(
                id = "zone_$idx",
                name = region.regionName.ifBlank { "Bölge ${idx + 1}" },
                issue = region.issue.ifBlank { "Hafif Nem İhtiyacı" },
                severity = if (color == Rose600) "Yüksek Öncelik" else if (color == Amber600) "Orta Öncelik" else "Dengeli",
                severityColor = color,
                recommendedIngredient = region.recommendedIngredient.ifBlank { "Hyalüronik Asit & Niasinamid" },
                routineAction = "Bu bölgeye özel jel temizleyici sonrası tampon tampon serum uygulayın.",
                tip = "Tampon hareketlerle masaj yaparak uygulayın, çitmeyin.",
                normalizedX = if (region.x > 0) region.x else 0.5f,
                normalizedY = if (region.y > 0) region.y else 0.3f
            )
        }
    }

    val hasSpots = userConcerns.any { it.lowercase().contains("leke") || it.lowercase().contains("pigment") }
    val hasBlackheads = userConcerns.any { it.lowercase().contains("siyah") || it.lowercase().contains("komedon") || it.lowercase().contains("gözenek") }
    val hasAcne = userConcerns.any { it.lowercase().contains("akne") || it.lowercase().contains("sivilce") }
    val hasWrinkles = userConcerns.any { it.lowercase().contains("kırış") || it.lowercase().contains("yaş") }
    val hasRedness = userConcerns.any { it.lowercase().contains("kızar") || it.lowercase().contains("hassas") }

    return listOf(
        FaceZoneData(
            id = "forehead",
            name = "Alın Bölgesi",
            issue = if (hasWrinkles) "İnce Mimik Çizgileri & Kuruluk" else "Nemsizlik & Yağ Dengesi",
            severity = if (hasWrinkles) "Yüksek Öncelik" else "Orta Öncelik",
            severityColor = if (hasWrinkles) Rose600 else Amber600,
            recommendedIngredient = if (hasWrinkles) "Peptit Kompleks & Retinol %0.3" else "Hyalüronik Asit & Niasinamid",
            routineAction = "Gece rutininde nem bağlayıcı peptit serum uygulayın.",
            tip = "Mimik çizgilerini önlemek için nem kilitleyici seramidli krem ile destekleyin.",
            normalizedX = 0.50f,
            normalizedY = 0.22f
        ),
        FaceZoneData(
            id = "tzone_nose",
            name = "T-Bölgesi & Burun",
            issue = if (hasBlackheads) "Tıkanmış Gözenek & Siyah Noktalar" else "Aşırı Sebum Parlaması",
            severity = "Yüksek Öncelik",
            severityColor = Rose600,
            recommendedIngredient = "Salisilik Asit (BHA %2) & Çinko PCA",
            routineAction = "Haftada 2-3 gece BHA tonik ile derinlemesine gözenek arındırma yapın.",
            tip = "Komedojenik (gözenek tıkayan) ağır yağ içeren kremlerden T-bölgesinde kaçının.",
            normalizedX = 0.50f,
            normalizedY = 0.45f
        ),
        FaceZoneData(
            id = "left_cheek",
            name = "Sol Yanak",
            issue = if (hasSpots) "Güneş / Akne Lekeleri & Ton Eşitsizliği" else "Bariyer Desteği İhtiyacı",
            severity = if (hasSpots) "Yüksek Öncelik" else "Dengeli",
            severityColor = if (hasSpots) Rose600 else Mint500,
            recommendedIngredient = if (hasSpots) "C Vitamini %10 & Arbutin %2" else "Skualen & Seramid NP",
            routineAction = "Gündüz C vitamini ve geniş spektrumlu SPF50+ güneş kremi uygulayın.",
            tip = "Lekelerin koyulaşmasını önlemek için her gün güneş kremi tazeleyin.",
            normalizedX = 0.30f,
            normalizedY = 0.52f
        ),
        FaceZoneData(
            id = "right_cheek",
            name = "Sağ Yanak",
            issue = if (hasRedness) "Kızarıklık & Bariyer Hassasiyeti" else if (hasSpots) "Güneş / Akne Lekeleri" else "Nem İhtiyacı",
            severity = if (hasRedness || hasSpots) "Yüksek Öncelik" else "Orta Öncelik",
            severityColor = if (hasRedness) Purple600 else if (hasSpots) Rose600 else Amber600,
            recommendedIngredient = if (hasRedness) "Centella Asiatica (Cica) & Azelaik Asit" else "Niasinamid & Hyalüronik Asit",
            routineAction = "Sabah-akşam bariyer onarıcı Cica krem tamponlayarak uygulayın.",
            tip = "Sıcak su ile yıkamaktan ve parfümlü ürünlerden uzak durun.",
            normalizedX = 0.70f,
            normalizedY = 0.52f
        ),
        FaceZoneData(
            id = "under_eyes",
            name = "Göz Altı Çevresi",
            issue = "İnce Kuruluk & Torbalanma Meyli",
            severity = "Orta Öncelik",
            severityColor = Amber600,
            recommendedIngredient = "Kafein %5 & C Vitamini Türevi",
            routineAction = "Sabah kafeinli göz serumu, gece yoğun peptitli göz kremi.",
            tip = "Göz çevresine yalnızca yüzük parmağınızla pıt pıt hareketlerle sürün.",
            normalizedX = 0.50f,
            normalizedY = 0.35f
        ),
        FaceZoneData(
            id = "chin",
            name = "Çene & Çene Hattı",
            issue = if (hasAcne) "Aktif Sivilce Tıkanıklığı & Pürüzler" else "Gözenek Tıkanıklığı",
            severity = "Yüksek Öncelik",
            severityColor = Rose600,
            recommendedIngredient = "Niasinamid %10 & Çay Ağacı Yağı",
            routineAction = "Lokal leke ve sivilce karşıtı niasinamid serumu gece uygulayın.",
            tip = "Çene hattını ellerinizle sık dokunmaktan koruyun.",
            normalizedX = 0.50f,
            normalizedY = 0.76f
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceMapDiagnosticCard(
    analysisResult: ProfileAnalysisResult? = null,
    photoPath: String? = null,
    userConcerns: List<String> = emptyList(),
    onApplyToProfile: (skinType: String, concerns: List<String>, goal: String) -> Unit = { _, _, _ -> },
    onRetakePhoto: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val zones = remember(analysisResult, userConcerns) { getDefaultFaceZones(analysisResult, userConcerns) }
    var selectedZone by remember { mutableStateOf<FaceZoneData?>(zones.firstOrNull()) }
    var showAllRegionsList by remember { mutableStateOf(false) }

    val detectedType = analysisResult?.skinType?.ifBlank { "Karma Cilt" } ?: "Karma Cilt (T-Bölgesi Yağlı)"
    val detectedConcerns = if (!analysisResult?.concerns.isNullOrEmpty()) analysisResult!!.concerns else listOf("Aşırı Sebum", "Nemsizlik", "Geniş Gözenekler")
    val detectedGoal = analysisResult?.goal?.ifBlank { "Nem & Sebum Dengesi" } ?: "Nem & Sebum Dengesi"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(SurfaceCard, RoundedCornerShape(24.dp))
            .border(1.dp, BorderDefault, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    Text(
                        text = "AI Yüz Haritası & Teşhis",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (!photoPath.isNullOrBlank()) "Gerçek Fotoğraf Üzerinde Haritalama" else "Bölgesel Cilt Haritası",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Mint100,
                    shape = CircleShape
                ) {
                    Text(
                        text = "%94 AI Güven",
                        color = Green600,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        onApplyToProfile(detectedType.split(" ").first(), detectedConcerns, detectedGoal)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Onayla", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = White)
                }
            }
        }

        // Visual Canvas Overlay
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Lilac50, SurfacePage)))
                .border(1.dp, Lilac200, RoundedCornerShape(20.dp))
        ) {
            val containerWidth = maxWidth
            val containerHeight = maxHeight

            val hasRealPhoto = !photoPath.isNullOrBlank() && File(photoPath).exists()

            if (hasRealPhoto) {
                // Real User Photo Background
                AsyncImage(
                    model = File(photoPath!!),
                    contentDescription = "Yüz Fotoğrafı",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Darkening overlay for pin contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.20f),
                                    Color.Black.copy(alpha = 0.45f)
                                )
                            )
                        )
                )
            } else {
                // High-fidelity Realistic Skin Tone Face Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerX = width / 2f
                    val centerY = height * 0.48f

                    // Head/Face Base Soft Skin Tone Fill
                    val faceWidth = width * 0.46f
                    val faceHeight = height * 0.72f

                    // Outer Shadow / Aura
                    drawOval(
                        color = Purple300.copy(alpha = 0.3f),
                        topLeft = Offset(centerX - faceWidth / 2f - 6.dp.toPx(), centerY - faceHeight / 2f - 6.dp.toPx()),
                        size = Size(faceWidth + 12.dp.toPx(), faceHeight + 12.dp.toPx())
                    )

                    // Skin Fill
                    drawOval(
                        color = Color(0xFFFBE8E0), // Realistic natural skin tint
                        topLeft = Offset(centerX - faceWidth / 2f, centerY - faceHeight / 2f),
                        size = Size(faceWidth, faceHeight)
                    )

                    // Face Border Stroke
                    drawOval(
                        color = Purple500.copy(alpha = 0.6f),
                        topLeft = Offset(centerX - faceWidth / 2f, centerY - faceHeight / 2f),
                        size = Size(faceWidth, faceHeight),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // T-Zone Shading Highlight
                    drawOval(
                        color = Rose600.copy(alpha = 0.12f),
                        topLeft = Offset(centerX - faceWidth * 0.20f, centerY - faceHeight * 0.38f),
                        size = Size(faceWidth * 0.40f, faceHeight * 0.48f)
                    )

                    // Eyebrows
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX - faceWidth * 0.32f, centerY - faceHeight * 0.22f)
                            quadraticTo(
                                centerX - faceWidth * 0.18f, centerY - faceHeight * 0.26f,
                                centerX - faceWidth * 0.06f, centerY - faceHeight * 0.21f
                            )
                        },
                        color = Navy900.copy(alpha = 0.7f),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX + faceWidth * 0.06f, centerY - faceHeight * 0.21f)
                            quadraticTo(
                                centerX + faceWidth * 0.18f, centerY - faceHeight * 0.26f,
                                centerX + faceWidth * 0.32f, centerY - faceHeight * 0.22f
                            )
                        },
                        color = Navy900.copy(alpha = 0.7f),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Eyes Shading & Pupil
                    drawOval(
                        color = White,
                        topLeft = Offset(centerX - faceWidth * 0.28f, centerY - faceHeight * 0.15f),
                        size = Size(faceWidth * 0.20f, faceHeight * 0.09f)
                    )
                    drawOval(
                        color = Navy900.copy(alpha = 0.7f),
                        topLeft = Offset(centerX - faceWidth * 0.28f, centerY - faceHeight * 0.15f),
                        size = Size(faceWidth * 0.20f, faceHeight * 0.09f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = Navy900,
                        radius = 4.dp.toPx(),
                        center = Offset(centerX - faceWidth * 0.18f, centerY - faceHeight * 0.105f)
                    )

                    drawOval(
                        color = White,
                        topLeft = Offset(centerX + faceWidth * 0.08f, centerY - faceHeight * 0.15f),
                        size = Size(faceWidth * 0.20f, faceHeight * 0.09f)
                    )
                    drawOval(
                        color = Navy900.copy(alpha = 0.7f),
                        topLeft = Offset(centerX + faceWidth * 0.08f, centerY - faceHeight * 0.15f),
                        size = Size(faceWidth * 0.20f, faceHeight * 0.09f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = Navy900,
                        radius = 4.dp.toPx(),
                        center = Offset(centerX + faceWidth * 0.18f, centerY - faceHeight * 0.105f)
                    )

                    // Cheek Flush Shading
                    drawCircle(
                        color = Pink400.copy(alpha = 0.2f),
                        radius = 18.dp.toPx(),
                        center = Offset(centerX - faceWidth * 0.25f, centerY + faceHeight * 0.05f)
                    )
                    drawCircle(
                        color = Pink400.copy(alpha = 0.2f),
                        radius = 18.dp.toPx(),
                        center = Offset(centerX + faceWidth * 0.25f, centerY + faceHeight * 0.05f)
                    )

                    // Nose Bridge & Tip
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX, centerY - faceHeight * 0.12f)
                            lineTo(centerX - 5.dp.toPx(), centerY + faceHeight * 0.04f)
                            quadraticTo(centerX, centerY + faceHeight * 0.09f, centerX + 5.dp.toPx(), centerY + faceHeight * 0.04f)
                        },
                        color = Navy900.copy(alpha = 0.5f),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Lips
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX - faceWidth * 0.16f, centerY + faceHeight * 0.24f)
                            quadraticTo(centerX, centerY + faceHeight * 0.19f, centerX + faceWidth * 0.16f, centerY + faceHeight * 0.24f)
                            quadraticTo(centerX, centerY + faceHeight * 0.31f, centerX - faceWidth * 0.16f, centerY + faceHeight * 0.24f)
                        },
                        color = Rose600.copy(alpha = 0.6f)
                    )
                }
            }

            // Quick Camera Snap Button on Top Right of Image Canvas
            Surface(
                color = White.copy(alpha = 0.9f),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clickable { onRetakePhoto() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (hasRealPhoto) Icons.Default.Refresh else Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Purple700,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (hasRealPhoto) "Değiştir" else "Yüzünü Tara",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple700
                    )
                }
            }

            // Interactive Map Pins
            zones.forEach { zone ->
                val isSelected = selectedZone?.id == zone.id
                val pinX = containerWidth * zone.normalizedX
                val pinY = containerHeight * zone.normalizedY

                Box(
                    modifier = Modifier
                        .offset(x = pinX - 35.dp, y = pinY - 14.dp)
                        .shadow(if (isSelected) 8.dp else 2.dp, CircleShape)
                        .background(if (isSelected) White else zone.severityColor, CircleShape)
                        .border(if (isSelected) 2.5.dp else 1.5.dp, if (isSelected) zone.severityColor else White, CircleShape)
                        .clickable { selectedZone = zone }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (isSelected) zone.severityColor else White, CircleShape)
                        )
                        Text(
                            text = zone.name.split(" ").firstOrNull() ?: zone.name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Navy900 else White
                        )
                    }
                }
            }
        }

        // Region Selection Chips Row
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("İncelenecek Yüz Bölgesi Seçin:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(zones) { zone ->
                    val isSelected = selectedZone?.id == zone.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedZone = zone },
                        label = {
                            Text(
                                text = zone.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(zone.severityColor, CircleShape)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple100,
                            selectedLabelColor = Purple700,
                            containerColor = SurfaceTint,
                            labelColor = Navy700
                        ),
                        shape = CircleShape
                    )
                }
            }
        }

        // Selected Zone Breakdown Detail Card
        selectedZone?.let { zone ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceTint, RoundedCornerShape(18.dp))
                    .border(1.dp, BorderDefault, RoundedCornerShape(18.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Zone Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(zone.severityColor, CircleShape)
                        )
                        Text(
                            text = zone.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = zone.severityColor.copy(alpha = 0.15f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = zone.severity,
                            color = zone.severityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Issue Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text("TESPİT EDİLEN DURUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(zone.issue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
                }

                // Ingredient & Action Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Lilac50, RoundedCornerShape(12.dp))
                            .border(1.dp, Lilac200, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = Purple600, modifier = Modifier.size(14.dp))
                            Text("Önerilen Bileşen", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Purple700)
                        }
                        Text(zone.recommendedIngredient, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    }
                }

                // Detailed Action Routine
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(White, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Green600, modifier = Modifier.size(14.dp))
                        Text("Bölgesel Bakım Rutini & Uygulama", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy900)
                    }
                    Text(zone.routineAction, fontSize = 12.sp, color = Navy700, lineHeight = 16.sp)
                    Text("💡 İpucu: ${zone.tip}", fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                }
            }
        }

        // Accordion for Full Face Routine Overview
        Surface(
            color = SurfaceTint,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showAllRegionsList = !showAllRegionsList }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = Purple600, modifier = Modifier.size(18.dp))
                    Text("Tüm Bölgelerin Özet Önerileri", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                }
                Icon(
                    imageVector = if (showAllRegionsList) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }

        AnimatedVisibility(visible = showAllRegionsList) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderDefault, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                zones.forEach { zone ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1.2f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(zone.severityColor, CircleShape))
                            Text(zone.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900, maxLines = 1)
                        }
                        Text(
                            text = zone.recommendedIngredient,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Purple700,
                            modifier = Modifier.weight(1.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Divider(color = Lilac100)
                }
            }
        }

        // Bottom AI Summary & Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Lilac100, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = Purple600, modifier = Modifier.size(18.dp))
                Text("AI Profil Teşhisi:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
            }

            Text(
                text = "Cilt tipi: $detectedType. Önerilen temel hedef: $detectedGoal.",
                fontSize = 12.sp,
                color = Navy700,
                lineHeight = 16.sp
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRetakePhoto,
                    modifier = Modifier
                        .weight(0.8f)
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    border = BorderStroke(1.dp, Purple500),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Purple600, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Yeniden Çek",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple600,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = {
                        onApplyToProfile(detectedType.split(" ").first(), detectedConcerns, detectedGoal)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple600),
                    shape = RoundedCornerShape(23.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Analizi Onayla & Düzenle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
