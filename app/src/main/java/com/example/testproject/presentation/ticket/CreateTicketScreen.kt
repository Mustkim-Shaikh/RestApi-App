package com.example.testproject.presentation.ticket


import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage // Make sure you have coil dependency
import com.example.testproject.R // Your drawable resources
import com.example.testproject.data.remote.RetrofitClient
import com.example.testproject.data.repository.AuthRepositoryImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(userName: String = "User") { // Pass user name if available
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val repository = AuthRepositoryImpl(RetrofitClient.api)

    val viewModel: CreateTicketViewModel = viewModel(
        factory = CreateTicketViewModelFactory(application, repository)
    )

    val ticketType by viewModel.ticketType.collectAsState()
    val message by viewModel.message.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val submissionMessage by viewModel.submissionMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    // Activity Result Launcher for picking image
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.onImageSelected(uri)
        }
    )

    // Show Toast for submission messages
    LaunchedEffect(submissionMessage) {
        submissionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (isSuccess) {
                // Optionally navigate back or clear form after success
                viewModel.clearSubmissionMessage() // Clear message after showing toast
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Support Ticket") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ticket Type Input
            OutlinedTextField(
                value = "67ab787870baa5efe5404d63",
                onValueChange = viewModel::onTicketTypeChange,
                label = { Text("Ticket Type (e.g., Complaint, Bug, Feature Request)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Message Input
            OutlinedTextField(
                value = message,
                onValueChange = viewModel::onMessageChange,
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Upload Image Button
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Image from Gallery")
            }

            // Image Preview
            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected Image Preview",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit // or ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Image Selected: ${uri.lastPathSegment ?: "unknown"}", style = MaterialTheme.typography.bodySmall)
            } ?: run {
                // Placeholder for no image selected
                Image(
                    painter = painterResource(id = R.drawable.image_placeholder), // Your placeholder drawable
                    contentDescription = "No image selected",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit,
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray.copy(alpha = 0.5f))
                )
            }


            Spacer(modifier = Modifier.weight(1f)) // Pushes content to top

            // Submit Button
            Button(
                onClick = { viewModel.submitTicket(userName) },
                enabled = !isLoading, // Disable button when loading
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit Ticket")
                }
            }
        }
    }
}