package com.example.util

data class ParsedRoutine(
    val morningSteps: List<String> = emptyList(),
    val eveningSteps: List<String> = emptyList(),
    val uncategorizedText: String? = null
) {
    val isCategorized: Boolean get() = morningSteps.isNotEmpty() || eveningSteps.isNotEmpty()
}

object RoutineParser {

    private enum class Section {
        NONE, MORNING, EVENING, OTHER
    }

    fun parse(rawRoutineText: String?): ParsedRoutine {
        if (rawRoutineText.isNullOrBlank()) {
            return ParsedRoutine()
        }

        val lines = rawRoutineText.lines()
        var currentSection = Section.NONE

        val morningList = mutableListOf<String>()
        val eveningList = mutableListOf<String>()
        val unclassifiedLines = mutableListOf<String>()

        var foundMorningHeader = false
        var foundEveningHeader = false

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isBlank()) continue

            val cleanLineForHeaderCheck = line
                .replace(Regex("""^[#*\-•\d\.\:\s]+"""), "")
                .lowercase()
                .trim()

            val isMorningHeader = isMorningSectionHeader(line, cleanLineForHeaderCheck)
            val isEveningHeader = isEveningSectionHeader(line, cleanLineForHeaderCheck)
            val isOtherHeader = isOtherSectionHeader(line, cleanLineForHeaderCheck)

            if (isMorningHeader) {
                currentSection = Section.MORNING
                foundMorningHeader = true
                continue
            } else if (isEveningHeader) {
                currentSection = Section.EVENING
                foundEveningHeader = true
                continue
            } else if (isOtherHeader && (foundMorningHeader || foundEveningHeader)) {
                currentSection = Section.OTHER
                continue
            }

            val stepText = cleanStepLine(line)
            if (stepText.isBlank()) continue

            when (currentSection) {
                Section.MORNING -> morningList.add(stepText)
                Section.EVENING -> eveningList.add(stepText)
                Section.NONE -> unclassifiedLines.add(line)
                Section.OTHER -> { /* ignore non-routine sections */ }
            }
        }

        if (!foundMorningHeader && !foundEveningHeader) {
            return ParsedRoutine(
                morningSteps = emptyList(),
                eveningSteps = emptyList(),
                uncategorizedText = rawRoutineText.trim()
            )
        }

        return ParsedRoutine(
            morningSteps = morningList.toList(),
            eveningSteps = eveningList.toList(),
            uncategorizedText = null
        )
    }

    private fun isMorningSectionHeader(line: String, cleanLower: String): Boolean {
        val lower = line.lowercase()
        val containsSabah = lower.contains("sabah") || cleanLower.contains("sabah")
        val containsAksam = lower.contains("akşam") || lower.contains("aksam")
        if (!containsSabah || containsAksam) return false

        if (containsProductOrActionWord(lower)) return false

        return cleanLower == "sabah" ||
               cleanLower.startsWith("sabah rutini") ||
               cleanLower.startsWith("sabah bakımı") ||
               cleanLower.startsWith("sabahları") ||
               cleanLower.startsWith("1. sabah") ||
               cleanLower.startsWith("sabah:") ||
               (cleanLower.length < 25)
    }

    private fun isEveningSectionHeader(line: String, cleanLower: String): Boolean {
        val lower = line.lowercase()
        val containsAksam = lower.contains("akşam") || lower.contains("aksam") || cleanLower.contains("akşam") || cleanLower.contains("aksam")
        val containsSabah = lower.contains("sabah")
        if (!containsAksam || containsSabah) return false

        if (containsProductOrActionWord(lower)) return false

        return cleanLower == "akşam" || cleanLower == "aksam" ||
               cleanLower.startsWith("akşam rutini") || cleanLower.startsWith("aksam rutini") ||
               cleanLower.startsWith("akşam bakımı") || cleanLower.startsWith("aksam bakımı") ||
               cleanLower.startsWith("akşamları") || cleanLower.startsWith("aksamları") ||
               cleanLower.startsWith("2. akşam") || cleanLower.startsWith("2. aksam") ||
               cleanLower.startsWith("akşam:") || cleanLower.startsWith("aksam:") ||
               (cleanLower.length < 25)
    }

    private fun containsProductOrActionWord(lower: String): Boolean {
        val words = listOf(
            "serum", "krem", "temizle", "jel", "tonik", "asit", "maske", "yağ",
            "yıkama", "köpük", "losyon", "spf", "güneş", "kapatıcı", "uygula",
            "sür", "yıka", "damla", "fısfıs", "peeling", "nemlendirici"
        )
        return words.any { lower.contains(it) }
    }

    private fun isOtherSectionHeader(line: String, cleanLower: String): Boolean {
        val lower = line.lowercase()
        return lower.contains("makyaj") || lower.contains("tavsiye") || lower.contains("notlar") ||
               lower.contains("bileşen") || lower.contains("2. bölüm")
    }

    private fun cleanStepLine(line: String): String {
        var text = line.trim()
        text = text.replace(Regex("""^#+\s*"""), "")
        text = text.replace(Regex("""^(?i:\s*adım\s*\d+[\.\:\)]*|\s*step\s*\d+[\.\:\)]*|[\s*•\-▪➢#\d\.\)\:]+)"""), "").trim()
        text = text.replace("**", "").replace("*", "").trim()
        return text
    }
}
