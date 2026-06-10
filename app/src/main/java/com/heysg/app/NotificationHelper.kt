package com.heysg.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object NotificationHelper {
    const val CHANNEL_ID = "heysg_daily"
    const val NOTIF_ID = 1001
    private const val REQUEST_CODE = 42

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, "Daily Reminders", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Daily task reminders from HeySG" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun scheduleDailyReminder(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = buildPendingIntent(context)
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_MONTH, 1)
        }
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(buildPendingIntent(context))
    }

    private fun buildPendingIntent(context: Context) = PendingIntent.getBroadcast(
        context, REQUEST_CODE, Intent(context, ReminderReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
