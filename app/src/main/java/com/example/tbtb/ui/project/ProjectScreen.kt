package com.example.tbtb.ui.project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.request.ProjectRequest
import com.example.tbtb.ui.common.BottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProjectScreen(navController: NavController) {
    val viewModel: ProjectViewModel = viewModel()
    val projects by viewModel.projects.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    // State for dialog visibility and project to delete
    var showDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    // Fetch projects when the screen is first displayed
    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.fetchProjects(token)
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add"){
                    popUpTo("dashboard") { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        },
        modifier = Modifier.statusBarsPadding(),
        bottomBar = {
            BottomNavigationBar(navController)

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F8FA))
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF7F8FA)),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }

                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF7F8FA))
                    ) {
                        Text(
                            text = "Projects",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF5DA89D),
                            modifier = Modifier.padding(16.dp) // Still keeping padding here for the header
                        )

                        // Tampilkan pesan error jika ada error
                        if (error != null) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                        }

                        // Tampilkan daftar proyek
                        ProjectListScreen(
                            projects = projects,
                            viewModel = viewModel,
                            token = token,
                            navController = navController,
                            onDeleteRequest = { project ->
                                projectToDelete = project
                                showDialog = true
                            }
                        )
                    }
                }

                // Handle delete confirmation dialog
                if (showDialog && projectToDelete != null) {
                    DeleteConfirmationDialog(
                        project = projectToDelete!!,
                        onConfirm = {
                            if (token != null) {
                                // Perform deletion
                                showDialog = false
                                viewModel.deleteProject(token, projectToDelete!!.id) { success ->
                                    if (success) {
                                        viewModel.fetchProjects(token)
                                    } else {
                                        // Handle failure, show message

                                        Toast.makeText(
                                            context,
                                            viewModel.projectDelete.value.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                            }
                        },
                        onDismiss = {
                            showDialog = false
                        }
                    )
                }

            }

        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    project: Project,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete the project '${project.namaProject}'?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}


@Composable
fun ProjectListScreen(
    projects: List<Project>,
    viewModel: ProjectViewModel,
    token: String?,
    navController: NavController,
    onDeleteRequest: (Project) -> Unit
) {
    if (projects.isEmpty()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFF7F8FA)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val imagePainter = painterResource(com.example.tbtb.R.drawable.ic_not_found)
                Image(
                    painter = imagePainter,
                    contentDescription = "Not Found",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = "Data project not found",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    } else {
        LazyColumn {
            items(projects) { project ->
                ProjectItem(
                    project = project,
                    onEdit = { navController.navigate("detail/${project.id}") },
                    onDelete = { onDeleteRequest(project) }
                )
            }
        }
    }
}



@Composable
fun ProjectItem(project: Project, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5DA89D)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = project.namaProject,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = project.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Project",
                    tint = Color.Red
                )
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProjectScreenPreview() {
    val navController = rememberNavController()
    ProjectScreen(navController = navController)
}
