package com.example.tbtb.ui.task

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tbtb.data.model.Task
import com.example.tbtb.data.request.UpdateTaskRequest
import com.example.tbtb.ui.task.TaskViewModel
import java.util.Calendar
import android.app.DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(navController: NavController, taskId: String) {
    val viewModel: TaskViewModel = viewModel()

    val taskDetails = viewModel.taskDetails.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    LaunchedEffect(taskId) {
        if (token != null) {
            viewModel.fetchTaskDetails(token, taskId)
        }
    }

    // Display a loading indicator if the data is being fetched
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

    // Handle error
    error?.let {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: $it", color = Color.Red)
        }
        return
    }

    taskDetails?.let { task ->
        var showEditDialog by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Task Details") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    modifier = Modifier.statusBarsPadding(),
                    actions = {
                        IconButton(onClick = {
                            // Show the edit dialog when clicked
                             showEditDialog = true
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Project", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Background color
                        titleContentColor = Color.White
                    ),
                )
            },
            containerColor = Color(0xFFF7F8FA), // Background color
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize()
                        .background(Color(0xFFF7F8FA))
                ) {
                    // Task Info Section (Now inside a Card)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Description: ${task.deskripsi}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Deadline: ${task.deadline}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Status: ${if (task.is_finish) "Completed" else "In Progress"}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Responsible Person: ${task.penanggung_jawab}")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showEditDialog) {
                        EditTaskDialog(
                            task = task,
                            onDismiss = { showEditDialog = false },
                            onUpdate = { updatedTask ->
                                // Handle task update logic here
                                if (token != null) {
                                    // Prepare the UpdateTaskRequest
                                    val updatedRequest = UpdateTaskRequest(
                                        deskripsi = updatedTask.deskripsi,
                                        deadline = updatedTask.deadline,
                                        is_finish = updatedTask.is_finish,
                                        penanggung_jawab = updatedTask.penanggung_jawab

                                    )

                                    // Call the updateTask method in the ViewModel
                                    viewModel.updateTask(
                                        token = token,
                                        taskId = task.id,
                                        request = updatedRequest
                                    ) {
                                        // After successful update, update the task UI and close the dialog
                                        showEditDialog = false
                                        Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                                        viewModel.fetchTaskDetails(token, taskId)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        )
    }
}


@ExperimentalMaterial3Api
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdate: (Task) -> Unit
) {
    var updatedDescription by remember { mutableStateOf(TextFieldValue(task.deskripsi ?: "")) }
    var updatedDeadline by remember { mutableStateOf(task.deadline ?: "") }
    var updatedResponsible by remember { mutableStateOf(TextFieldValue(task.penanggung_jawab ?: "")) }
    var updatedIsFinish by remember { mutableStateOf(task.is_finish) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Task Description
                TextField(
                    value = updatedDescription,
                    onValueChange = { updatedDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Task Deadline with DatePickerDialog
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = updatedDeadline,
                        onValueChange = { updatedDeadline = it },
                        label = { Text("Deadline") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                updatedDeadline = "$year-${month + 1}-${dayOfMonth.toString().padStart(2, '0')}"
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Select Date")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Responsible Person
                TextField(
                    value = updatedResponsible,
                    onValueChange = { updatedResponsible = it },
                    label = { Text("Responsible Person") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Task Completion Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Completed", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = updatedIsFinish,
                        onCheckedChange = { updatedIsFinish = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate and prepare updated task data
                    val updatedTask = task.copy(
                        deskripsi = updatedDescription.text,
                        deadline = updatedDeadline,
                        penanggung_jawab = updatedResponsible.text,
                        is_finish = updatedIsFinish
                    )
                    onUpdate(updatedTask)
                    onDismiss()
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

