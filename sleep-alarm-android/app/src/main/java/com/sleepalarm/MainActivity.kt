package com.sleepalarm

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.DateFormat
import java.util.Date

class MainActivity : Activity() {

    private lateinit var delayInput: EditText
    private lateinit var toggleButton: Button
    private lateinit var testButton: Button
    private lateinit var statusText: TextView
    private lateinit var warningText: TextView
    private lateinit var fixButton: Button

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        runOnUiThread { render() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        delayInput = findViewById(R.id.delayInput)
        toggleButton = findViewById(R.id.toggleButton)
        testButton = findViewById(R.id.testButton)
        statusText = findViewById(R.id.statusText)
        warningText = findViewById(R.id.warningText)
        fixButton = findViewById(R.id.fixButton)

        delayInput.setText(SleepState.delayMinutes(this).toString())
        toggleButton.setOnClickListener { if (isTracking()) stopTracking() else requestPermissionsThenStart() }
        testButton.setOnClickListener {
            SleepTracker.scheduleAlarm(this, System.currentTimeMillis() + 60_000L)
            Toast.makeText(this, "Test alarm will ring in 1 minute", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        SleepState.prefs(this).registerOnSharedPreferenceChangeListener(prefsListener)
        render()
    }

    override fun onPause() {
        super.onPause()
        SleepState.prefs(this).unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    /** Tracking = listening for sleep, or alarm already scheduled for tonight. */
    private fun isTracking() = SleepState.isArmed(this) || SleepState.alarmAt(this) > 0

    private fun requestPermissionsThenStart() {
        val needed = buildList {
            if (!SleepTracker.hasActivityPermission(this@MainActivity)) add(Manifest.permission.ACTIVITY_RECOGNITION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (needed.isEmpty()) startTracking() else requestPermissions(needed.toTypedArray(), REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_PERMISSIONS) return
        if (SleepTracker.hasActivityPermission(this)) {
            startTracking()
        } else {
            Toast.makeText(this, "Physical activity permission is needed to detect sleep", Toast.LENGTH_LONG).show()
        }
    }

    private fun startTracking() {
        val minutes = delayInput.text.toString().toIntOrNull()
        if (minutes == null || minutes < 1) {
            delayInput.error = "Enter minutes (1 or more)"
            return
        }
        SleepState.setDelayMinutes(this, minutes)
        SleepTracker.start(this)
            .addOnSuccessListener { render() }
            .addOnFailureListener {
                Toast.makeText(this, "Couldn't start sleep detection: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun stopTracking() {
        SleepTracker.stop(this)
        render()
    }

    private fun render() {
        val tracking = isTracking()
        toggleButton.text = if (tracking) "Stop" else "Start — I'm going to sleep"
        delayInput.isEnabled = !tracking
        testButton.isEnabled = !tracking

        val alarmAt = SleepState.alarmAt(this)
        val confidence = SleepState.lastConfidence(this)
        statusText.text = when {
            alarmAt > 0 ->
                "Fell asleep around ${time(SleepState.sleepOnset(this))}.\nAlarm set for ${time(alarmAt)}."
            SleepState.isArmed(this) && confidence >= 0 ->
                "Waiting for you to fall asleep…\nLast reading: $confidence% asleep at ${time(SleepState.lastReadingAt(this))}"
            SleepState.isArmed(this) ->
                "Waiting for the first sleep reading (arrives about every 10 minutes)…"
            else -> "Not tracking."
        }
        renderWarnings()
    }

    /** Surfaces settings that would stop the alarm from waking the user. */
    private fun renderWarnings() {
        val nm = getSystemService(NotificationManager::class.java)
        val am = getSystemService(AlarmManager::class.java)
        val (message, fix) = when {
            !nm.areNotificationsEnabled() ->
                "Notifications are off — the alarm can't ring." to
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms() ->
                "Exact alarms are not allowed — the alarm may be late." to
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName"))
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !nm.canUseFullScreenIntent() ->
                "Full-screen alarms are off — the alarm will only show as a notification." to
                    Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT, Uri.parse("package:$packageName"))
            else -> null to null
        }
        warningText.text = message
        warningText.visibility = if (message != null) View.VISIBLE else View.GONE
        fixButton.visibility = warningText.visibility
        fixButton.setOnClickListener { fix?.let(::startActivity) }
    }

    private fun time(millis: Long) = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(millis))

    private companion object {
        const val REQUEST_PERMISSIONS = 1
    }
}
