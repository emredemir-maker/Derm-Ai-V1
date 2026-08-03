import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

# Add parameter to HomeScreen
content = content.replace("fun HomeScreen(\n    viewModel: SkinCareViewModel,\n    modifier: Modifier = Modifier\n)", 
                          "fun HomeScreen(\n    viewModel: SkinCareViewModel,\n    onNavigateToMakeupAnalysis: () -> Unit = {},\n    modifier: Modifier = Modifier\n)")

# Add button
target = """                        // 2. Makeup Advisory Card
                        RoutineSectionCard(
                            title = "Cildinize Özel Makyaj Tavsiyeleri",
                            icon = Icons.Default.Brush,
                            iconTint = MaterialTheme.colorScheme.primary,
                            content = activeProfile.lastAnalysisMakeup ?: "Teninize en uygun doğal tonlar ve hafif kapatıcılar tercih edin."
                        )"""

button_code = """                        // 2. Makeup Advisory Card
                        RoutineSectionCard(
                            title = "Cildinize Özel Makyaj Tavsiyeleri",
                            icon = Icons.Default.Brush,
                            iconTint = MaterialTheme.colorScheme.primary,
                            content = activeProfile.lastAnalysisMakeup ?: "Teninize en uygun doğal tonlar ve hafif kapatıcılar tercih edin."
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onNavigateToMakeupAnalysis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC06D5E))
                        ) {
                            Icon(imageVector = Icons.Default.Face, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Yapay Zeka Makyaj Analizi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }"""
content = content.replace(target, button_code)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)
