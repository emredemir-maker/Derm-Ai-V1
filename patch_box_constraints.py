import re

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "r") as f:
    content = f.read()

target = """                                    lastScannedPhotoPath?.let { photoPath ->
                                        Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp))) {"""

replacement = """                                    lastScannedPhotoPath?.let { photoPath ->
                                        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(12.dp))) {
                                            val boxWidth = maxWidth
                                            val boxHeight = maxHeight"""

content = content.replace(target, replacement)

target2 = """                                                            .offset(
                                                                x = (250.dp * xPercent) - 12.dp, // Assuming ~250dp width approx for the Box
                                                                y = (250.dp * yPercent) - 12.dp
                                                            )"""

replacement2 = """                                                            .offset(
                                                                x = (boxWidth * xPercent) - 12.dp,
                                                                y = (boxHeight * yPercent) - 12.dp
                                                            )"""

content = content.replace(target2, replacement2)

with open("app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt", "w") as f:
    f.write(content)
