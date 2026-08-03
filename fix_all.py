# -*- coding: utf-8 -*-
import codecs

# Fix ChatScreen
with codecs.open("app/src/main/java/com/example/ui/screens/ChatScreen.kt", "r", "utf-8") as f:
    chat_content = f.read()
chat_content = chat_content.replace("val isTyping by viewModel.isChatTyping.collectAsState()", "val isTyping by viewModel.isChatLoading.collectAsState()")
chat_content = chat_content.replace("text = msg.content,", "text = msg.text,")
with codecs.open("app/src/main/java/com/example/ui/screens/ChatScreen.kt", "w", "utf-8") as f:
    f.write(chat_content)

# Fix DiaryScreen
with codecs.open("app/src/main/java/com/example/ui/screens/DiaryScreen.kt", "r", "utf-8") as f:
    diary_content = f.read()
diary_imports = "import androidx.compose.ui.graphics.Brush\nimport androidx.compose.ui.text.style.TextAlign\n"
diary_content = diary_content.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\n" + diary_imports)
with codecs.open("app/src/main/java/com/example/ui/screens/DiaryScreen.kt", "w", "utf-8") as f:
    f.write(diary_content)

# Fix HomeScreen
with codecs.open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r", "utf-8") as f:
    home_content = f.read()
home_content = home_content.replace("val score = activeProfile?.healthScore ?: 80", "val score = 80")
with codecs.open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w", "utf-8") as f:
    f.write(home_content)

# Fix IngredientScanScreen
with codecs.open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "r", "utf-8") as f:
    scan_content = f.read()
scan_content = scan_content.replace("val analysisResult by viewModel.ingredientAnalysisResult.collectAsState()", "val analysisResult by viewModel.ingredientAnalysis.collectAsState()")
scan_content = scan_content.replace("viewModel.analyzeProductScan(file.absolutePath)", "viewModel.analyzeProductIngredients(file.absolutePath, null)")
scan_content = scan_content.replace("viewModel.clearScanAnalysis()", "viewModel.clearIngredientAnalysis()")

scan_text_target = '''Text("Ürünün 'İçindekiler' listesini olabildiğince net çekin.\n\nKavisli şişelerde geniş açı kullanın. Kenarlara doğru eğilen yazıları AI tamamlar.", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)'''
scan_text_replacement = '''Text("Ürünün 'İçindekiler' listesini olabildiğince net çekin.\\n\\nKavisli şişelerde geniş açı kullanın. Kenarlara doğru eğilen yazıları AI tamamlar.", fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center)'''
scan_content = scan_content.replace(scan_text_target, scan_text_replacement)

scan_imports = "import androidx.compose.ui.text.style.TextAlign\n"
scan_content = scan_content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\n" + scan_imports)

with codecs.open("app/src/main/java/com/example/ui/screens/IngredientScanScreen.kt", "w", "utf-8") as f:
    f.write(scan_content)

# Fix ProfileSetupScreen
with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r", "utf-8") as f:
    profile_content = f.read()
profile_content = profile_content.replace("viewModel.saveProfile(", "viewModel.saveSkinProfile(")
with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w", "utf-8") as f:
    f.write(profile_content)

