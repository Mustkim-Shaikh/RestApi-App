package com.example.testproject.data.model

data class VerifyOtpRequest(
    val phoneNumber: String,
    val countryCode: String,
    val otp: String
)
