package com.example.todoapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.todoapp.model.Task
import java.time.LocalDate
import java.time.LocalDateTime

class TaskViewModel : ViewModel() {

    private val tasks = mutableStateListOf<Task>()
    private var nextId = 1

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTask(title: String, deadlineStr: String?, reminderDateTime: LocalDateTime?): Task? {
        val parsedDeadline = deadlineStr?.let {
            try {
                val parts = it.split("/")
                // Ensure we have exactly 3 parts: day, month, year
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt()
                    val year = parts[2].toInt()
                    LocalDate.of(year, month, day)
                } else null
            } catch (e: Exception) {
                null
            }
        }

        // IMPORTANT: If validation fails, we should still add the task
        // but maybe without the reminder, OR return null but show a Toast.
        // Let's keep your logic but make sure the data matches.
        if (parsedDeadline != null && reminderDateTime != null) {
            if (reminderDateTime.isAfter(parsedDeadline.atTime(23, 59))) {
                return null
            }
        }

        val task = Task(
            id = nextId++,
            title = title,
            isDone = false,
            deadline = parsedDeadline,
            reminderDateTime = reminderDateTime
        )
        tasks.add(task)
        return task
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
    }

    fun toggleComplete(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = tasks[index].copy(isDone = !tasks[index].isDone)
        }
    }

    fun getActiveTasks(): List<Task> = tasks.filter { !it.isDone }
    fun getCompletedTasks(): List<Task> = tasks.filter { it.isDone }
}