package com.example.todoapp.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(day: Int, month: Int, year: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(calendar.time)
}
