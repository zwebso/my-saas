package com.sleepalarm

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

/** Full-screen "Wake up" screen shown over the lock screen while the alarm rings. */
class AlarmActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        findViewById<TextView>(R.id.alarmDetail).text =
            "You've been asleep about ${SleepState.delayMinutes(this)} minutes."
        findViewById<Button>(R.id.dismissButton).setOnClickListener {
            AlarmReceiver.dismiss(this)
            finish()
        }
    }
}
