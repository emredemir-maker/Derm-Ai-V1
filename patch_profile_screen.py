with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

old_header = """                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        "Yapay Zeka Taraması Uygulandı!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }"""

new_header = """                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Yapay Zeka Taraması Uygulandı!",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    
                                    val confidenceColor = when {
                                        result.confidenceScore >= 80 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        result.confidenceScore >= 50 -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                        else -> androidx.compose.ui.graphics.Color(0xFFF44336)
                                    }
                                    Surface(
                                        color = confidenceColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "%${result.confidenceScore} Güvenilirlik",
                                            color = confidenceColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }"""
content = content.replace(old_header, new_header)

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
