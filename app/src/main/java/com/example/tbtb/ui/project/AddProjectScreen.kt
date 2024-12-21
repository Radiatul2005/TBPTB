package com.example.tbtb.ui.project

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.request.ProjectRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(navController: NavController) {
    val viewModel: ProjectViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Initialize mutable state for project fields
    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var projectObjectType by remember { mutableStateOf("") }
    val collaborators = remember { mutableStateListOf<String>() }

    // Initialize error state for each field
    var projectNameError by remember { mutableStateOf<String?>(null) }
    var projectDescriptionError by remember { mutableStateOf<String?>(null) }
    var projectObjectTypeError by remember { mutableStateOf<String?>(null) }

    // Retrieve token from SharedPreferences
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    // Show loading indicator while adding the project
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
    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        return
    }

    // Add Project Form with Custom Background Color
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA)), // Set the background to #F7F8FA
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Add Project", color = Color.White) },
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
            // Project Name Field
            TextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text(text = "Project Name") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = projectNameError != null
            )
            // Project Name Error Text
            projectNameError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Project Description Field
            TextField(
                value = projectDescription,
                onValueChange = { projectDescription = it },
                label = { Text("Project Description") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = projectDescriptionError != null
            )
            // Project Description Error Text
            projectDescriptionError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Object Type Field
            TextField(
                value = projectObjectType,
                onValueChange = { projectObjectType = it },
                label = { Text("Object Type") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = projectObjectTypeError != null
            )
            // Object Type Error Text
            projectObjectTypeError?.let {
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
                    projectNameError = null
                    projectDescriptionError = null
                    projectObjectTypeError = null

                    // Validate each field
                    if (projectName.isEmpty()) {
                        projectNameError = "Project Name is required."
                        isValid = false
                    }

                    if (projectDescription.isEmpty()) {
                        projectDescriptionError = "Project Description is required."
                        isValid = false
                    } else if (projectDescription.length < 10) {
                        projectDescriptionError = "Project Description must be at least 10 characters."
                        isValid = false
                    }

                    if (projectObjectType.isEmpty()) {
                        projectObjectTypeError = "Object Type is required."
                        isValid = false
                    }

                    // If valid, proceed to add project
                    if (isValid && token != null) {
                        // Create a project request object with empty collaborators list
                        val request = ProjectRequest(
                            nama_project = projectName,
                            deskripsi = projectDescription,
                            object_type = projectObjectType,
                            collaborators = collaborators // Empty list for collaborators
                        )

                        viewModel.addProject(token, request) {
                            navController.popBackStack()
                        }


                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Project")
            }
        }
    }
}



