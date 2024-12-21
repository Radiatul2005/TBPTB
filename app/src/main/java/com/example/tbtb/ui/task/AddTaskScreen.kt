package com.example.tbtb.ui.task

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tbtb.data.request.CreateTaskRequest
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, projectId: String) {
    val viewModel: TaskViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()

    // Initialize mutable state for task fields
    var taskDescription by remember { mutableStateOf("") }
    var taskDeadline by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)
    val userId = sharedPreferences.getString("user_id", null)

    // Error state for each field
    var taskDescriptionError by remember { mutableStateOf<String?>(null) }
    var taskDeadlineError by remember { mutableStateOf<String?>(null) }

    // Show loading indicator while adding the task
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA)),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
        return
    }

    // Show error message if an error occurs
    error?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        return
    }

    // Add Task Form with Custom Background Color
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA)), // Set the background to #F7F8FA
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Add Task", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary, // Background color
                titleContentColor = Color.White
            ),
            modifier = Modifier.statusBarsPadding()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Task Description (Deskripsi) Field
            TextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                label = { Text("Task Description") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = taskDescriptionError != null
            )
            // Task Description Error Text
            taskDescriptionError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Task Deadline Field (Date Picker)
            TextField(
                value = taskDeadline,
                onValueChange = { taskDeadline = it },
                label = { Text("Deadline") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = taskDeadlineError != null,
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        val calendar = Calendar.getInstance()
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                taskDeadline = "$year-${month + 1}-${dayOfMonth.toString().padStart(2, '0')}"
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.show()
                    }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )
            // Task Deadline Error Text
            taskDeadlineError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    var isValid = true

                    // Reset error messages before validation
                    taskDescriptionError = null
                    taskDeadlineError = null

                    // Validate each field
                    if (taskDescription.isEmpty()) {
                        taskDescriptionError = "Task Description is required."
                        isValid = false
                    } else if (taskDescription.length < 10) {
                        taskDescriptionError = "Task Description must be at least 10 characters."
                        isValid = false
                    }

                    if (taskDeadline.isEmpty()) {
                        taskDeadlineError = "Deadline is required."
                        isValid = false
                    }

                    // If valid, proceed to add task
                    if (isValid && token != null && userId != null) {
                        // Create a task request object with collaborators list
                        val request = CreateTaskRequest(
                            deskripsi = taskDescription,
                            deadline = taskDeadline,
                            penanggung_jawab = userId,
                            project_id = projectId
                        )

                        viewModel.createTask(token, request) { success ->
                            if (success){
                                navController.popBackStack()
                                Toast.makeText(context, "Task added successfully!", Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(context, "Failed to add task.", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Task")
            }
        }
    }
}
