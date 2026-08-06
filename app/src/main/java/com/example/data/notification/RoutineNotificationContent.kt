package com.example.data.notification

import com.example.data.database.SkinProfile
import com.example.util.RoutineParser

data class RoutineNotificationContent(
    val title: String,
    val message: String,
    val destination: String
)

object RoutineNotificationContentBuilder {
    const val DESTINATION_MORNING = "routine_morning"
    const val DESTINATION_EVENING = "routine_evening"
    const val DESTINATION_WEEKLY = "weekly_analysis"

    fun build(profile: SkinProfile?, type: String): RoutineNotificationContent {
        val parsed = RoutineParser.parse(profile?.lastAnalysisRoutine)
        val namePrefix = profile?.userName?.trim()?.takeIf { it.isNotEmpty() }?.let { "$it, " }.orEmpty()

        return when (type) {
            "morning" -> RoutineNotificationContent(
                title = "Sabah bakımın hazır",
                message = buildRoutineMessage(namePrefix, parsed.morningSteps, "sabah bakım adımlarını görmek için dokun."),
                destination = DESTINATION_MORNING
            )
            "evening" -> RoutineNotificationContent(
                title = "Akşam bakımın hazır",
                message = buildRoutineMessage(namePrefix, parsed.eveningSteps, "akşam bakım adımlarını görmek için dokun."),
                destination = DESTINATION_EVENING
            )
            "weekly" -> RoutineNotificationContent(
                title = "Haftalık cilt kontrolü",
                message = "${namePrefix}cildindeki değişimi kaydetmek ve yüz analizini güncellemek için dokun.",
                destination = DESTINATION_WEEKLY
            )
            else -> RoutineNotificationContent(
                title = "Cilt bakım zamanı",
                message = "${namePrefix}kişisel bakım planını açmak için dokun.",
                destination = DESTINATION_MORNING
            )
        }
    }

    private fun buildRoutineMessage(prefix: String, steps: List<String>, emptyMessage: String): String {
        if (steps.isEmpty()) return prefix + emptyMessage
        val preview = steps.take(2).joinToString(" • ") { it.replace('\n', ' ').trim() }
        return "$prefix$preview"
    }
}
