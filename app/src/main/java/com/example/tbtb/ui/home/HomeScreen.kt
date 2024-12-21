package com.example.tbtb.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.tbtb.R
import com.example.tbtb.data.model.Project
import com.example.tbtb.data.response.UserResponse
import com.example.tbtb.ui.common.BottomNavigationBar
import com.example.tbtb.ui.common.Screen
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    val viewmodel: HomeViewmodel = viewModel()
    val currentUser by viewmodel.currentUser.collectAsState()
    val projects by viewmodel.projects.collectAsState()
    val isLoading by viewmodel.isLoading.collectAsState()
    val isError by viewmodel.errorMessage.collectAsState()
    val shouldLogout by viewmodel.shouldLogout.collectAsState()
    val message by viewmodel.message.collectAsState()


    LaunchedEffect(Unit) {
        if (token != null) {
            viewmodel.getCurrentUser(token)
            Log.d("MessageTest", message.toString())
            viewmodel.fetchAllProjects(token)

        }
    }

    // Penanganan Loading dan Error
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F8FA)),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }

        }
        isError != null -> {
            if (message != null){
                Log.d("MessageHome", message.toString())
                if (message.toString() == "Invalid or expired token"){
                    navController.navigate("login") {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                    Toast.makeText(context, "Session Expired", Toast.LENGTH_SHORT).show()
                }
            }
        }

        else -> {
            Scaffold(
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
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF7F8FA))
                            .padding(16.dp)
                    ){
                        HeaderSection(currentUser)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tanggal Horizontal
                        DateSection()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Current Project - Horizontal List
                        Text(
                            text = "Current Project",
                            fontSize = 18.sp,
                            color = Color(0xFF5DA89D),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalProjectsList(
                            projects.sortedByDescending { project ->
                                try {
                                    OffsetDateTime.parse(project.createdAt)
                                } catch (e: Exception) {
                                    OffsetDateTime.MIN
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }


                }

            }
        }

    }
}

@Composable
fun HeaderSection(currentUser: UserResponse?) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Check if currentUser is available, then show the profile data
            if (currentUser != null) {
                val profileImageUrl = currentUser.data?.photo_url // Default profile image

                if (profileImageUrl == null) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }else{
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.ic_profile),
                        error = painterResource(id = R.drawable.ic_broken),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 14.sp,
                        color = Color(0xFF5DA89D)
                    )
                    Text(
                        text = "Hello, ${currentUser.data?.nama ?: "Guest"}!", // Nama pengguna atau fallback "Guest"
                        fontSize = 20.sp,
                        color = Color(0xFF5DA89D),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFF7F8FA)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

        }
        IconButton(onClick = { /* TODO: Notification Click */ }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                tint = Color(0xFF5DA89D)

            )
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun DateSection() {
    val currentDate = LocalDate.now()
    val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val dayFormatter = DateTimeFormatter.ofPattern("EEE") // Format nama hari

    val currentMonthYear = currentDate.format(monthYearFormatter)
    val daysInMonth = currentDate.lengthOfMonth() // Jumlah hari dalam bulan ini

    // List tanggal dan hari dalam bulan ini
    val dates = (1..daysInMonth).map { day ->
        val date = currentDate.withDayOfMonth(day)
        Pair(day.toString(), date.format(dayFormatter)) // Pair(Tanggal, Hari)
    }

    val currentDayIndex = dates.indexOfFirst { it.first == currentDate.dayOfMonth.toString() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Menampilkan Bulan dan Tahun
        Text(
            text = currentMonthYear,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tanggal Horizontal dengan LazyRow dan Scroll Otomatis
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = rememberLazyListState(initialFirstVisibleItemIndex = currentDayIndex), // Fokus pada tanggal saat ini
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dates) { (date, day) ->
                DateItem(
                    date = date,
                    day = day,
                    isSelected = date == currentDate.dayOfMonth.toString()
                )
            }
        }
    }
}

@Composable
fun DateItem(date: String, day: String, isSelected: Boolean) {
    Column(
        modifier = Modifier
            .size(64.dp)
            .background(
                if (isSelected) Color(0xFF5DA89D) else Color.White, // Warna untuk tanggal saat ini
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.uppercase(),
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Text(
            text = date,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun HorizontalProjectsList(projects: List<Project>) {
    if (projects.isNotEmpty()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(projects) { project ->
                ProjectCard(
                    title = project.namaProject,
                    desc = project.deskripsi, // Pastikan deskripsi ada di data model
                    objectType = project.objectType
                )
            }
        }
    } else {
        Text(
            text = "Tidak ada proyek terbaru",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun ProjectCard(title: String, desc: String, objectType: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5DA89D)),
        modifier = Modifier
            .width(180.dp) // Ukuran diperbesar untuk ruang tambahan
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Bagian Atas: Judul dan Lingkaran Warna
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .size(34.dp)
                        .padding(end = 8.dp)
                ) {
                    drawCircle(color = Color(0xFFFFCB63))
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f) // Supaya judul tidak overflow
                )
            }

            // Bagian Tengah: Deskripsi
            Text(
                text = desc,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                lineHeight = 16.sp
            )

            // Bagian Bawah: Object Type
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = objectType,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp) // Label kecil
                )

            }

        }

    }
}

@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}