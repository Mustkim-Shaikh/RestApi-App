package com.example.testproject.presentation.ticket

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.repository.AuthRepositoryImpl
import com.example.testproject.utils.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException // Import for specific exception handling

class CreateTicketViewModel(
    private val repository: AuthRepositoryImpl,
    private val application: Application
) : AndroidViewModel(application) {

    private val _ticketType = MutableStateFlow("")
    val ticketType: StateFlow<String> = _ticketType

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _submissionMessage = MutableStateFlow<String?>(null)
    val submissionMessage: StateFlow<String?> = _submissionMessage

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    fun onTicketTypeChange(newTicketType: String) {
        _ticketType.value = newTicketType
        Log.d("CreateTicketViewModel", "Ticket type changed to: $newTicketType") // Log input changes
    }

    fun onMessageChange(newMessage: String) {
        _message.value = newMessage
        Log.d("CreateTicketViewModel", "Message changed to: $newMessage") // Log input changes
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
        if (uri != null) {
            Log.d("CreateTicketViewModel", "Image selected. URI: $uri, Path: ${uri.path}")
        } else {
            Log.d("CreateTicketViewModel", "Image selection cleared (URI is null).")
        }
    }

    fun clearSubmissionMessage() {
        _submissionMessage.value = null
        _isSuccess.value = false
        Log.d("CreateTicketViewModel", "Submission message and success status cleared.")
    }

    fun submitTicket(userName: String) {
        _isLoading.value = true
        _submissionMessage.value = null
        _isSuccess.value = false
        Log.d("CreateTicketViewModel", "Attempting to submit ticket for user: $userName")
        Log.d("CreateTicketViewModel", "Ticket Type: ${_ticketType.value}, Message: ${_message.value}")


        viewModelScope.launch {
            try {
                val accessToken = TokenStore.getAccessToken(application.applicationContext)
                if (accessToken.isNullOrEmpty()) {
                    _submissionMessage.value = "Authentication token not found. Please log in."
                    _isLoading.value = false
                    Log.e("CreateTicketViewModel", "ERROR: Access token is null or empty.") // Log specific error
                    return@launch
                }
                Log.d("CreateTicketViewModel", "Access token retrieved (first 5 chars): ${accessToken.take(5)}...") // Log token snippet

                val finalMessage = "FINAL SUBMISSION BY $userName - ${_message.value}"
                Log.d("CreateTicketViewModel", "Final message to be sent: $finalMessage")

                val hardcodeTickitType = "67ab787870baa5efe5404d63"
                val ticketTypePart = hardcodeTickitType.toRequestBody("text/plain".toMediaTypeOrNull())
                val messagePart = finalMessage.toRequestBody("text/plain".toMediaTypeOrNull())

                var filePart: MultipartBody.Part? = null
                _selectedImageUri.value?.let { uri ->
                    Log.d("CreateTicketViewModel", "Processing selected image URI: $uri")
                    val contentResolver = application.contentResolver
                    var file: File? = null
                    try {
                        // Create a temporary file
                        file = File(application.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(file).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        if (file?.exists() == true && file.length() > 0) {
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            filePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
                            Log.d("CreateTicketViewModel", "Image file prepared for upload: ${file.name}, size: ${file.length()} bytes")
                        } else {
                            val msg = "Failed to create temporary file for image or file is empty. File exists: ${file?.exists()}, Length: ${file?.length()}."
                            Log.e("CreateTicketViewModel", "ERROR: $msg")
                            _submissionMessage.value = "Image processing failed. Please try again."
                            // Don't return, allow submission without image if main fields are present
                        }
                    } catch (e: IOException) {
                        Log.e("CreateTicketViewModel", "IOException during image file processing: ${e.localizedMessage}", e)
                        _submissionMessage.value = "Failed to read image file: ${e.localizedMessage}"
                    } catch (e: SecurityException) {
                        Log.e("CreateTicketViewModel", "SecurityException (permission issue) during image file processing: ${e.localizedMessage}", e)
                        _submissionMessage.value = "Permission denied to read image file. Please grant storage permission."
                    } catch (e: Exception) {
                        Log.e("CreateTicketViewModel", "GENERIC EXCEPTION during image file processing: ${e.localizedMessage}", e)
                        _submissionMessage.value = "An unexpected error occurred while processing the image: ${e.localizedMessage}"
                    } finally {
                        // Consider deleting the temporary file here after the request is made,
                        // or within an OkHttp interceptor if you have one.
                        // For now, it will remain in cache until app closure or system cleanup.
                    }
                }

                Log.d("CreateTicketViewModel", "Sending ticket creation request...")
                val response = repository.createTicket(
                    token = "Bearer $accessToken",
                    ticketType = ticketTypePart,
                    message = messagePart,
                    filePart = filePart
                )
                Log.d("CreateTicketViewModel", "API Response received. Code: ${response.code()}")


                if (response.isSuccessful) {
                    _submissionMessage.value = "Ticket created successfully!"
                    _isSuccess.value = true
                    val successBody = response.body()?.toString() ?: "No body"
                    Log.d("CreateTicketViewModel", "Ticket creation success: $successBody")
                    // Optionally clear fields on success
                    _ticketType.value = ""
                    _message.value = ""
                    _selectedImageUri.value = null
                    Log.d("CreateTicketViewModel", "Form fields cleared after successful submission.")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _submissionMessage.value = "Failed to create ticket: ${response.code()} - ${errorBody ?: "Unknown error"}"
                    Log.e("CreateTicketViewModel", "ERROR: Ticket creation failed: ${response.code()} - ${errorBody ?: "No error body"}")
                }
            } catch (e: Exception) {
                _submissionMessage.value = "Network error: ${e.localizedMessage ?: "Unknown network error"}"
                Log.e("CreateTicketViewModel", "FATAL ERROR: Exception during ticket submission process: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
                Log.d("CreateTicketViewModel", "Submission process finished. isLoading set to false.")
            }
        }
    }
}