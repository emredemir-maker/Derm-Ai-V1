# -*- coding: utf-8 -*-
import codecs

with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r", "utf-8") as f:
    profile_content = f.read()
    
target = """                            viewModel.saveSkinProfile(
                                skinType = selectedSkinType,
                                concerns = selectedConcerns.joinToString(", "),
                                goal = selectedGoal,
                                makeup = "Doğal & Hafif (Yok Gibi Makyaj)"
                            )"""

replacement = """                            viewModel.saveSkinProfile(
                                skinType = selectedSkinType,
                                skinConcerns = selectedConcerns.joinToString(", "),
                                skincareGoal = selectedGoal,
                                makeupPreference = "Doğal & Hafif (Yok Gibi Makyaj)"
                            )"""

profile_content = profile_content.replace(target, replacement)

with codecs.open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w", "utf-8") as f:
    f.write(profile_content)
