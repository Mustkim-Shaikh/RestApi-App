package com.example.testproject.data.model

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T
)