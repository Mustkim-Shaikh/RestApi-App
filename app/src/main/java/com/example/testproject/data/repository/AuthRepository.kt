package com.example.testproject.data.repository

import com.example.testproject.data.model.ApiResponse
import com.example.testproject.data.model.ProductResponse
import com.example.testproject.data.model.SendOtpRequest
import com.example.testproject.data.model.SendOtpResponse
import com.example.testproject.data.model.VerifyOtpRequest
import com.example.testproject.data.model.VerifyOtpResponse
import com.example.testproject.data.remote.AuthApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

interface AuthRepository {
    suspend fun sendOtp(request: SendOtpRequest): Response<SendOtpResponse>


    suspend fun verifyOtp(request: VerifyOtpRequest): Response<VerifyOtpResponse>

    suspend fun getProducts(token: String): Response<ProductResponse>

    // New: Function to create a ticket
    // New: Function to create a ticket
    suspend fun createTicket(
        token: String,
        ticketType: RequestBody,
        message: RequestBody,
        filePart: MultipartBody.Part?
    ): Response<ApiResponse<Any>>



}
