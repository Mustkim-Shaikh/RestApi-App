package com.example.testproject.data.remote

import com.example.testproject.data.model.ApiResponse
import com.example.testproject.data.model.ProductResponse
import com.example.testproject.data.model.SendOtpRequest
import com.example.testproject.data.model.SendOtpResponse
import com.example.testproject.data.model.VerifyOtpRequest
import com.example.testproject.data.model.VerifyOtpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApi {
    @POST("api/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @GET("api/product") // Adjust endpoint if needed
    suspend fun getProducts(@Header("Authorization") token: String): Response<ProductResponse>

    // New: Ticket Creation Endpoint
    @Multipart // Declare this as a multipart request
    @POST("api/ticket")
    suspend fun createTicket(
        @Header("Authorization") token: String,
        @Part("ticketType") ticketType: RequestBody, // Text part
        @Part("message") message: RequestBody,       // Text part
        @Part files: MultipartBody.Part?             // Optional file part
    ): Response<ApiResponse<Any>> // Assuming a generic success response
}







object RetrofitClient {
    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.29041989.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
