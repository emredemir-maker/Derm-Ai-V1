with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "r") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "val makeup =" in line and "profile.makeupPreference" in line:
        lines.insert(i + 1, '                val allergies = if (profile != null && profile.skinType == skinType) profile.allergies else "Yok"\n')
        break

for i, line in enumerate(lines):
    if "goal = goal," in line:
        lines[i] = "                    goal = goal,\n"
    if "makeup = makeup" in line and "GeminiRepository.fetchCustomRecommendations" in "".join(lines[i-6:i]):
        lines[i] = "                    makeup = makeup,\n                    allergies = allergies\n"

with open("app/src/main/java/com/example/ui/viewmodel/SkinCareViewModel.kt", "w") as f:
    f.writelines(lines)
