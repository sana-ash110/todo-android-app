package com.example.todoapp.ui.screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.ui.components.TaskCard
import com.example.todoapp.util.scheduleAlarm
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoScreen(
    viewModel: TaskViewModel = viewModel(),
    sendTestNotification: () -> Unit = {}
) {
    val context = LocalContext.current
    var taskText by remember { mutableStateOf("") }

    // State for Date Picker (Deadline)
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // State for Time Picker (Reminder)
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Create New Task", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        // Task Name Input
        TextField(
            value = taskText,
            onValueChange = { taskText = it },
            placeholder = { Text("What needs to be done?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Deadline Button
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(selectedDate?.toString() ?: "Set Deadline")
            }

            // Reminder Button
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.weight(1f)
            ) {
                Text(selectedTime?.toString() ?: "Set Reminder")
            }
        }

        // --- Date Picker Dialog ---
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        // --- Time Picker Dialog ---
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState()
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text("OK") }
                },
                title = { Text("Select Reminder Time") },
                text = { TimePicker(state = timePickerState) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ADD TASK BUTTON
        Button(
            onClick = {
                val now = LocalDateTime.now()

                // 1. Title Validation
                if (taskText.isBlank()) {
                    Toast.makeText(context, "Please enter a task name", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // 2. Build Reminder DateTime
                val reminderDateTime = when {
                    selectedTime != null && selectedDate != null ->
                        LocalDateTime.of(selectedDate, selectedTime)

                    selectedTime != null && selectedDate == null ->
                        LocalDateTime.of(LocalDate.now(), selectedTime)

                    else -> null
                }

                // 3. Logic Validation
                selectedDate?.let { deadline ->
                    val deadlineDateTime = deadline.atTime(23, 59)
                    if (reminderDateTime != null) {
                        if (reminderDateTime.isAfter(deadlineDateTime)) {
                            Toast.makeText(context, "Reminder must be before deadline!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }
                }
                // 4. Formatting deadline for ViewModel
                val deadlineString = selectedDate?.let { "${it.dayOfMonth}/${it.monthValue}/${it.year}" }

                // 5. ADD TASK
                val newTask = viewModel.addTask(taskText, deadlineString, reminderDateTime)

                // 6. SCHEDULE REMINDER ALARM
                if (newTask != null && reminderDateTime != null) {
                    val millis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    scheduleAlarm(context, millis, newTask.title)
                }

                // Clear UI
                taskText = ""
                selectedDate = null
                selectedTime = null
                Toast.makeText(context, "Task Added!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()

        // ACTIVE TASKS LIST
        Text("Active Tasks", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.getActiveTasks(), key = { it.id }) { task ->
                Box(modifier = Modifier.animateContentSize(animationSpec = tween(500))) {
                    TaskCard(
                        task = task,
                        onCheckedChange = { viewModel.toggleComplete(task) },
                        onDelete = { viewModel.removeTask(task) }
                    )
                }
            }
        }

        // COMPLETED TASKS LIST
        Text("Completed Tasks", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.getCompletedTasks(), key = { it.id }) { task ->
                Box(modifier = Modifier.animateContentSize(animationSpec = tween(500))) {
                    TaskCard(
                        task = task,
                        onCheckedChange = { viewModel.toggleComplete(task) },
                        onDelete = { viewModel.removeTask(task) }
                    )
                }
            }
        }
    }
}