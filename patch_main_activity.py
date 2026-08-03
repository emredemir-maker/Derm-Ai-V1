import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# Add import for MakeupAnalysisScreen
content = content.replace("import com.example.ui.screens.IngredientScanScreen", 
                          "import com.example.ui.screens.IngredientScanScreen\nimport com.example.ui.screens.MakeupAnalysisScreen")

# Add showMakeupAnalysis state
state_code = """
                val skinProfile by viewModel.skinProfile.collectAsState()
                var currentTab by remember { mutableStateOf(0) }
                var showMakeupAnalysis by remember { mutableStateOf(false) }
"""
content = content.replace("val skinProfile by viewModel.skinProfile.collectAsState()\n                var currentTab by remember { mutableStateOf(0) }", state_code.strip())

# hide bottom bar if showMakeupAnalysis is true
bottom_bar_code = """                    bottomBar = {
                        // Only show bottom navigation if the user has completed their profile setup
                        if (skinProfile != null && !showMakeupAnalysis) {"""
content = content.replace("bottomBar = {\n                        // Only show bottom navigation if the user has completed their profile setup\n                        if (skinProfile != null) {", bottom_bar_code)

# Handle rendering MakeupAnalysisScreen
render_code = """                    if (skinProfile == null) {
                        // Guide users through onboarding first
                        ProfileSetupScreen(
                            viewModel = viewModel,
                            onCompleted = {
                                currentTab = 0
                            },
                            modifier = modifier
                        )
                    } else if (showMakeupAnalysis) {
                        MakeupAnalysisScreen(
                            viewModel = viewModel,
                            onNavigateBack = { showMakeupAnalysis = false },
                            modifier = modifier
                        )
                    } else {"""
content = content.replace("""                    if (skinProfile == null) {
                        // Guide users through onboarding first
                        ProfileSetupScreen(
                            viewModel = viewModel,
                            onCompleted = {
                                currentTab = 0
                            },
                            modifier = modifier
                        )
                    } else {""", render_code)

# Pass onNavigateToMakeupAnalysis to HomeScreen
content = content.replace("0 -> HomeScreen(\n                                viewModel = viewModel,\n                                modifier = modifier\n                            )", 
                          "0 -> HomeScreen(\n                                viewModel = viewModel,\n                                onNavigateToMakeupAnalysis = { showMakeupAnalysis = true },\n                                modifier = modifier\n                            )")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
