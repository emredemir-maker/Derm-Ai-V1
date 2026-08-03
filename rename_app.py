with open("app/src/main/res/values/strings.xml", "r") as f:
    content = f.read()

content = content.replace("<string name=\"app_name\">Skin Care App</string>", "<string name=\"app_name\">Derm-Ai</string>")
content = content.replace("<string name=\"app_name\">My Application</string>", "<string name=\"app_name\">Derm-Ai</string>")
content = content.replace("<string name=\"app_name\">SkinCare</string>", "<string name=\"app_name\">Derm-Ai</string>")
content = content.replace("<string name=\"app_name\">Skin Care</string>", "<string name=\"app_name\">Derm-Ai</string>")

with open("app/src/main/res/values/strings.xml", "w") as f:
    f.write(content)
