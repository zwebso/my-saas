# Sleep Alarm (Android)

Rings an alarm a set number of minutes **after you fall asleep** (default: 60).

## How it works

1. Tap **Start — I'm going to sleep** at bedtime.
2. The app subscribes to Google's on-device **Sleep API** (Play services Activity
   Recognition). About every 10 minutes it gets a 0–100 "asleep" confidence based on
   the phone's motion and ambient-light sensors.
3. Two readings in a row at ≥ 75% are treated as "asleep". The first of them marks
   the sleep onset.
4. The app schedules an exact alarm (`AlarmManager.setAlarmClock`) for
   *onset + your delay*, then stops listening.
5. When it fires, a full-screen alarm shows over the lock screen and keeps ringing
   until you tap **Dismiss**.

The thresholds are in `SleepState.kt` (`ASLEEP_CONFIDENCE`, `REQUIRED_READINGS`).

> Google Fit's APIs are deprecated, and neither Fit nor Health Connect delivers
> live "fell asleep" events, so this app uses the Sleep API instead. Detection is
> roughly ±10–20 minutes accurate.

## Install

**Option A — prebuilt APK:** open the repo's **Actions** tab → *Sleep Alarm Android
build* → latest run → download the `sleep-alarm-debug-apk` artifact, unzip it, copy
`app-debug.apk` to your phone and open it (allow "Install unknown apps").

**Option B — Android Studio:** *File → Open* this `sleep-alarm-android` folder, plug
in your phone with USB debugging on, and press **Run**.

Command line (needs the Android SDK): `./gradlew assembleDebug`

## Tips

- Grant **Physical activity** and **Notifications** when asked.
- Set the app's battery usage to **Unrestricted** (Settings → Apps → Sleep Alarm →
  Battery), otherwise sleep readings can be delayed.
- Keep the phone on the bed or nightstand, screen down, with no charger cable
  tugging on it.
- Use **Test alarm in 1 minute** once to check the sound and lock-screen display.
- Rebooting the phone stops tracking for that night. Open the app and tap Start again.

Requires Android 10+ and Google Play services.
