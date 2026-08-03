with open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "r") as f:
    content = f.read()

old_product_name = """                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = result.productName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )"""

new_product_name = """                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val confidenceColor = when {
                        result.confidenceScore >= 80 -> Color(0xFF4CAF50)
                        result.confidenceScore >= 50 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                    Surface(
                        color = confidenceColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "%${result.confidenceScore} AI Analiz Güvenilirliği",
                            color = confidenceColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = result.productName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )"""

content = content.replace(old_product_name, new_product_name)

with open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "w") as f:
    f.write(content)
