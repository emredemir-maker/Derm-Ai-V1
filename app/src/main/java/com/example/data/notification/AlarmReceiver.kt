package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.app.PendingIntent
import com.example.MainActivity
import com.example.data.database.AppDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("reminder_type") ?: return

        // Reschedule next occurrence of the alarm
        NotificationHelper.updateReminders(context)

        val pendingResult = goAsync()
        GlobalScope.launch {
            try {
                // Fetch the skin profile from Database to personalize notification content
                val db = AppDatabase.getDatabase(context)
                val profile = db.skinDao().getSkinProfileDirect()
                val skinType = profile?.skinType ?: "Normal"

                val (title, message) = when (type) {
                    "morning" -> {
                        val msg = when (skinType) {
                            "Kuru" -> "Günaydın! Cildini nazikçe temizle, ardından yoğun nemlendiricini ve güneş kremini sürmeyi unutma! 💧☀️"
                            "Yağlı" -> "Günaydın! Sebum dengesini korumak için hafif temizleyici ve yağsız nemlendiricini uygula! 🧪☀️"
                            "Karma" -> "Günaydın! T-bölgesi parlamasını kontrol altına almak ve yanakları nemlendirmek için rutinine başla! ⚖️☀️"
                            "Hassas" -> "Günaydın! Cilt bariyerini koruyan yatıştırıcı kremini ve mineral güneş koruyucunu uygula! 🌿☀️"
                            else -> "Günaydın! Güne taze bir ciltle başlamak için sabah bakım rutininizi uygulayın! 🧴☀️"
                        }
                        Pair("Sabah Cilt Bakımı Vakti", msg)
                    }
                    "evening" -> {
                        val msg = when (skinType) {
                            "Kuru" -> "İyi akşamlar! Yoğun nemlendirme ve cilt bariyeri onarımı zamanı. Onarıcı kremini sür! 🌙✨"
                            "Yağlı" -> "İyi akşamlar! Gözenek arındırma zamanı. Salisilik asit (BHA) içeren jelini ve hafif kremini uygula! 🧪🌙"
                            "Karma" -> "İyi akşamlar! Cildini kirden arındır ve dengeli bir gece nemlendiricisiyle neme doyur! ⚖️🌙"
                            "Hassas" -> "İyi akşamlar! Günün yorgunluğunu yatıştırıcı kreminle cildinden arındır. Bariyerini besle! 🌿🌙"
                            else -> "İyi akşamlar! Cildiniz uyurken yenilenir. Gece bakım rutininizi tamamlamayı unutmayın! 🧴🌙"
                        }
                        Pair("Akşam Cilt Bakımı Vakti", msg)
                    }
                    "weekly" -> {
                        Pair(
                            "Haftalık Cilt Analizi Zamanı 📸",
                            "Cildindeki gelişimi ve değişimi takip etmek için yeni bir fotoğraf çekip analizini güncelleyebilirsin!"
                        )
                    }
                    else -> Pair("Cilt Bakım Zamanı", "Kendine bir iyilik yap ve cildine hak ettiği bakımı sağla! 🧴✨")
                }

                // Fire notification
                sendNotification(context, title, message, type)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun sendNotification(context: Context, title: String, message: String, type: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val requestCode = when (type) {
            "morning" -> 201
            "evening" -> 202
            "weekly" -> 203
            else -> 204
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(requestCode, notification)
    }
}
