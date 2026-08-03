# -*- coding: utf-8 -*-
import codecs

with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r", "utf-8") as f:
    profile_content = f.read()

target = "skinConcerns = selectedConcerns.joinToString(\", \"),"
replacement = "skinConcerns = selectedConcerns.toList(),"

profile_content = profile_content.replace(target, replacement)

with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w", "utf-8") as f:
    f.write(profile_content)
