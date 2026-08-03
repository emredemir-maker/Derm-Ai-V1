import re

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

# Add a DetailedAnalysisCard below the basic scan result card.
new_card_code = """
                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))
                                
                                if (result.eyeAreaAnalysis.isNotEmpty() && result.eyeAreaAnalysis != "null") {
                                    Text(
                                        text = "Göz Çevresi: ${result.eyeAreaAnalysis}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                if (result.makeupEvaluation.isNotEmpty() && result.makeupEvaluation != "null") {
                                    Text(
                                        text = "Makyaj Analizi: ${result.makeupEvaluation}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                                
                                if (result.faceMapRegions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Yüz Haritası (Saptanan Sorunlar):",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    result.faceMapRegions.forEach { region ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Column {
                                                Text(
                                                    text = "${region.regionName}: ${region.issue}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                                Text(
                                                    text = "Öneri: ${region.recommendedIngredient}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
"""

target = """                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))"""

# Insert the new code right after the target. (Replacing target with target + new_code)
# But wait, we also have the Row with "Seçimleri aşağıdan değiştirebilirsiniz." below it.
# Let's replace target with new_card_code because new_card_code includes target at the top.

if target in content:
    content = content.replace(target, new_card_code)
else:
    print("Could not find the target to insert detailed analysis.")

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
