package com.example.tbtb.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbtb.data.response.UserResponse
import com.example.tbtb.ui.profile.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.tbtb.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val profileViewModel: ProfileViewModel = viewModel()

    val context = LocalContext.current

    // Observing the user data and loading state
    val currentUser by profileViewModel.currentUser.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val errorMessage by profileViewModel.errorMessage.collectAsState()

    var photoFile by remember { mutableStateOf<File?>(null) }
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val token = sharedPreferences.getString("user_token", "") ?: ""
    // Show error message if there's an issue fetching the user data
    LaunchedEffect(Unit) {
        profileViewModel.getCurrentUser(token)
    }

    var showDialog by remember { mutableStateOf(false) }

    // Launchers for Gallery and Camera
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = FileUtils.getFileFromUri(context, it)
            photoFile = file
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
            file.outputStream().use { outStream ->
                it.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            }
            photoFile = file
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    if (!errorMessage.isNullOrEmpty()){
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        return
    }

    if (currentUser != null) {
        var email by remember { mutableStateOf(currentUser?.data?.email ?: "") }
        var nama by remember { mutableStateOf(currentUser?.data?.nama ?: "") }
        var password by remember { mutableStateOf("") }
        val photoUrl by remember { mutableStateOf(currentUser?.data?.photo_url ?: "") }        // Top Bar with Back Button
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Background color
                        titleContentColor = Color.White
                    ),
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                ) {
                    // Profile Image Section
                    Box(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        // Profile Circle
                        Surface(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape),
                            color = Color(0xFF78909C) // Gray-blue color like in image
                        ) {
                            AsyncImage(
                                model = if (photoFile != null) photoFile?.absolutePath else photoUrl,
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                placeholder = painterResource(id = R.drawable.ic_profile),
                                error = painterResource(id = R.drawable.ic_broken),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Camera Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset((-8).dp, (-8).dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { showDialog = true }
                                .background(Color(0xFF4CAF50)) // Green color like in image
                                .padding(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = "Change photo",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Select Image") },
                            text = { Text("Choose an option to add a profile picture.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    galleryLauncher.launch("image/*")
                                }) {
                                    Text("Gallery")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDialog = false
                                    cameraLauncher.launch(null)
                                }) {
                                    Text("Camera")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Input Field
                    InputField(
                        label = "New Name",
                        value = nama,
                        onValueChange = { nama = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input Field
                    InputField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputField(
                        label = "New Password",
                        value = password,
                        onValueChange = { password = it}
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val changes = mutableMapOf<String, String>()
                            if (nama != currentUser?.data?.nama) changes["nama"] = nama
                            if (email != currentUser?.data?.email) changes["email"] = email
                            if (password.isNotEmpty()) changes["password"] = password
                            val photoPart = photoFile?.let {
                                val mimeType = when (it.extension.lowercase()) {
                                    "jpg" -> "image/jpg"
                                    "jpeg" -> "image/jpeg"
                                    "png" -> "image/png"
                                    else -> throw IllegalArgumentException("Unsupported file type")
                                }
                                val photoRequestBody = RequestBody.create(mimeType.toMediaTypeOrNull(), it)
                                MultipartBody.Part.createFormData("photo_url", it.name, photoRequestBody)
                            }

                            if (photoPart != null || changes.isNotEmpty()) {

                                profileViewModel.updateUser(
                                    token = token,
                                    nama = changes["nama"],
                                    email = changes["email"],
                                    password = changes["password"],
                                    photo_url = photoPart
                                ) { success ->
                                    if (success) {
                                        navController.popBackStack()
                                        Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else {
                                Toast.makeText(context, "No changes made.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Changes", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp))
                    }

                }
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        file.outputStream().use { inputStream?.copyTo(it) }
        return file
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(navController = rememberNavController())
}
