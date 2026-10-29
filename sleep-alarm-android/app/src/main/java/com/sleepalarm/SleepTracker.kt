package com.sleepalarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.google.android.gms.tasks.Task

/** Subscribes to the Sleep API and schedules / cancels the wake-up alarm. */
object SleepTracker {

    fun hasActivityPermission(context: Context) =
        context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) ==
            PackageManager.PERMISSION_GRANTED

    /** Starts receiving sleep classification events (~every 10 minutes) in [SleepReceiver]. */
    @SuppressLint("MissingPermission") // callers check hasActivityPermission() first
    fun start(context: Context): Task<Void> {
        SleepState.resetNight(context)
        return ActivityRecognition.getClient(context)
            .requestSleepSegmentUpdates(
                sleepPendingIntent(context),
                SleepSegmentRequest(SleepSegmentRequest.CLASSIFY_EVENTS_ONLY)
            )
            .addOnSuccessListener { SleepState.setArmed(context, true) }
    }

    /** Stops listening for sleep events. Leaves any already-scheduled alarm in place. */
    @SuppressLint("MissingPermission")
    fun stopListening(context: Context) {
        if (hasActivityPermission(context)) {
            ActivityRecognition.getClient(context)
                .removeSleepSegmentUpdates(sleepPendingIntent(context))
        }
        SleepState.setArmed(context, false)
    }

    /** Stops listening and cancels the alarm, ending the night. */
    fun stop(context: Context) {
        stopListening(context)
        cancelAlarm(context)
        SleepState.resetNight(context)
    }

    fun scheduleAlarm(context: Context, triggerAt: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val fire = alarmPendingIntent(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            val showApp = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            // setAlarmClock is exact, survives Doze, and shows the alarm icon in the status bar.
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAt, showApp), fire)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, fire)
        }
    }

    fun cancelAlarm(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(alarmPendingIntent(context))
    }

    private fun sleepPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context, 0, Intent(context, SleepReceiver::class.java),
            // Must be mutable: Play services adds the sleep events as extras.
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    private fun alarmPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context, 1,
            Intent(context, AlarmReceiver::class.java).setAction(AlarmReceiver.ACTION_FIRE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}
