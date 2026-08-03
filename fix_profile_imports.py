with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import com.example.ui.theme.AloeLight\n", "")
content = content.replace("import com.example.ui.theme.CoralLight\n", "")

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
