package com.sleepalarm

import android.content.Context
import android.content.SharedPreferences

/** Persisted state shared by the activity and the background receivers. */
object SleepState {
    const val DEFAULT_DELAY_MINUTES = 60

    /** Sleep API confidence (0-100) at or above which a reading counts as "asleep". */
    const val ASLEEP_CONFIDENCE = 75

    /** Consecutive "asleep" readings (~10 min apart) needed before we trust it. */
    const val REQUIRED_READINGS = 2

    private const val KEY_ARMED = "armed"
    private const val KEY_DELAY = "delayMinutes"
    private const val KEY_STREAK = "streak"
    private const val KEY_STREAK_START = "streakStart"
    private const val KEY_ONSET = "sleepOnset"
    private const val KEY_ALARM_AT = "alarmAt"
    private const val KEY_LAST_CONFIDENCE = "lastConfidence"
    private const val KEY_LAST_READING_AT = "lastReadingAt"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences("sleep_state", Context.MODE_PRIVATE)

    fun isArmed(c: Context) = prefs(c).getBoolean(KEY_ARMED, false)
    fun delayMinutes(c: Context) = prefs(c).getInt(KEY_DELAY, DEFAULT_DELAY_MINUTES)
    fun streak(c: Context) = prefs(c).getInt(KEY_STREAK, 0)
    fun streakStart(c: Context) = prefs(c).getLong(KEY_STREAK_START, 0)
    fun sleepOnset(c: Context) = prefs(c).getLong(KEY_ONSET, 0)
    fun alarmAt(c: Context) = prefs(c).getLong(KEY_ALARM_AT, 0)
    fun lastConfidence(c: Context) = prefs(c).getInt(KEY_LAST_CONFIDENCE, -1)
    fun lastReadingAt(c: Context) = prefs(c).getLong(KEY_LAST_READING_AT, 0)

    fun setDelayMinutes(c: Context, minutes: Int) =
        prefs(c).edit().putInt(KEY_DELAY, minutes).apply()

    fun setArmed(c: Context, armed: Boolean) =
        prefs(c).edit().putBoolean(KEY_ARMED, armed).apply()

    fun recordReading(c: Context, confidence: Int, at: Long, streak: Int, streakStart: Long) =
        prefs(c).edit()
            .putInt(KEY_LAST_CONFIDENCE, confidence)
            .putLong(KEY_LAST_READING_AT, at)
            .putInt(KEY_STREAK, streak)
            .putLong(KEY_STREAK_START, streakStart)
            .apply()

    fun recordAlarm(c: Context, onset: Long, alarmAt: Long) =
        prefs(c).edit().putLong(KEY_ONSET, onset).putLong(KEY_ALARM_AT, alarmAt).apply()

    /** Clears everything about the current night, keeping the user's delay setting. */
    fun resetNight(c: Context) =
        prefs(c).edit()
            .remove(KEY_ARMED).remove(KEY_STREAK).remove(KEY_STREAK_START)
            .remove(KEY_ONSET).remove(KEY_ALARM_AT)
            .remove(KEY_LAST_CONFIDENCE).remove(KEY_LAST_READING_AT)
            .apply()
}
