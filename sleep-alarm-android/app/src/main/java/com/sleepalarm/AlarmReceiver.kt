package com.sleepalarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.RingtoneManager

/** Rings the alarm (looping, full-screen notification) and handles dismissal. */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_FIRE -> ring(context)
            ACTION_DISMISS -> dismiss(context)
        }
    }

    companion object {
        const val ACTION_FIRE = "com.sleepalarm.FIRE"
        const val ACTION_DISMISS = "com.sleepalarm.DISMISS"
        private const val CHANNEL_ID = "wake_alarm"
        private const val NOTIFICATION_ID = 1

        fun ring(context: Context) {
            val nm = context.getSystemService(NotificationManager::class.java)
            ensureChannel(nm)

            val fullScreen = PendingIntent.getActivity(
                context, 0,
                Intent(context, AlarmActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val dismiss = PendingIntent.getBroadcast(
                context, 2,
                Intent(context, AlarmReceiver::class.java).setAction(ACTION_DISMISS),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Wake up!")
                .setContentText("${SleepState.delayMinutes(context)} min since you fell asleep")
                .setCategory(Notification.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreen, true)
                .setContentIntent(fullScreen)
                .setOngoing(true)
                .addAction(Notification.Action.Builder(null as Icon?, "Dismiss", dismiss).build())
                .build()
            // Keep repeating the sound until the user dismisses it.
            notification.flags = notification.flags or Notification.FLAG_INSISTENT
            nm.notify(NOTIFICATION_ID, notification)
        }

        fun dismiss(context: Context) {
            context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
            SleepState.resetNight(context)
        }

        private fun ensureChannel(nm: NotificationManager) {
            if (nm.getNotificationChannel(CHANNEL_ID) != null) return
            val channel = NotificationChannel(CHANNEL_ID, "Wake-up alarm", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 600)
                setBypassDnd(true)
            }
            nm.createNotificationChannel(channel)
        }
    }
}
