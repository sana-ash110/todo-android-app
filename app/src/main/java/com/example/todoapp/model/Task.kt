package com.example.todoapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime

data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean = false,
    val deadline: LocalDate? = null,
    val reminderDateTime: LocalDateTime? = null
) {
    // Helper to check if task is late
    val isLate: Boolean
        @RequiresApi(Build.VERSION_CODES.O)
        get() = !isDone && deadline != null && deadline.isBefore(LocalDate.now())
}