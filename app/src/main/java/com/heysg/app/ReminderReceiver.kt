package com.heysg.app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val tasks = TaskStorage(context).load()
        val pending = tasks.count { !it.isDone }
        if (pending == 0) return

        val pi = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val body = if (pending == 1) "1 task waiting for you today. Let's get it done! 💪"
                   else "$pending tasks waiting for you today. You've got this! 💪"

        val notif = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Hey SG! 👋 Good morning!")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        context.getSystemService(android.app.NotificationManager::class.java)
            .notify(NotificationHelper.NOTIF_ID, notif)
    }
}
