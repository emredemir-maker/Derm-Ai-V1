# -*- coding: utf-8 -*-
import codecs

with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r", "utf-8") as f:
    profile_content = f.read()
    
profile_content = profile_content.replace("import androidx.compose.foundation.clickable", "import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.border")

with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w", "utf-8") as f:
    f.write(profile_content)
