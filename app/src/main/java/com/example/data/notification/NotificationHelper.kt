package com.example.data.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.util.Calendar

object NotificationHelper {
    const val CHANNEL_ID_REMINDERS = "cilt_bakim_hatirlaticilari"
    const val CHANNEL_ID_TIPS = "cilt_bakim_tavsiyeleri"

    const val PREFS_NAME = "cilt_bakimi_hatirlatici_prefs"
    const val KEY_MORNING_ENABLED = "morning_enabled"
    const val KEY_MORNING_HOUR = "morning_hour"
    const val KEY_MORNING_MINUTE = "morning_minute"

    const val KEY_EVENING_ENABLED = "evening_enabled"
    const val KEY_EVENING_HOUR = "evening_hour"
    const val KEY_EVENING_MINUTE = "evening_minute"

    const val KEY_WEEKLY_ENABLED = "weekly_enabled"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Skincare Reminders Channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Cilt Bakım Hatırlatıcıları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Sabah ve akşam cilt bakım rutinlerinizi hatırlatır."
                enableVibration(true)
            }

            // AI Tips and Insights Channel
            val tipsChannel = NotificationChannel(
                CHANNEL_ID_TIPS,
                "Cilt Analiz ve Öneriler",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Yapay zeka analizleri ve kişiselleştirilmiş cilt bakım önerileri."
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(tipsChannel)
        }
    }

    fun sendImmediateTestNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Using safe system icon, custom adaptive icon will show in launcher
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun updateReminders(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 1. Morning Reminder
        val morningEnabled = prefs.getBoolean(KEY_MORNING_ENABLED, true)
        val morningHour = prefs.getInt(KEY_MORNING_HOUR, 8)
        val morningMinute = prefs.getInt(KEY_MORNING_MINUTE, 0)
        scheduleAlarm(context, "morning", morningHour, morningMinute, morningEnabled)

        // 2. Evening Reminder
        val eveningEnabled = prefs.getBoolean(KEY_EVENING_ENABLED, true)
        val eveningHour = prefs.getInt(KEY_EVENING_HOUR, 21)
        val eveningMinute = prefs.getInt(KEY_EVENING_MINUTE, 0)
        scheduleAlarm(context, "evening", eveningHour, eveningMinute, eveningEnabled)

        // 3. Weekly Reminder (Sunday 10:00 AM)
        val weeklyEnabled = prefs.getBoolean(KEY_WEEKLY_ENABLED, true)
        scheduleWeeklyAlarm(context, weeklyEnabled)
    }

    private fun scheduleAlarm(context: Context, type: String, hour: Int, minute: Int, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.ciltanalizvebakim.NOTIFICATION_TRIGGER"
            putExtra("reminder_type", type)
        }

        val requestCode = if (type == "morning") 101 else 102
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // If time has passed, schedule for tomorrow
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for newer Android versions restricting exact alarms
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun scheduleWeeklyAlarm(context: Context, enabled: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.ciltanalizvebakim.NOTIFICATION_TRIGGER"
            putExtra("reminder_type", "weekly")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            103,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.WEEK_OF_YEAR, 1) // Schedule for next Sunday
            }
        }

        try {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
