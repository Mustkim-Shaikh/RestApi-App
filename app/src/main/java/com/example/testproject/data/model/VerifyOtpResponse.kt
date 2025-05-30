package com.example.testproject.data.model

data class VerifyOtpResponse(
    val success: Boolean,
    val message: String,
    val statusCode: Int,
    val data: OtpData? = null
)

data class OtpData(
    val isExistingUser: Boolean,
    val jwt: String?,
    val refreshToken: String?
)
