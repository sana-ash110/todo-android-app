package com.example.todoapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todoapp.AlarmReceiver

fun scheduleAlarm(context: Context, timeInMillis: Long, taskTitle: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("TASK_TITLE", taskTitle)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        timeInMillis.toInt(), // Use a unique ID for each alarm
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Schedule the alarm
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeInMillis,
        pendingIntent
    )
}