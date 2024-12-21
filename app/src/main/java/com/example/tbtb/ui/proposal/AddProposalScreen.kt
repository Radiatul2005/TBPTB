package com.example.tbtb.ui.proposal

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import okhttp3.MultipartBody
import com.example.tbtb.data.model.Proposal
import com.example.tbtb.data.repository.ProposalRepository
import com.example.tbtb.ui.theme.TBTBTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProposalScreen(navController: NavController, projectId: String) {
    val viewModel: ProposalViewModel = viewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val operationResult by viewModel.operationResult.collectAsState()
    val error by viewModel.error.collectAsState()
    val serverError by viewModel.serverError.collectAsState()

    var proposalTitle by remember { mutableStateOf("") }
    var proposalDescription by remember { mutableStateOf("") }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfFileName by remember { mutableStateOf<String?>(null) }

    var proposalTitleError by remember { mutableStateOf<String?>(null) }
    var proposalDescriptionError by remember { mutableStateOf<String?>(null) }
    var pdfFileError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    val pickPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            pdfUri = uri
            pdfFileName = uri?.let { context.getFileName(it) }
        }
    )

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA)),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }
    LaunchedEffect(serverError) {
        serverError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.setServerError(null)
        }
    }

    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Add Proposal", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
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
            TextField(
                value = proposalTitle,
                onValueChange = { proposalTitle = it },
                label = { Text("Proposal Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                isError = proposalTitleError != null
            )
            proposalTitleError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            TextField(
                value = proposalDescription,
                onValueChange = { proposalDescription = it },
                label = { Text("Proposal Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                isError = proposalDescriptionError != null
            )
            proposalDescriptionError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Button(
                onClick = { pickPdfLauncher.launch("application/pdf") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Select Proposal PDF")
            }

            pdfFileName?.let {
                Text(
                    text = "Selected File: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            pdfFileError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    proposalTitleError = null
                    proposalDescriptionError = null
                    pdfFileError = null

                    if (proposalTitle.isEmpty()) {
                        proposalTitleError = "Proposal Title is required."
                        isValid = false
                    }

                    if (proposalDescription.isEmpty()) {
                        proposalDescriptionError = "Proposal Description is required."
                        isValid = false
                    } else if (proposalDescription.length < 10) {
                        proposalDescriptionError = "Description must be at least 10 characters."
                        isValid = false
                    }

                    if (pdfUri == null) {
                        pdfFileError = "PDF file is required."
                        isValid = false
                    }

                    if (isValid) {
                        val filePart = pdfUri?.let { uri ->
                            val file = uri.toFile(context)
                            MultipartBody.Part.createFormData(
                                "file_url",
                                file.name,
                                RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
                            )
                        }

                        if (token != null && filePart != null) {
                            viewModel.createProposal(
                                token = token,
                                projectId = projectId,
                                title = proposalTitle,
                                description = proposalDescription,
                                pdfFile = filePart
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "Proposal created successfully", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                else {
                                    Toast.makeText(context, "Failed to create proposal", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            }
                        } else {
                            Toast.makeText(context, "File or token is missing", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Submit Proposal")
            }
        }
    }
}

fun Uri.toFile(context: Context): File {
    val inputStream = context.contentResolver.openInputStream(this)
    val tempFile = context.getFileName(this)?.let { File(context.cacheDir, it) }
    tempFile?.outputStream()?.use { output ->
        inputStream?.copyTo(output)
    }
    return tempFile!!
}


fun Context.getFileName(uri: Uri): String? {
    var name: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            name = it.getString(columnIndex)
        }
    }
    return name
}

@Preview(showBackground = true)
@Composable
fun AddProposalScreenPreview() {
    TBTBTheme {
        AddProposalScreen(navController = NavController(LocalContext.current), projectId = "123")
    }
}
