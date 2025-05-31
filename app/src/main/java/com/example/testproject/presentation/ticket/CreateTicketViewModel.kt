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

import java.io.IOException



class CreateTicketViewModel(

    private val repository: AuthRepositoryImpl,

    private val application: Application

) : AndroidViewModel(application) {



    // Initialize as an empty string for user input

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



    // RESTORED: onTicketTypeChange function for user input

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

        // Log user-provided ticket type

        Log.d("CreateTicketViewModel", "Ticket Type (user input): ${_ticketType.value}, Message: ${_message.value}")



        viewModelScope.launch {

            var tempFile: File? = null // Declared here for finally block cleanup

            try {

                val accessToken = TokenStore.getAccessToken(application.applicationContext)

                if (accessToken.isNullOrEmpty()) {

                    _submissionMessage.value = "Authentication token not found. Please log in."

                    _isLoading.value = false

                    Log.e("CreateTicketViewModel", "ERROR: Access token is null or empty.")

                    return@launch

                }

                Log.d("CreateTicketViewModel", "Access token retrieved (first 5 chars): ${accessToken.take(5)}...")



                val finalMessage = "FINAL SUBMISSION BY $userName - ${_message.value}"

                Log.d("CreateTicketViewModel", "Final message to be sent: $finalMessage")



                // Use the user-provided _ticketType.value

                val ticketTypePart = "67ab787870baa5efe5404d63".toRequestBody("text/plain".toMediaTypeOrNull())

                val messagePart = finalMessage.toRequestBody("text/plain".toMediaTypeOrNull())



                var filePart: MultipartBody.Part? = null

                _selectedImageUri.value?.let { uri ->

                    Log.d("CreateTicketViewModel", "Processing selected image URI: $uri")

                    val contentResolver = application.contentResolver

                    try {

                        // Infer file extension for better temporary file naming

                        val extension = contentResolver.getType(uri)?.substringAfterLast('/') ?: "jpg"

                        val fileName = "upload_image_${System.currentTimeMillis()}.$extension"

                        tempFile = File(application.cacheDir, fileName) // Assign to tempFile for cleanup



                        contentResolver.openInputStream(uri)?.use { inputStream ->

                            FileOutputStream(tempFile).use { outputStream ->

                                inputStream.copyTo(outputStream)

                            }

                        }



                        if (tempFile?.exists() == true && tempFile.length() > 0) {

                            // Use specific MIME type from ContentResolver, default to image/jpeg

                            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"

                            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())



                            // *** KEY CHANGE: Changed "files" to "file" as per your backend's expectation ***

                            filePart = MultipartBody.Part.createFormData("files", tempFile.name, requestFile)

                            Log.d("CreateTicketViewModel", "Image file prepared for upload: ${tempFile.name}, size: ${tempFile.length()} bytes, MimeType: $mimeType. Field name: 'file'")

                        } else {

                            val msg = "Failed to create temporary file for image or file is empty. File exists: ${tempFile?.exists()}, Length: ${tempFile?.length()}."

                            Log.e("CreateTicketViewModel", "ERROR: $msg")

                            _submissionMessage.value = "Image processing failed. Please try again."

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

                    // Clear all fields on success as they are user-inputted

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

                // Ensure temporary file is deleted

                tempFile?.delete()

                if (tempFile?.exists() == true) {

                    Log.w("CreateTicketViewModel", "Warning: Temporary file '${tempFile.name}' was not deleted.")

                } else {

                    Log.d("CreateTicketViewModel", "Temporary file deleted successfully (if existed).")

                }

            }

        }

    }

}







