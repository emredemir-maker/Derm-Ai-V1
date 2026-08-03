import re

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

# Add lastScannedPhotoPath state collection
content = content.replace("val scanResult by viewModel.scanProfileAnalysis.collectAsState()",
"""val scanResult by viewModel.scanProfileAnalysis.collectAsState()
    val lastScannedPhotoPath by viewModel.lastScannedPhotoPath.collectAsState()""")

# Add Coil image loading with face map overlays
face_map_code = """
                                if (result.faceMapRegions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Yüz Haritası (Saptanan Sorunlar):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    
                                    lastScannedPhotoPath?.let { photoPath ->
                                        Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp))) {
                                            coil.compose.AsyncImage(
                                                model = photoPath,
                                                contentDescription = "Analiz Edilen Yüz",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            // Overlays
                                            result.faceMapRegions.forEach { region ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp) // Safety padding
                                                ) {
                                                    val xPercent = region.x.coerceIn(0f, 1f)
                                                    val yPercent = region.y.coerceIn(0f, 1f)
                                                    
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopStart)
                                                            .offset(
                                                                x = (250.dp * xPercent) - 12.dp, // Assuming ~250dp width approx for the Box
                                                                y = (250.dp * yPercent) - 12.dp
                                                            )
                                                            .size(24.dp)
                                                            .background(MaterialTheme.colorScheme.error, CircleShape)
                                                            .border(2.dp, Color.White, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "!",
                                                            color = Color.White,
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    result.faceMapRegions.forEach { region ->
"""
# Find where faceMapRegions is iterated and replace it to add the image preview
target = """                                if (result.faceMapRegions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Yüz Haritası (Saptanan Sorunlar):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    result.faceMapRegions.forEach { region ->"""

if target in content:
    content = content.replace(target, face_map_code)
else:
    print("Target 2 not found!")

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
