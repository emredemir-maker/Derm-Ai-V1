with open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "r") as f:
    content = f.read()

old_text = """                            Text(
                                text = "Etiketi Taratın",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ürünün arkasındaki 'Ingredients' veya 'İçindekiler' listesini net bir şekilde çekin.\\n\\n💡 İpucu: Yuvarlak şişelerde tüm kelimeler okunmuyorsa, AI eksik harfleri tahmin edecektir. Alternatif olarak 'Metin Girişi' sekmesinden içeriği kendiniz yapıştırabilirsiniz.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))"""

new_text = """                            Text(
                                text = "Etiketi Taratın",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Ürünün 'İçindekiler' (Ingredients) listesini olabildiğince net çekin.\\n\\n📷 Kavisli / Yuvarlak Şişeler:\\nMetnin tamamının görünmesi için kamerayı biraz uzaklaştırıp geniş açı kullanın. Kenarlara doğru eğilen yazıları AI otomatik tamamlamaya çalışacaktır.\\n\\n💡 Alternatif: Çok silikse 'Metin Girişi' sekmesini kullanın.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Start,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))"""

content = content.replace(old_text, new_text)

old_camera = """                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "İçerik Etiketi Tarayıcı",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }"""

new_camera = """                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "İçerik Etiketi Tarayıcı",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Yuvarlak şişeler için kamerayı biraz uzaklaştırın",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }"""

content = content.replace(old_camera, new_camera)

with open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "w") as f:
    f.write(content)
