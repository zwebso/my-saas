package com.sleepalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.SleepClassifyEvent

/**
 * Receives Sleep API classification events. Once enough consecutive readings say the
 * user is asleep, it schedules the alarm for (sleep onset + delay) and stops listening.
 */
class SleepReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!SleepState.isArmed(context) || SleepState.alarmAt(context) > 0) return
        if (!SleepClassifyEvent.hasEvents(intent)) return

        var streak = SleepState.streak(context)
        var streakStart = SleepState.streakStart(context)

        for (event in SleepClassifyEvent.extractEvents(intent).sortedBy { it.timestampMillis }) {
            if (event.confidence >= SleepState.ASLEEP_CONFIDENCE) {
                if (streak == 0) streakStart = event.timestampMillis
                streak++
            } else {
                streak = 0
                streakStart = 0
            }
            SleepState.recordReading(context, event.confidence, event.timestampMillis, streak, streakStart)

            if (streak >= SleepState.REQUIRED_READINGS) {
                onFellAsleep(context, onset = streakStart)
                return
            }
        }
    }

    private fun onFellAsleep(context: Context, onset: Long) {
        val delayMillis = SleepState.delayMinutes(context) * 60_000L
        // Never schedule in the past (e.g. events delivered late after Doze).
        val triggerAt = maxOf(onset + delayMillis, System.currentTimeMillis() + 60_000L)
        SleepTracker.scheduleAlarm(context, triggerAt)
        SleepState.recordAlarm(context, onset, triggerAt)
        SleepTracker.stopListening(context)
    }
}
