package com.example.tbtb.ui.project

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbtb.data.model.Collaborator
import com.example.tbtb.data.model.Task
import com.example.tbtb.data.request.AddCollaboratorsRequest
import com.example.tbtb.data.request.EditProjectRequest
import com.example.tbtb.data.request.ProjectRequest
import com.example.tbtb.ui.project.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(navController: NavController, projectId: String) {
    val viewModel: ProjectViewModel = viewModel()

    val projectDetails = viewModel.projectDetails.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val addCollaboratorsResult = viewModel.addCollaboratorsResult.collectAsState().value
    val error = viewModel.error.collectAsState().value

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    var showAddCollaboratorsDialog by remember { mutableStateOf(false) }


    LaunchedEffect(projectId) {
        if (token != null) {
            viewModel.fetchProjectDetails(token, projectId)
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

    projectDetails?.let { project ->
        // State for managing the edit dialog
        var showEditDialog by remember { mutableStateOf(false) }
        val projectName by remember { mutableStateOf(TextFieldValue(project.data?.namaProject ?: "")) }
        val projectDescription by remember { mutableStateOf(TextFieldValue(project.data?.deskripsi ?: "")) }
        val projectObject by remember { mutableStateOf(TextFieldValue(project.data?.objectType ?: "")) }
        val isFinished by remember { mutableStateOf(project.data?.isFinish ?: false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Project Details") },
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
                    // Project Info Section (Now inside a Card)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Project Name: ${project.data?.namaProject}", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Description: ${project.data?.deskripsi}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Object: ${project.data?.objectType}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Status: ${if (project.data?.isFinish == true) "Finished" else "Ongoing"}")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Collaborators Section
                    Text(text = "Collaborators", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (project.data?.collaborators.isNullOrEmpty()) {
                        Text(text = "No collaborators available.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyRow(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(project.data?.collaborators ?: emptyList()) { collaborator ->
                                CollaboratorItem(collaborator)

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Task List Section
                    Text(text = "Tasks", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Displaying task list
                    if (project.data?.tasks?.isNotEmpty() == true) {
                        LazyRow(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(project.data.tasks) { task ->
                                TaskItem(
                                    task = task,
                                    onClick = { selectedTask ->
                                        // Handle task click and navigate to task detail screen
                                        val taskId = selectedTask.id
                                        navController.navigate("taskDetail/${taskId}")
                                    },
                                    onDelete = { taskId ->
                                        // Handle task deletion
                                        token?.let {
                                            viewModel.deleteTask(it, taskId) { success ->
                                                if (success) {
                                                    Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                                                    viewModel.fetchProjectDetails(token, projectId)
                                                } else {
                                                    Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT).show()
                                                    viewModel.fetchProjectDetails(token, projectId)
                                                }
                                            }
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    } else {
                        Text(text = "No tasks available.", style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons (Add Collaborator, Add Task)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                showAddCollaboratorsDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Collaborator")
                        }
                        if (showAddCollaboratorsDialog) {
                            AddCollaboratorsDialog(
                                projectId = projectId,
                                onDismiss = { showAddCollaboratorsDialog = false },
                                onSave = { request ->
                                    if (token != null) {
                                        viewModel.addCollaborators(
                                            token = token,
                                            request = request,
                                            onComplete = { success ->
                                                if (success) {
                                                    Toast.makeText(context, viewModel.addCollaboratorsResult.value ?: "Collaborator added successfully", Toast.LENGTH_LONG).show()
                                                    viewModel.fetchProjectDetails(token, projectId)
                                                } else {
                                                    Toast.makeText(context, viewModel.addCollaboratorsResult.value ?: "Failed to add collaborator", Toast.LENGTH_SHORT).show()
                                                    viewModel.fetchProjectDetails(token, projectId)
                                                }
                                            }
                                        )
                                    } else {
                                        Toast.makeText(context, "Invalid token", Toast.LENGTH_SHORT).show()
                                    }

                                    showAddCollaboratorsDialog = false
                                }
                            )
                        }

                        Button(
                            onClick = {
                                navController.navigate("addTask/$projectId")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Add Task")
                        }

                    }
                    Button(
                        onClick = {
                            navController.navigate("addProposal/$projectId")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Proposal")
                    }
                }
            }
        )

        // Show the edit dialog when needed
        if (showEditDialog) {
            UpdateProjectDialog(
                projectName = projectName,
                projectDescription = projectDescription,
                projectObject = projectObject,
                isFinish = isFinished,
                onDismiss = { showEditDialog = false },
                onUpdate = { updatedName, updatedDescription, updatedObject, updatedIsFinished ->
                    // Handle project update logic here
                    val updatedProjectRequest = EditProjectRequest(
                        updatedName,
                        updatedDescription,
                        updatedObject,
                        updatedIsFinished
                    )

                    Log.d("ProjectId", projectId)
                    if (projectId.isNotEmpty()) {
                        viewModel.updateProject(token!!, projectId, updatedProjectRequest)
                        Toast.makeText(context, "Project updated successfully", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                        viewModel.fetchProjectDetails(token, projectId)
                    } else {
                        Log.d("Update", "Project Id is null")
                    }

                    showEditDialog = false
                }
            )
        }

    }
}

@Composable
fun TaskItem(task: Task, onClick: (Task) -> Unit, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp)
            .clickable { onClick(task) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        // Remove the horizontalScroll modifier here.
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(end = 16.dp)) {
                // Display Task details
                Text(
                    text = "Description: ${task.deskripsi}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Deadline: ${task.deadline}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Status: ${if (task.is_finish) "Completed" else "In Progress"}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Delete Button
                Button(
                    onClick = { onDelete(task.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = "Delete Task", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}


@Composable
fun UpdateProjectDialog(
    projectName: TextFieldValue,
    projectDescription: TextFieldValue,
    projectObject: TextFieldValue,
    isFinish: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (String, String, String, Boolean) -> Unit
) {
    // State to hold updated values
    var updatedProjectName by remember { mutableStateOf(projectName) }
    var updatedProjectDescription by remember { mutableStateOf(projectDescription) }
    var updatedProjectObject by remember { mutableStateOf(projectObject) }
    var isProjectFinished by remember { mutableStateOf(isFinish) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Project", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Project Name
                TextField(
                    value = updatedProjectName,
                    onValueChange = { updatedProjectName = it },
                    label = { Text("Project Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Project Description
                TextField(
                    value = updatedProjectDescription,
                    onValueChange = { updatedProjectDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Project Object
                TextField(
                    value = updatedProjectObject,
                    onValueChange = { updatedProjectObject = it },
                    label = { Text("Object") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium),
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                // Project Finished Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Project Finished", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isProjectFinished,
                        onCheckedChange = { isProjectFinished = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Pass updated values to the onUpdate function
                    onUpdate(
                        updatedProjectName.text,
                        updatedProjectDescription.text,
                        updatedProjectObject.text,
                        isProjectFinished
                    )
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            ) {
                Text("Update", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CollaboratorItem(collaborator: Collaborator) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Tambahkan jarak antar item
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),

        ) {
            // Nama Pengguna
            Text(
                text = "Nama: ${collaborator.User.nama}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 16.dp) // Berikan jarak antar elemen
            )

            // Email Pengguna
            Text(
                text = "Email: ${collaborator.User.email}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 16.dp) // Berikan jarak antar elemen
            )

            // Status Kolaborator
            Text(
                text = "Status: ${collaborator.status}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 16.dp) // Berikan jarak antar elemen
            )

            // Role (Owner/Collaborator)
            Text(
                text = "Role: ${if (collaborator.is_owner) "Owner" else "Collaborator"}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
            )
        }
    }
}




@Composable
fun AddCollaboratorsDialog(
    projectId: String,
    onDismiss: () -> Unit,
    onSave: (AddCollaboratorsRequest) -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    val emailList = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("Add Collaborators", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Input Field for Email
                TextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Enter Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Add Button
                Button(
                    onClick = {
                        if (emailInput.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                            emailList.add(emailInput)
                            emailInput = ""
                        } else {
                            Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Email")
                }

                // Display Email List
                Text(text = "Collaborator Emails:", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                ) {
                    items(emailList) { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (emailList.isNotEmpty()) {
                        // Create AddCollaboratorsRequest and pass to onSave
                        val request = AddCollaboratorsRequest(
                            project_id = projectId,
                            collaborators = emailList.toList()
                        )
                        onSave(request)
                        onDismiss()
                    } else {
                        Toast.makeText(context, "No emails to save.", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun ProjectDetailPreview() {
    val navController = rememberNavController()
    ProjectDetailScreen(navController = navController, projectId = "121")
}


